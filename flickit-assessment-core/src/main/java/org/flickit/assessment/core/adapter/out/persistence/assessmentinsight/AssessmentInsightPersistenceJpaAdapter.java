package org.flickit.assessment.core.adapter.out.persistence.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.UpdateAssessmentInsightPort;
import org.flickit.assessment.data.jpa.core.assessmentinsight.AssessmentInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_INSIGHT_INSIGHT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.assessmentinsight.AssessmentInsightMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class AssessmentInsightPersistenceJpaAdapter implements
    CreateAssessmentInsightPort,
    LoadAssessmentInsightPort,
    UpdateAssessmentInsightPort {

    private final AssessmentInsightJpaRepository repository;

    @Override
    public UUID persist(AssessmentInsight assessmentInsight) {
        return repository.save(toJpaEntity(assessmentInsight)).getId();
    }

    @Override
    public Optional<AssessmentInsight> loadByAssessmentResultId(UUID assessmentResultId) {
        return repository.findByAssessmentResultId(assessmentResultId)
            .map(AssessmentInsightMapper::mapToDomain);
    }

    @Override
    public void updateInsight(AssessmentInsight assessmentInsight) {
        if (!repository.existsById(assessmentInsight.getId()))
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_INSIGHT_INSIGHT_NOT_FOUND);

        repository.update(assessmentInsight.getId(),
            assessmentInsight.getInsight(),
            assessmentInsight.getInsightTime(),
            assessmentInsight.getInsightBy());
    }
}
