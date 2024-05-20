package org.flickit.assessment.common.application.domain.assessment;

import java.util.UUID;

public interface AssessmentPermissionChecker {

    boolean isAuthorized(UUID assessmentId, UUID userId, AssessmentPermission permission);
}
