package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.in.insight.subject.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithKitLanguage;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.allLevels;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitSubjectInsightServiceTest {

    @InjectMocks
    private InitSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Captor
    private ArgumentCaptor<SubjectInsight> subjectInsightArgumentCaptor;

    private final AssessmentResult assessmentResult = validResult();
    private final SubjectValue subjectValue = SubjectValueMother.createSubjectValue();
    private final InitSubjectInsightUseCase.Param param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

    @Test
    void testInitSubjectInsight_whenCurrentUserDoesNotHaveRequiredPermission_throwAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = Assertions.assertThrows(AccessDeniedException.class, () -> service.initSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenAssessmentResultOfRequestedAssessmentNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initSubjectInsight(param));
        assertEquals(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadSubjectValuePort,
            loadMaturityLevelsPort,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectInsightExists_thenUpdateSubjectInsight() {
        var subjectInsight = SubjectInsightMother.subjectInsight();
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.of(subjectInsight));
        when(loadSubjectValuePort.load(param.getSubjectId(), assessmentResult.getId()))
            .thenReturn(subjectValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(allLevels());

        service.initSubjectInsight(param);

        verify(updateSubjectInsightPort).update(subjectInsightArgumentCaptor.capture());
        SubjectInsight capturedSubjectInsight = subjectInsightArgumentCaptor.getValue();
        assertNotNull(capturedSubjectInsight);
        assertEquals(assessmentResult.getId(), capturedSubjectInsight.getAssessmentResultId());
        assertEquals(param.getSubjectId(), capturedSubjectInsight.getSubjectId());
        assertEquals(expectedDefaultInsight(locale), capturedSubjectInsight.getInsight());
        assertNull(capturedSubjectInsight.getInsightBy());
        assertNotNull(capturedSubjectInsight.getInsightTime());
        assertFalse(capturedSubjectInsight.isApproved());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectInsightDoesNotExist_thenCreateDefaultSubjectInsight() {
        var assessmentResultWithPersianKit = validResultWithKitLanguage(KitLanguage.FA);
        var locale = Locale.of(assessmentResultWithPersianKit.getAssessment().getAssessmentKit().getLanguage().getCode());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResultWithPersianKit));
        when(loadSubjectInsightPort.load(assessmentResultWithPersianKit.getId(), param.getSubjectId())).thenReturn(Optional.empty());
        when(loadSubjectValuePort.load(param.getSubjectId(), assessmentResultWithPersianKit.getId())).thenReturn(subjectValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResultWithPersianKit.getKitVersionId())).thenReturn(allLevels());

        service.initSubjectInsight(param);

        verify(createSubjectInsightPort).persist(subjectInsightArgumentCaptor.capture());
        SubjectInsight subjectInsight = subjectInsightArgumentCaptor.getValue();
        assertNotNull(subjectInsight);
        assertEquals(assessmentResultWithPersianKit.getId(), subjectInsight.getAssessmentResultId());
        assertEquals(param.getSubjectId(), subjectInsight.getSubjectId());
        assertEquals(expectedDefaultInsight(locale), subjectInsight.getInsight());
        assertNull(subjectInsight.getInsightBy());
        assertNotNull(subjectInsight.getInsightTime());
        assertNotNull(subjectInsight.getLastModificationTime());
        assertFalse(subjectInsight.isApproved());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(updateSubjectInsightPort);
    }

    private @NotNull String expectedDefaultInsight(Locale locale) {
        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            locale,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            subjectValue.getConfidenceValue() != null ? (int) Math.ceil(subjectValue.getConfidenceValue()) : 0,
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            allLevels().size(),
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getAttributeValues().size(),
            subjectValue.getSubject().getTitle());
    }

    private InitSubjectInsightUseCase.Param createParam(Consumer<InitSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InitSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitSubjectInsightUseCase.Param.builder()
            .assessmentId(assessmentResult.getAssessment().getId())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
