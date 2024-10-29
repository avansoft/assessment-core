package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.ANSWER_OPTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements
    UpdateAnswerOptionPort,
    LoadAnswerOptionsByQuestionPort,
    CreateAnswerOptionPort,
    DeleteAnswerOptionPort,
    LoadAnswerOptionPort {

    private final AnswerOptionJpaRepository repository;
    private final QuestionJpaRepository questionRepository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public void update(UpdateAnswerOptionPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId) {
        if (!questionRepository.existsByIdAndKitVersionId(questionId, kitVersionId))
            throw new ResourceNotFoundException(QUESTION_ID_NOT_FOUND);

        return repository.findByQuestionIdAndKitVersionId(questionId, kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public Long persist(CreateAnswerOptionPort.Param param) {
        var entity = AnswerOptionMapper.mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerOptionId());
        return repository.save(entity).getId();
    }

    @Override
    public void delete(Long answerOptionId, Long kitVersionId) {
        if(!repository.existsByIdAndKitVersionId(answerOptionId, kitVersionId))
            throw new ResourceNotFoundException(ANSWER_OPTION_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(answerOptionId, kitVersionId);
    }

    @Override
    public List<AnswerOption> loadByKitVersionId(Long kitVersionId) {
        return repository.findByKitVersionId(kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }
}
