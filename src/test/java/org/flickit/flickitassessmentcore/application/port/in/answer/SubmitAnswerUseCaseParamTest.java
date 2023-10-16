package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerUseCaseParamTest {

    @Test
    void testSubmitAnswer_NullAssessmentId_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(null, 1L, 1L, 1L, Boolean.FALSE));
        assertThat(throwable).hasMessage("assessmentId: " + SUBMIT_ANSWER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_NullQuestionnaireId_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(assessmentResult, null, 1L, 1L, Boolean.FALSE));
        assertThat(throwable).hasMessage("questionnaireId: " + SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_NullQuestionId_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(assessmentResult, 1L, null, 1L, Boolean.FALSE));
        assertThat(throwable).hasMessage("questionId: " + SUBMIT_ANSWER_QUESTION_ID_NOT_NULL);
    }

}
