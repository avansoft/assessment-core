package org.flickit.assessment.kit.application.port.out.levelcomptenece;

import java.util.UUID;

public interface UpdateLevelCompetencePort {

    void update(Long affectedLevelId, Long effectiveLevelId, Integer value, UUID lastModifiedBy);
}
