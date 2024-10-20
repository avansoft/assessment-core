package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.test.fixture.application.AssessmentMother;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother.createSimpleAdviceListItem;
import static org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother.createAttributeLevelTarget;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAiAdviceNarrationServiceTest {

    @InjectMocks
    CreateAiAdviceNarrationService service;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    LoadAssessmentPort loadAssessmentPort;

    @Mock
    ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;

    @Mock
    LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    LoadAttributesPort loadAttributesPort;

    @Mock
    OpenAiProperties openAiProperties;

    @Mock
    CallAiPromptPort callAiPromptPort;

    @Mock
    LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    AppAiProperties appAiProperties;

    @Test
    void testCreateAiAdviceNarration_UserDoesNotHaveRequiredPermission_ShouldThrowAccessDeniedException() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(appAiProperties,
            loadAssessmentResultPort,
            validateAssessmentResultPort,
            openAiProperties,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeCurrentAndTargetLevelIndexPort,
            loadMaturityLevelsPort,
            loadAssessmentPort,
            loadAttributesPort);
    }

    @Test
    void testCreateAiAdviceNarration_AiIsDisabled_ShouldReturnAiIsDisabledMessage() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = service.createAiAdviceNarration(param);

        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result.content());

        verifyNoInteractions(loadAssessmentResultPort,
            validateAssessmentResultPort,
            openAiProperties,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeCurrentAndTargetLevelIndexPort,
            loadAttributesPort,
            loadAssessmentPort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAiAdviceNarration_AssessmentResultDoesNotExist_ShouldReturnResourceNoFound() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            openAiProperties,
            callAiPromptPort,
            loadAdviceNarrationPort,
            loadAssessmentPort,
            createAdviceNarrationPort);
    }

    @Test
    void testCreateAiAdviceNarration_AdviceNarrationDoesNotExist_ShouldCreateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 123L);
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var prompt = new Prompt("AI prompt");
        var assessment = AssessmentMother.assessmentWithShortTitle("ShortTitle");

        var attributes = List.of(new Attribute(attributeLevelTargets.getFirst().getAttributeId(), "Reliability"));
        var maturityLevels = List.of(new MaturityLevel(attributeLevelTargets.getFirst().getMaturityLevelId(), "Great"));

        var promptAdviceItems = List.of(new CreateAiAdviceNarrationService.AdviceItem(adviceListItems.getFirst().question().title(),
            adviceListItems.getFirst().answeredOption().title(),
            adviceListItems.getFirst().recommendedOption().title()));

        var targetAttributes = List.of(new CreateAiAdviceNarrationService.TargetAttribute(
            attributes.getFirst().getTitle(), maturityLevels.getFirst().getTitle()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndKitVersionId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentResult.getKitVersionId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(assessment);
        when(openAiProperties.createAiAdviceNarrationPrompt(assessment.getShortTitle(), promptAdviceItems.toString(), targetAttributes.toString())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiNarration);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAiAdviceNarration(param);
    }

    @Test
    void testCreateAiAdviceNarration_AdviceNarrationExistsAndShortTitleNotExists_ShouldUpdateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 123L);
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var prompt = new Prompt("AI prompt");
        var assessment = AssessmentMother.assessmentWithShortTitle(null);

        var attributes = List.of(new Attribute(attributeLevelTargets.getFirst().getAttributeId(), "Reliability"));
        var maturityLevels = List.of(new MaturityLevel(attributeLevelTargets.getFirst().getMaturityLevelId(), "Great"));

        var promptAdviceItems = List.of(new CreateAiAdviceNarrationService.AdviceItem(adviceListItems.getFirst().question().title(),
            adviceListItems.getFirst().answeredOption().title(),
            adviceListItems.getFirst().recommendedOption().title()));

        var targetAttributes = List.of(new CreateAiAdviceNarrationService.TargetAttribute(
            attributes.getFirst().getTitle(), maturityLevels.getFirst().getTitle()));

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndKitVersionId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentResult.getKitVersionId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(assessment);
        when(openAiProperties.createAiAdviceNarrationPrompt(assessment.getTitle(), promptAdviceItems.toString(), targetAttributes.toString())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiNarration);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAiAdviceNarration(param);
    }

    @Test
    void testCreateAiAdviceNarration_AdviceNarrationExistsAndShortTitleExists_ShouldUpdateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 123L);
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var prompt = new Prompt("AI prompt");
        var assessment = AssessmentMother.assessmentWithShortTitle("shortTitle");

        var attributes = List.of(new Attribute(attributeLevelTargets.getFirst().getAttributeId(), "Reliability"));
        var maturityLevels = List.of(new MaturityLevel(attributeLevelTargets.getFirst().getMaturityLevelId(), "Great"));

        var promptAdviceItems = List.of(new CreateAiAdviceNarrationService.AdviceItem(adviceListItems.getFirst().question().title(),
            adviceListItems.getFirst().answeredOption().title(),
            adviceListItems.getFirst().recommendedOption().title()));

        var targetAttributes = List.of(new CreateAiAdviceNarrationService.TargetAttribute(
            attributes.getFirst().getTitle(), maturityLevels.getFirst().getTitle()));

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndKitVersionId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentResult.getKitVersionId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(assessment);
        when(openAiProperties.createAiAdviceNarrationPrompt(assessment.getShortTitle(), promptAdviceItems.toString(), targetAttributes.toString())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiNarration);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAiAdviceNarration(param);
    }

    @Test
    void testCreateAiAdviceNarration_NoValidTargetExists_ThrowValidationException() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 123L);
        var param = new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());


        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 1)));

        var throwable = assertThrows(ValidationException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN, throwable.getMessageKey());

        verifyNoInteractions(loadAttributesPort,
            loadMaturityLevelsPort,
            callAiPromptPort,
            openAiProperties,
            createAdviceNarrationPort);
    }
}
