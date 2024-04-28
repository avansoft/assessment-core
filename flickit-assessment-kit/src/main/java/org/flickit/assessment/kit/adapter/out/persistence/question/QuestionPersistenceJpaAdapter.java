package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.AttributeLevelImpactfulQuestionsView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadAttributeLevelQuestionsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper.mapToJpaEntity;
import static org.flickit.assessment.kit.adapter.out.persistence.questionnaire.QuestionnaireMapper.mapToDomainModel;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    UpdateQuestionPort,
    CreateQuestionPort,
    CountSubjectQuestionsPort,
    LoadQuestionPort,
    LoadAttributeLevelQuestionsPort {

    private final QuestionJpaRepository repository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public void update(UpdateQuestionPort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.advisable(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Long persist(CreateQuestionPort.Param param) {
        return repository.save(mapToJpaEntity(param)).getId();
    }

    @Override
    public int countBySubjectId(long subjectId) {
        return repository.countDistinctBySubjectId(subjectId);
    }

    @Override
    public Question load(long id, long kitId) {
        var questionEntity = repository.findByIdAndKitId(id, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));
        Question question = QuestionMapper.mapToDomainModel(questionEntity);

        var impacts = questionImpactRepository.findAllByQuestionId(id).stream()
            .map(QuestionImpactMapper::mapToDomainModel)
            .map(this::setOptionImpacts)
            .toList();

        var options = answerOptionRepository.findByQuestionId(id).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();

        question.setImpacts(impacts);
        question.setOptions(options);
        return question;
    }

    private QuestionImpact setOptionImpacts(QuestionImpact impact) {
        impact.setOptionImpacts(
            answerOptionImpactRepository.findAllByQuestionImpactId(impact.getId()).stream()
                .map(AnswerOptionImpactMapper::mapToDomainModel)
                .toList()
        );
        return impact;
    }

    @Override
    public List<LoadAttributeLevelQuestionsPort.Result> loadAttributeLevelQuestions(long kitId, long attributeId, long maturityLevelId) {
        if (!attributeRepository.existsByIdAndKitId(attributeId, kitId))
            throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);

        if (!maturityLevelRepository.existsByIdAndKitId(maturityLevelId, kitId))
            throw new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND);
        var views = repository.findByAttributeIdAndMaturityLevelId(attributeId, maturityLevelId);

        Map<QuestionJpaEntity, List<AttributeLevelImpactfulQuestionsView>> myMap = views.stream()
            .collect(Collectors.groupingBy(AttributeLevelImpactfulQuestionsView::getQuestion));

        return myMap.entrySet().stream()
            .map(entry -> {
                Question question = QuestionMapper.mapToDomainModel(entry.getKey());
                Questionnaire questionnaire = mapToDomainModel(entry.getValue().get(0).getQuestionnaire());

                QuestionImpact impact = QuestionImpactMapper.mapToDomainModel(entry.getValue().get(0).getQuestionImpact());
                Map<Long, AnswerOptionImpactJpaEntity> optionMap = entry.getValue().stream()
                    .collect(Collectors.toMap(e -> e.getOptionImpact().getId(), AttributeLevelImpactfulQuestionsView::getOptionImpact,
                        (existing, replacement) -> existing));
                List<AnswerOptionImpact> optionImpacts = optionMap.values()
                    .stream().map(AnswerOptionImpactMapper::mapToDomainModel).toList();
                impact.setOptionImpacts(optionImpacts);
                question.setImpacts(List.of(impact));

                List<AnswerOption> options = entry.getValue().stream()
                    .collect(Collectors.toMap(e -> e.getAnswerOption().getId(), AttributeLevelImpactfulQuestionsView::getAnswerOption,
                        (existing, replacement) -> existing))
                    .values()
                    .stream().map(AnswerOptionMapper::mapToDomainModel).toList();
                question.setOptions(options);

                return new Result(question, questionnaire);
            }).toList();
    }
}
