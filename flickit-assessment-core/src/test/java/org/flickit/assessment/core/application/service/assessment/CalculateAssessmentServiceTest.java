package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CALCULATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.toBeCalcAsLevelFourWithWeight;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.toBeCalcAsLevelThreeWithWeight;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withQAValues;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withQAValuesAndSubjectWithQAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateAssessmentServiceTest {

    @InjectMocks
    private CalculateAssessmentService service;

    @Mock
    private LoadCalculateInfoPort loadCalculateInfoPort;

    @Mock
    private UpdateCalculatedResultPort updateCalculatedResultPort;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateAttributeValuePort createAttributeValuePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testCalculateMaturityLevel_ValidInput_ValidResults() {
        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();

        List<AttributeValue> s1AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelThreeWithWeight(3),
            toBeCalcAsLevelThreeWithWeight(3)
        );

        List<AttributeValue> s2AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(4),
            toBeCalcAsLevelThreeWithWeight(1)
        );

        List<SubjectValue> subjectValues = List.of(
            withQAValues(s1AttributeValues),
            withQAValues(s2AttributeValues)
        );

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        UUID currentUserId = UUID.randomUUID();

        CalculateAssessmentUseCase.Param param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId(), currentUserId);

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(kitLastMajorModificationTime);

        CalculateAssessmentUseCase.Result result = service.calculateMaturityLevel(param);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(4, result.maturityLevel().getValue());
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        List<AttributeValue> s1AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelThreeWithWeight(3),
            toBeCalcAsLevelThreeWithWeight(3)
        );

        List<AttributeValue> s2AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(4),
            toBeCalcAsLevelThreeWithWeight(1)
        );

        List<SubjectValue> subjectValues = List.of(
            withQAValuesAndSubjectWithQAs(s1AttributeValues, s1AttributeValues.stream().map(AttributeValue::getAttribute).toList()),
            withQAValuesAndSubjectWithQAs(s2AttributeValues, s2AttributeValues.stream().map(AttributeValue::getAttribute).toList())
        );

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = toBeCalcAsLevelFourWithWeight(4);
        var newSubjectValue = withQAValuesAndSubjectWithQAs(List.of(newAttributeValue), List.of(newAttributeValue.getAttribute()));
        subjects.add(newSubjectValue.getSubject());

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        UUID currentUserId = UUID.randomUUID();

        CalculateAssessmentUseCase.Param param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId(), currentUserId);

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anyList(), any())).thenReturn(List.of(newAttributeValue));

        CalculateAssessmentUseCase.Result result = service.calculateMaturityLevel(param);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(4, result.maturityLevel().getValue());
    }

    @Test
    void testCalculateMaturityLevel_CurrentUserDoesNotHaveAccessToAssessment_ThrowsAccessDeniedException() {
        var param = new CalculateAssessmentUseCase.Param(UUID.randomUUID(), UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(false);
        verifyNoInteractions(loadCalculateInfoPort);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.calculateMaturityLevel(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
