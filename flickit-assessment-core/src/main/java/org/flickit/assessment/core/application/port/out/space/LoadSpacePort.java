package org.flickit.assessment.core.application.port.out.space;

import org.flickit.assessment.core.application.domain.Space;

public interface LoadSpacePort {

    Space loadSpace(long spaceId);
}
