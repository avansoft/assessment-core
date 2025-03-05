package org.flickit.assessment.core.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static Space mapToDomain(SpaceJpaEntity entity) {
        return new Space(entity.getId(),
            entity.getTitle(),
            entity.getOwnerId(),
            SpaceType.valueOfById(entity.getType()),
            entity.getSubscriptionExpiry());
    }
}
