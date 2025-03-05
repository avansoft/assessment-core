package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.port.out.assessmentkit.*;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_KIT_ID_NOT_FOUND;

@Component("coreAssessmentKitPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitLastMajorModificationTimePort,
    LoadAssessmentKitVersionIdPort,
    CheckKitAccessPort,
    LoadKitInfoPort,
    LoadAssessmentKitPort {

    private final AssessmentKitJpaRepository repository;

    @Override
    public LocalDateTime loadLastMajorModificationTime(Long kitId) {
        return repository.loadLastMajorModificationTime(kitId);
    }

    @Override
    public Long loadVersionId(long kitId) {
        return repository.loadKitVersionId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));
    }

    @Override
    public Optional<Long> checkAccess(long kitId, UUID userId) {
        return repository.existsByUserId(kitId, userId);
    }

    @Override
    public Result loadKitInfo(long id) {
        AssessmentKitJpaEntity kitEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));

        return new Result(kitEntity.getTitle(), kitEntity.getCreatedBy(), kitEntity.getExpertGroupId());
    }

    @Override
    public AssessmentKit loadAssessmentKit(long kitId) {
        var entity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));

        return AssessmentKitMapper.mapToDomainModel(entity, null);
    }
}
