package org.flickit.assessment.advice.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;

public interface LoadAssessmentKitLanguagePort {

    KitLanguage loadKitLanguage(long kitVersionId);
}
