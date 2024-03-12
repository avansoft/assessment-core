package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerServiceTest {

    private static final Long QUESTIONNAIRE_ID = 25L;
    private static final UUID QUESTION_REF_NUM = UUID.randomUUID();;

    @InjectMocks
    private SubmitAnswerService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAnswerPort createAnswerPort;

    @Mock
    private UpdateAnswerPort updateAnswerPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private LoadAnswerOptionPort loadAnswerOptionPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNotNull_SavesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        Long answerOptionId = 2L;
        Boolean isNotApplicable = Boolean.FALSE;
        Long questionId = 5L;

        Question question = QuestionMother.withIdAndQuestionnaireId(questionId, QUESTIONNAIRE_ID);
        AnswerOption answerOption = AnswerOptionMother.optionWithQuestionId(questionId);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.empty());
        when(loadAnswerOptionPort.loadById(answerOptionId)).thenReturn(Optional.of(answerOption));
        when(loadQuestionPort.loadByRefNum(assessmentResult.getKitVersionId(), QUESTION_REF_NUM)).thenReturn(Optional.of(question));
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, answerOptionId, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());
        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_REF_NUM, saveAnswerParam.getValue().questionRefNum());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.FALSE, Boolean.FALSE);
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNull_SavesAnswerAndDoesNotInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        Boolean isNotApplicable = Boolean.FALSE;
        Long questionId = 5L;

        Question question = QuestionMother.withIdAndQuestionnaireId(questionId, QUESTIONNAIRE_ID);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.empty());
        when(loadQuestionPort.loadByRefNum(assessmentResult.getKitVersionId(), QUESTION_REF_NUM)).thenReturn(Optional.of(question));
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, null, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());
        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_REF_NUM, saveAnswerParam.getValue().questionRefNum());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertNull(saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verifyNoInteractions(invalidateAssessmentResultPort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistsAndIsNotApplicableTrue_SavesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        Long answerOptionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;
        Long questionId = 5L;

        Question question = QuestionMother.withIdAndQuestionnaireId(questionId, QUESTIONNAIRE_ID);
        AnswerOption answerOption = AnswerOptionMother.optionWithQuestionId(questionId);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.empty());
        when(loadAnswerOptionPort.loadById(answerOptionId)).thenReturn(Optional.of(answerOption));
        when(loadQuestionPort.loadByRefNum(assessmentResult.getKitVersionId(), QUESTION_REF_NUM)).thenReturn(Optional.of(question));
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, answerOptionId, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());
        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_REF_NUM, saveAnswerParam.getValue().questionRefNum());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class), eq(Boolean.FALSE), eq(Boolean.FALSE));
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAnswerOptionChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.FALSE;
        Long newAnswerOptionId = AnswerOptionMother.optionTwo().getId();
        AnswerOption oldAnswerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, newAnswerOptionId, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(newAnswerOptionId, updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_REF_NUM);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.FALSE, Boolean.TRUE);
        verifyNoInteractions(createAnswerPort,
            loadAnswerOptionPort,
            loadQuestionPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAndIsNotApplicableTrue_SavesAndInvalidatesAssessmentResult() {
        UUID currentUserId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.TRUE;
        AnswerOption oldAnswerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.of(existAnswer));

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, ConfidenceLevel.getDefault().getId(), isNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, oldAnswerOption.getId(), ConfidenceLevel.getDefault().getId(), isNotApplicable, currentUserId);
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class), eq(Boolean.FALSE), eq(Boolean.TRUE));
        verifyNoInteractions(createAnswerPort,
            loadAnswerOptionPort,
            loadQuestionPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOption_DoNotInvalidateAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        AnswerOption sameAnswerOption = AnswerOptionMother.optionTwo();
        Answer existAnswer = AnswerMother.answerWithNullNotApplicable(sameAnswerOption);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, sameAnswerOption.getId(), ConfidenceLevel.getDefault().getId(), null, UUID.randomUUID());
        service.submitAnswer(param);

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_REF_NUM);
        verifyNoInteractions(createAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort,
            loadAnswerOptionPort,
            loadQuestionPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsNotApplicableChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID currentUserId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean newIsNotApplicable = Boolean.FALSE;
        AnswerOption answerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableTrue(answerOption);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.of(existAnswer));

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), answerOption.getId(), ConfidenceLevel.getDefault().getId(), newIsNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, answerOption.getId(), ConfidenceLevel.getDefault().getId(), newIsNotApplicable, currentUserId);
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(newIsNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_REF_NUM);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.FALSE, Boolean.TRUE);
        verifyNoInteractions(createAnswerPort,
            loadAnswerOptionPort,
            loadQuestionPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameIsNotApplicableExists_DoNotInvalidateAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Answer existAnswer = AnswerMother.answerWithNotApplicableTrue(null);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, null, ConfidenceLevel.getDefault().getId(), Boolean.TRUE, UUID.randomUUID());
        service.submitAnswer(param);

        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(any());
        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_REF_NUM);
        verifyNoInteractions(createAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort,
            loadAnswerOptionPort,
            loadQuestionPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsConfidenceLevelChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.FALSE;
        Integer newConfidenceLevelId = 3;
        AnswerOption answerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableFalse(answerOption);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_REF_NUM)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_REF_NUM, answerOption.getId(), newConfidenceLevelId, isNotApplicable, UUID.randomUUID());
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(newConfidenceLevelId, updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_REF_NUM);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.TRUE, Boolean.FALSE);
        verifyNoInteractions(createAnswerPort,
            loadAnswerOptionPort,
            loadQuestionPort);
    }
}
