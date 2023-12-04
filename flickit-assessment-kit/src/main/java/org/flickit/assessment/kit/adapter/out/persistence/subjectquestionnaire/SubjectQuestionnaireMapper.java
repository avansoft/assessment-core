package org.flickit.assessment.kit.adapter.out.persistence.subjectquestionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaEntity;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectQuestionnaireMapper {

    public static SubjectQuestionnaire mapToDomainModel(SubjectQuestionnaireJpaEntity entity) {
        return new SubjectQuestionnaire(
            entity.getId(),
            entity.getSubjectId(),
            entity.getQuestionnaireId());
    }

    public static SubjectQuestionnaireJpaEntity mapToJpaEntity(CreateSubjectQuestionnairePort.Param param) {
        return new SubjectQuestionnaireJpaEntity(
            null,
            param.subjectId(),
            param.questionnaireId());
    }
}
