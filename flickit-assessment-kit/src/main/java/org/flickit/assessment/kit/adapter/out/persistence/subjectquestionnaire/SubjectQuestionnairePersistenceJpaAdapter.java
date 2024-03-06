package org.flickit.assessment.kit.adapter.out.persistence.subjectquestionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.DeleteSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.flickit.assessment.kit.adapter.out.persistence.subjectquestionnaire.SubjectQuestionnaireMapper.mapToJpaEntity;


@Component
@RequiredArgsConstructor
public class SubjectQuestionnairePersistenceJpaAdapter implements
    LoadSubjectQuestionnairePort,
    DeleteSubjectQuestionnairePort,
    CreateSubjectQuestionnairePort {

    private final SubjectQuestionnaireJpaRepository repository;

    @Override
    public List<SubjectQuestionnaire> loadByKitVersionId(long kitVersionId) {
        List<SubjectQuestionnaireJpaEntity> entities = repository.findAllByKitVersionId(kitVersionId);
        return entities.stream().map(SubjectQuestionnaireMapper::mapToDomainModel).toList();
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public long persist(long subjectId, long questionnaireId) {
        return  repository.save(mapToJpaEntity(subjectId, questionnaireId)).getId();
    }

    @Override
    public void persistAll(Map<Long, Set<Long>> questionnaireIdToSubjectIdsMap) {
        List<SubjectQuestionnaireJpaEntity> entities = questionnaireIdToSubjectIdsMap.keySet().stream()
            .flatMap(questionnaireId -> questionnaireIdToSubjectIdsMap.get(questionnaireId).stream()
                .map(subjectId -> mapToJpaEntity(subjectId, questionnaireId)))
            .toList();
        repository.saveAll(entities);
    }
}
