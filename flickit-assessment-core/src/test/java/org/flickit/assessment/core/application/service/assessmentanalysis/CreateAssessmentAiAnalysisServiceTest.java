package org.flickit.assessment.core.application.service.assessmentanalysis;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAiAnalysisUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_AI_ANALYSIS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ANALYSIS_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAiAnalysisServiceTest {

    @InjectMocks
    CreateAssessmentAiAnalysisService service;

    @Mock
    AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;

    @Test
    void testCreateAssessmentAnalysis_CurrentUserDoesNotHavePermission_ShouldReturnAccessDeniedException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var type = 1;
        var param = new CreateAssessmentAiAnalysisUseCase.Param(assessmentId, type, currentUserId);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAiAnalysis(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAssessmentAnalysis_AssessmentResultNotFound_ShouldReturnResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var type = 1;
        var param = new CreateAssessmentAiAnalysisUseCase.Param(assessmentId, type, currentUserId);

        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiAnalysis(param));

        assertEquals(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testCreateAssessmentAnalysis_AssessmentAnalysisNotFound_ShouldReturnResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var type = 1;
        var param = new CreateAssessmentAiAnalysisUseCase.Param(assessmentId, type, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();


        when(assessmentPermissionChecker.isAuthorized(assessmentId, currentUserId, CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), param.getType())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiAnalysis(param));

        assertEquals(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ANALYSIS_NOT_FOUND, throwable.getMessage());
    }
}
