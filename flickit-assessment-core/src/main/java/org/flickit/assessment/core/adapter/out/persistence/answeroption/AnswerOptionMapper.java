package org.flickit.assessment.core.adapter.out.persistence.answeroption;

import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity entity) {
        return new AnswerOption(entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getQuestionId(),
            null);
    }
}
