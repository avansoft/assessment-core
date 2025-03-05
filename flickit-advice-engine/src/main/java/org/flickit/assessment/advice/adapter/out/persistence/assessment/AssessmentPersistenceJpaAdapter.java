package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.Assessment;
import org.flickit.assessment.advice.application.port.out.assessment.*;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;

@Component("adviceAssessmentPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    LoadSelectedAttributeIdsRelatedToAssessmentPort,
    LoadSelectedLevelIdsRelatedToAssessmentPort,
    LoadAssessmentKitVersionIdPort,
    LoadAssessmentPort,
    LoadAssessmentKitLanguagePort {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public Set<Long> loadSelectedAttributeIdsRelatedToAssessment(UUID assessmentId, Set<Long> attributeIds) {
        return repository.findSelectedAttributeIdsRelatedToAssessment(assessmentId, attributeIds);
    }

    @Override
    public Set<Long> loadSelectedLevelIdsRelatedToAssessment(UUID assessmentId, Set<Long> levelIds) {
        return repository.findSelectedLevelIdsRelatedToAssessment(assessmentId, levelIds);
    }

    @Override
    public Long loadKitVersionIdById(UUID assessmentId) {
        return assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND))
            .getKitVersionId();
    }

    @Override
    public Assessment loadById(UUID assessmentId) {
        return repository.findById(assessmentId).map(AssessmentMapper::mapToDomain)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
    }

    @Override
    public KitLanguage loadKitLanguage(UUID assessmentId) {
        int languageId = repository.loadKitLanguageByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        return KitLanguage.valueOfById(languageId);
    }
}
