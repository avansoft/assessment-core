package org.flickit.assessment.core.application.port.out.assessmentkit;

import org.flickit.assessment.core.application.domain.AssessmentKit;

public interface LoadAssessmentKitPort {

    AssessmentKit loadAssessmentKit(long kitId);
}
