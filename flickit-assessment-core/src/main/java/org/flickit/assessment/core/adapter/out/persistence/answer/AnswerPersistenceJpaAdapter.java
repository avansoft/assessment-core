package org.flickit.assessment.core.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.out.answer.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answer.AnswersQuestionnaireAndCountView;
import org.flickit.assessment.data.jpa.core.answer.QuestionnaireIdAndAnswerCountView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AnswerPersistenceJpaAdapter implements
    CreateAnswerPort,
    CountAnswersPort,
    LoadAnswerPort,
    UpdateAnswerPort,
    LoadQuestionsAnswerListPort,
    CountLowConfidenceAnswersPort,
    ApproveAnswerPort {

    private final AnswerJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final QuestionJpaRepository questionRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;

    @Override
    public UUID persist(CreateAnswerPort.Param param) {
        var assessmentResult = assessmentResultRepo.findById(param.assessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));
        var question = questionRepository.findByIdAndKitVersionId(param.questionId(), assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND));
        AnswerOptionJpaEntity answerOption = null;
        if (param.answerOptionId() != null)
            answerOption = answerOptionRepository.findByIdAndKitVersionId(param.answerOptionId(), assessmentResult.getKitVersionId())
                .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ANSWER_OPTION_ID_NOT_FOUND));

        if (!Objects.equals(question.getKitVersionId(), assessmentResult.getKitVersionId()) ||
            (answerOption != null && !Objects.equals(question.getAnswerRangeId(), answerOption.getAnswerRangeId())) ||
            !Objects.equals(question.getQuestionnaireId(), param.questionnaireId()))
            throw new ResourceNotFoundException(SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND);

        AnswerJpaEntity unsavedEntity = AnswerMapper.mapCreateParamToJpaEntity(param);
        unsavedEntity.setAssessmentResult(assessmentResult);
        AnswerJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public int countByQuestionIds(UUID assessmentResultId, List<Long> questionIds) {
        return repository.getCountByQuestionIds(assessmentResultId, questionIds);
    }

    @Override
    public int countUnapprovedAnswers(UUID assessmentResultId) {
        return repository.countUnapprovedAnswersByAssessmentResultId(assessmentResultId, AnswerStatus.UNAPPROVED.getId());
    }

    @Override
    public Map<Long, Integer> countUnapprovedAnswers(UUID assessmentResultId, Set<Long> questionnaireIds) {
        return repository.countQuestionnairesUnapprovedAnswers(assessmentResultId, questionnaireIds, AnswerStatus.UNAPPROVED.getId()).stream()
            .collect(toMap(
                AnswersQuestionnaireAndCountView::getQuestionnaireId,
                AnswersQuestionnaireAndCountView::getCount
            ));
    }

    @Override
    public Optional<Answer> load(UUID assessmentResultId, Long questionId) {
        return repository.findByAssessmentResultIdAndQuestionId(assessmentResultId, questionId)
            .map(AnswerMapper::mapToDomainModel);
    }

    @Override
    public void update(UpdateAnswerPort.Param param) {
        var answer = repository.findById(param.answerId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ANSWER_ID_NOT_FOUND));
        AnswerOptionJpaEntity answerOption;
        if (param.answerOptionId() != null) {
            answerOption = answerOptionRepository.findByIdAndKitVersionId(param.answerOptionId(), answer.getAssessmentResult().getKitVersionId())
                .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ANSWER_OPTION_ID_NOT_FOUND));
            var question = questionRepository.findByIdAndKitVersionId(answer.getQuestionId(), answer.getAssessmentResult().getKitVersionId())
                .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND));

            if (!Objects.equals(answerOption.getAnswerRangeId(), question.getAnswerRangeId()))
                throw new ResourceNotFoundException(SUBMIT_ANSWER_ANSWER_OPTION_ID_NOT_FOUND);
        }

        repository.update(param.answerId(),
            param.answerOptionId(),
            param.confidenceLevelId(),
            param.isNotApplicable(),
            param.status() != null ? param.status().getId() : null,
            param.currentUserId());
    }

    @Override
    public List<Answer> loadByQuestionIds(UUID assessmentId, List<Long> questionIds) {
        var assessmentResult = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        return repository.findByAssessmentResultIdAndQuestionIdIn(assessmentResult.getId(), questionIds).stream()
            .map(AnswerMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public int countWithConfidenceLessThan(UUID assessmentResultId, ConfidenceLevel confidence) {
        return repository.countWithConfidenceLessThan(assessmentResultId, confidence.getId());
    }

    @Override
    public Map<Long, Integer> countWithConfidenceLessThan(UUID assessmentResultId, Set<Long> questionnaireIds, ConfidenceLevel confidence) {
        return repository.countByQuestionnaireIdWithConfidenceLessThan(assessmentResultId, questionnaireIds, confidence.getId()).stream()
            .collect(toMap(
                QuestionnaireIdAndAnswerCountView::getQuestionnaireId,
                QuestionnaireIdAndAnswerCountView::getAnswerCount));
    }

    @Override
    public void approve(UUID answerId, UUID approvedBy) {
        repository.approve(answerId, approvedBy, AnswerStatus.APPROVED.getId());
    }

    @Override
    public void approveAll(UUID assessmentId, UUID approvedBy) {
        var assessmentResult = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        repository.approveByAssessmentResultId(assessmentResult.getId(), approvedBy, AnswerStatus.APPROVED.getId());
    }
}
