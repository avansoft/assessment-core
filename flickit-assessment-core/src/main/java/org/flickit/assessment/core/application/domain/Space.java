package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Space {

    private final long id;
    private final String title;
    private final UUID ownerId;
    private final SpaceType type;
    private final LocalDateTime subscriptionExpiry;
}
