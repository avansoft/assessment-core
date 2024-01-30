package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MaturityLevelJpaEntityMother {

    public static int index = 1;

    public static MaturityLevelJpaEntity maturityLevelEntity(Long id) {
        return new MaturityLevelJpaEntity(
            id,
            "code" + id,
            index++,
            "title" + id,
            id.intValue(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            1L
        );
    }
}
