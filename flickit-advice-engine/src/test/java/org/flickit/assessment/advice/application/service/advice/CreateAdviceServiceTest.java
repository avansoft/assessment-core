package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.SneakyThrows;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.advice.*;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentSpacePort;
import org.flickit.assessment.advice.application.port.out.question.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.advice.application.port.out.question.LoadCreatedAdviceDetailsPort.Result;
import org.flickit.assessment.advice.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.UUID.randomUUID;
import static org.flickit.assessment.advice.application.service.QuestionMother.createQuestionWithTargetAndCurrentOption;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdviceServiceTest {

    @InjectMocks
    private CreateAdviceService service;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentSpacePort loadAssessmentSpacePort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private LoadAdviceCalculationInfoPort loadInfoPort;

    @Mock
    private SolverManager<Plan, UUID> solverManager;

    @Mock
    private LoadCreatedAdviceDetailsPort loadCreatedAdviceDetailsPort;

    @Test
    void testCreateAdvice_AssessmentNotExist_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createAdvice(param), CREATE_ADVICE_ASSESSMENT_NOT_FOUND);
        Mockito.verifyNoInteractions(
            checkSpaceAccessPort,
            validateAssessmentResultPort,
            loadInfoPort,
            solverManager,
            loadCreatedAdviceDetailsPort
        );
    }

    @Test
    void testCreateAdvice_UserHasNotAccessToAssessment_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        var spaceId = 5L;
        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.of(spaceId));

        when(checkSpaceAccessPort.checkIsMember(spaceId, param.getCurrentUserId()))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.createAdvice(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(checkSpaceAccessPort, times(1)).checkIsMember(spaceId, param.getCurrentUserId());
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadCreatedAdviceDetailsPort
        );
    }

    @Test
    void testCreateAdvice_AssessmentCalculateIsNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        var spaceId = 5L;
        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.of(spaceId));

        when(checkSpaceAccessPort.checkIsMember(spaceId, param.getCurrentUserId()))
            .thenReturn(true);

        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        assertThrows(CalculateNotValidException.class, () -> service.createAdvice(param), CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(checkSpaceAccessPort, times(1)).checkIsMember(spaceId, param.getCurrentUserId());
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadCreatedAdviceDetailsPort
        );
    }

    @Test
    void testCreateAdvice_ConfidenceCalculateIsNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        var spaceId = 5L;
        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.of(spaceId));

        when(checkSpaceAccessPort.checkIsMember(spaceId, param.getCurrentUserId()))
            .thenReturn(true);

        doThrow(new ConfidenceCalculationNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.createAdvice(param), CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);

        verify(loadAssessmentSpacePort, times(1)).loadAssessmentSpaceId(param.getAssessmentId());
        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(checkSpaceAccessPort, times(1)).checkIsMember(spaceId, param.getCurrentUserId());
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadCreatedAdviceDetailsPort
        );
    }

    @SneakyThrows
    @Test
    void testCreateAdvice_CalculationInterrupted_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        var spaceId = 5L;
        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.of(spaceId));
        when(checkSpaceAccessPort.checkIsMember(spaceId, param.getCurrentUserId()))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);
        var problem = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        when(solverJob.getFinalBestSolution()).thenThrow(new InterruptedException());

        assertThrows(FinalSolutionNotFoundException.class, () -> service.createAdvice(param), CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(checkSpaceAccessPort, times(1)).checkIsMember(spaceId, param.getCurrentUserId());
//        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        verify(solverManager, times(1)).solve(any(), any());
        Mockito.verifyNoInteractions(
            loadCreatedAdviceDetailsPort
        );
    }
    @SneakyThrows
    @Test
    void testCreateAdvice_CalculationExecutionException_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        var spaceId = 5L;
        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.of(spaceId));

        when(checkSpaceAccessPort.checkIsMember(spaceId, param.getCurrentUserId()))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);
        var problem = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        when(solverJob.getFinalBestSolution()).thenThrow(new ExecutionException("", null));

        assertThrows(FinalSolutionNotFoundException.class, () -> service.createAdvice(param), CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(checkSpaceAccessPort, times(1)).checkIsMember(spaceId, param.getCurrentUserId());
//        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        verify(solverManager, times(1)).solve(any(), any());
        Mockito.verifyNoInteractions(
            loadCreatedAdviceDetailsPort
        );
    }

    @SneakyThrows
    @Test
    void testCreateAdvice_ValidParam_ReturnsAdvice() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        var param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );
        var spaceId = 5L;

        mockPorts(param, spaceId);

        var result = service.createAdvice(param);

        for (AdviceListItem question : result.adviceItems()) {
            assertNotNull(question.recommendedOption());
            assertNotNull(question.attributes());
            assertNotNull(question.questionnaire());
            assertFalse(question.question().title().isBlank());
            assertNotEquals(0, question.benefit());
        }

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(checkSpaceAccessPort, times(1)).checkIsMember(spaceId, param.getCurrentUserId());
//        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        verify(solverManager, times(1)).solve(any(), any());
        verify(loadCreatedAdviceDetailsPort, times(1)).loadAdviceDetails(any());
    }

    private void mockPorts(CreateAdviceUseCase.Param param, Long spaceId) throws InterruptedException, ExecutionException {
        when(loadAssessmentSpacePort.loadAssessmentSpaceId(param.getAssessmentId()))
            .thenReturn(Optional.of(spaceId));
        when(checkSpaceAccessPort.checkIsMember(spaceId, param.getCurrentUserId()))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);
        var problem = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        question1.setRecommendedOptionIndex(3);
        question2.setRecommendedOptionIndex(3);

        var solution = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(solverJob.getFinalBestSolution()).thenReturn(solution);

        var adviceQuestion1 = new AdviceQuestion(question1.getId(), "what?", 12);
        var questionnaire = new AdviceQuestionnaire(15L, "Dev ops");
        var attribute = new AdviceAttribute(216L, "Software Efficiency");
        var optionListItems1 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var questionsPortResult1 = new Result(adviceQuestion1, optionListItems1, List.of(attribute), questionnaire);

        var adviceQuestion2 = new AdviceQuestion(question2.getId(), "what?", 15);
        var optionListItems2 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var questionsPortResult2 = new Result(adviceQuestion2, optionListItems2, List.of(attribute), questionnaire);
        when(loadCreatedAdviceDetailsPort.loadAdviceDetails(List.of(question1.getId(), question2.getId())))
            .thenReturn(List.of(questionsPortResult1, questionsPortResult2));
    }
}
