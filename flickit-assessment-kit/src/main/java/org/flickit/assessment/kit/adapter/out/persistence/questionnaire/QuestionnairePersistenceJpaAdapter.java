package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort,
    LoadQuestionnairesPort,
    LoadKitQuestionnaireDetailPort {

    private final QuestionnaireJpaRepository repository;
    private final AssessmentKitJpaRepository assessmentKitRepository;
    private final QuestionJpaRepository questionRepository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public Long persist(Questionnaire questionnaire, long kitVersionId, UUID createdBy) {
        return repository.save(QuestionnaireMapper.mapToJpaEntityToPersist(questionnaire, kitVersionId, createdBy)).getId();
    }

    @Override
    public void update(UpdateQuestionnairePort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public List<Questionnaire> loadByKitId(Long kitId) {
        var kitVersionId = assessmentKitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND))
            .getKitVersionId();

        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public Result loadKitQuestionnaireDetail(Long questionnaireId, Long kitId) {
        QuestionnaireJpaEntity questionnaireEntity = repository.findQuestionnaireByIdAndKitId(questionnaireId, kitId)
            .orElseThrow(() ->  new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        List<QuestionJpaEntity> questionEntities = questionRepository.findAllByQuestionnaireIdOrderByIndexAsc(questionnaireId);
        List<SubjectJpaEntity> subjectEntities = subjectRepository.findAllByQuestionnaireId(questionnaireId);

        List<String> relatedSubjects = subjectEntities.stream()
            .map(SubjectJpaEntity::getTitle)
            .toList();

        List<Question> questions = questionEntities.stream()
            .map(QuestionMapper::mapToDomainModel)
            .toList();

        return new Result(questionEntities.size(), relatedSubjects, questionnaireEntity.getDescription(), questions);
    }
}
