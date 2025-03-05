package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentKit;

public class AssessmentKitMother {

    private static long id = 134L;

    public static AssessmentKit kit() {
        return kitWithLanguage(KitLanguage.EN);
    }

    public static AssessmentKit kitWithLanguage(KitLanguage language) {
        return new AssessmentKit(id++,
            "title" + id,
            id,
            language,
            MaturityLevelMother.allLevels(),
            Boolean.TRUE);
    }

    public static AssessmentKit publicKit() {
        return new AssessmentKit(id++,
            "title" + id,
            id,
            KitLanguage.EN,
            MaturityLevelMother.allLevels(),
            Boolean.FALSE);
    }

    public static AssessmentKit AssessmentKitWithoutActiveKitVersion() {
        return new AssessmentKit(id++,
            "title" + id,
            null,
            KitLanguage.EN,
            MaturityLevelMother.allLevels(),
            Boolean.TRUE);
    }
}
