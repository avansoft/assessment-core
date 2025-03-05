package org.flickit.assessment.core.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;


@Component("coreSpacePersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    LoadSpaceOwnerPort,
    LoadSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public UUID loadOwnerId(long spaceId) {
        return repository.loadOwnerIdById(spaceId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_SPACE_ID_NOT_FOUND));
    }

    @Override
    public UUID loadOwnerId(UUID assessmentId) {
        return repository.findOwnerByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
    }

    @Override
    public Space loadSpace(long spaceId) {
        var space = repository.findById(spaceId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_SPACE_ID_NOT_FOUND));

        return SpaceMapper.mapToDomain(space);
    }
}
