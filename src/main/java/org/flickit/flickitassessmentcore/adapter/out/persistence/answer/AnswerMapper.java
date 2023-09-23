package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static AnswerListItem mapJpaEntityToAnswerItem(AnswerJpaEntity answer) {
        return new AnswerListItem(
            answer.getId(),
            answer.getQuestionId(),
            answer.getAnswerOptionId(),
            Boolean.FALSE
        );
    }
}
