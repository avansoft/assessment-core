package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.attribute.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attribute.AttributeMaturityLevelSubjectView;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component("coreAttributePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    LoadAttributeScoreDetailPort,
    LoadAttributePort,
    LoadAttributeScoresPort,
    CountAttributesPort,
    LoadAttributesPort {

    private final AttributeJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final KitCustomJpaRepository kitCustomRepository;
    private final AssessmentJpaRepository assessmentRepository;

    @Override
    public PaginatedResponse<LoadAttributeScoreDetailPort.Result> loadScoreDetail(LoadAttributeScoreDetailPort.Param param) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND));

        var pageRequest = buildPageRequest(param.page(), param.size(), param.sort(), param.order());
        var pageResult = repository.findImpactFullQuestionsScore(
            assessmentResult.getAssessment().getId(),
            assessmentResult.getId(),
            assessmentResult.getKitVersionId(),
            param.attributeId(),
            param.maturityLevelId(),
            pageRequest);

        var items = pageResult.getContent().stream()
            .map(view -> new LoadAttributeScoreDetailPort.Result(view.getQuestionnaireId(),
                view.getQuestionnaireTitle(),
                view.getQuestionId(),
                view.getQuestionIndex(),
                view.getQuestionTitle(),
                view.getQuestionImpact().getWeight(),
                view.getOptionIndex(),
                view.getOptionTitle(),
                view.getAnswer() == null ? null : view.getAnswer().getIsNotApplicable(),
                view.getGainedScore(),
                view.getMissedScore(),
                view.getAnswer() != null && view.getAnswer().getConfidenceLevelId() != null ? view.getAnswer().getConfidenceLevelId() : null,
                view.getEvidenceCount()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageRequest.getPageNumber(),
            pageRequest.getPageSize(),
            param.order().getTitle(),
            param.sort().getTitle(),
            (int) pageResult.getTotalElements()
        );
    }

    private PageRequest buildPageRequest(int page, int size, GetAttributeScoreDetailUseCase.Param.Sort sort, Order order) {
        var orderDir = switch (order) {
            case Order.ASC -> Sort.Direction.ASC;
            case Order.DESC -> Sort.Direction.DESC;
        };

        String sortField = switch (sort) {
            case GetAttributeScoreDetailUseCase.Param.Sort.QUESTIONNAIRE -> "qr." + QuestionnaireJpaEntity.Fields.title;
            case GetAttributeScoreDetailUseCase.Param.Sort.WEIGHT -> "qi." + QuestionImpactJpaEntity.Fields.weight;
            case GetAttributeScoreDetailUseCase.Param.Sort.CONFIDENCE ->
                "ans." + AnswerJpaEntity.Fields.confidenceLevelId;
            case GetAttributeScoreDetailUseCase.Param.Sort.GAINED_SCORE -> "gainedScore";
            case GetAttributeScoreDetailUseCase.Param.Sort.MISSED_SCORE -> "missedScore";
            case GetAttributeScoreDetailUseCase.Param.Sort.EVIDENCE_COUNT -> "evidenceCount";
        };

        return PageRequest.of(page, size, orderDir, sortField);
    }

    @Override
    public List<LoadAttributeScoresPort.Result> loadScores(UUID assessmentId, long attributeId, long maturityLevelId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_STATS_ASSESSMENT_RESULT_NOT_FOUND));

        return repository.findScoreStats(assessmentResult.getId(), assessmentResult.getKitVersionId(), attributeId, maturityLevelId)
            .stream()
            .map(view -> new LoadAttributeScoresPort.Result(view.getQuestionId(),
                view.getQuestionWeight(),
                getScore(view.getAnswer(), view.getOptionValue()),
                view.getAnswer() != null && view.getAnswerIsNotApplicable() != null && view.getAnswer().getIsNotApplicable()))
            .toList();
    }

    private Double getScore(AnswerJpaEntity answer, Double optionValue) {
        if (answer == null) // if no answer is submitted for the question
            return 0.0;
        if (Boolean.TRUE.equals(answer.getIsNotApplicable())) // if there is an answer and notApplicable == true
            return null;
        return optionValue;
    }

    @Override
    public Attribute load(Long attributeId, Long kitVersionId) {
        var attribute = repository.findByIdAndKitVersionId(attributeId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND));
        return mapToDomainModel(attribute);
    }

    @Override
    public int countAttributes(long kitVersionId) {
        return repository.countByKitVersionId(kitVersionId);
    }

    @Override
    public List<LoadAttributesPort.Result> loadAll(UUID assessmentId) {
        var assessment = assessmentRepository.findByIdAndDeletedFalse(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        var attributeViews = repository.findAllByAssessmentIdWithSubjectAndMaturityLevel(assessmentId);
        var attributes = attributeViews.stream()
            .map(e -> AttributeMapper.mapToDomainModel(e.getAttribute()))
            .toList();

        var attributeIdToWeight = getAttributeIdToWeightMap(attributes,
            assessment.getAssessmentKitId(),
            assessment.getKitCustomId());

        return attributeViews.stream()
            .sorted(Comparator.comparingInt((AttributeMaturityLevelSubjectView v) -> v.getSubject().getIndex())
                .thenComparingInt(v -> v.getAttribute().getIndex()))
            .map(e -> AttributeMapper.mapToResult(e, attributeIdToWeight.get(e.getAttribute().getId())))
            .toList();
    }

    @SneakyThrows
    private Map<Long, Integer> getAttributeIdToWeightMap(List<Attribute> attributes, long kitId, Long kitCustomId) {
        if (kitCustomId == null)
            return attributes.stream()
                .collect(toMap(Attribute::getId, Attribute::getWeight));

        var kitCustomEntity = kitCustomRepository.findByIdAndKitId(kitCustomId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        KitCustomData kitCustomData = new ObjectMapper()
            .readValue(kitCustomEntity.getCustomData(), KitCustomData.class);

        if (kitCustomData == null || kitCustomData.attributes() == null)
            return attributes.stream()
                .collect(toMap(Attribute::getId, Attribute::getWeight));

        Map<Long, Integer> attributeIdToCustomWeight = kitCustomData.attributes().stream()
            .collect(toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight));

        return attributes.stream()
            .collect(toMap(
                Attribute::getId,
                e -> attributeIdToCustomWeight.getOrDefault(e.getId(), e.getWeight())));
    }
}
