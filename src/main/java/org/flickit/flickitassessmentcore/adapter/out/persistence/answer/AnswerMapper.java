package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.domain.Answer;

public class AnswerMapper {

    public static AnswerJpaEntity mapCreateParamToJpaEntity(CreateAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionnaireId(),
            param.questionId(),
            param.answerOptionId()
        );
    }
}
