package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;
import java.util.UUID;

public interface UpdateMaturityLevelPort {

    void update(List<MaturityLevel> maturityLevels, UUID lastModifiedBy);

}
