package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentKit;

public class AssessmentKitMother {

    private static long id = 134L;

    public static AssessmentKit kit() {
        return new AssessmentKit(id++, MaturityLevelMother.allLevels());
    }

    public static AssessmentKit kitWithId(long id) {
        return new AssessmentKit(id, MaturityLevelMother.allLevels());
    }
}
