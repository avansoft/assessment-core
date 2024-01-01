package org.flickit.assessment.core.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface SubmitAnswerUseCase {

    Result submitAnswer(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = SUBMIT_ANSWER_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL)
        Long questionnaireId;

        @NotNull(message = SUBMIT_ANSWER_QUESTION_ID_NOT_NULL)
        Long questionId;

        Long answerOptionId;

        Integer confidenceLevelId;

        Boolean isNotApplicable;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long questionnaireId, Long questionId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.questionnaireId = questionnaireId;
            this.questionId = questionId;
            this.answerOptionId = answerOptionId;
            this.confidenceLevelId = confidenceLevelId;
            this.isNotApplicable = isNotApplicable;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
