package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentListItem;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AssessmentMother {

    private static int counter = 341;

    public static Assessment assessment() {
        return assessmentWithKitLanguage(KitLanguage.EN);
    }

    public static Assessment assessmentWithKitLanguage(KitLanguage language) {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            "Short title" + counter,
            AssessmentKitMother.kitWithLanguage(language),
            SpaceMother.createBasicSpaceWithOwnerId(UUID.randomUUID()),
            234L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false,
            UUID.randomUUID()
        );
    }

    public static Assessment assessmentWithoutActiveVersion() {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            "Short title" + counter,
            AssessmentKitMother.AssessmentKitWithoutActiveKitVersion(),
            SpaceMother.createBasicSpaceWithOwnerId(UUID.randomUUID()),
            234L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false,
            UUID.randomUUID()
        );
    }

    public static AssessmentListItem assessmentListItem(Long spaceId, Long kitId) {
        return assessmentListItem(spaceId, kitId, Boolean.FALSE);
    }

    public static AssessmentListItem assessmentListItem(Long spaceId, Long kitId, boolean manageable) {
        counter++;
        return new AssessmentListItem(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            new AssessmentListItem.Kit(kitId, "kitTitle"+kitId, 2),
            new AssessmentListItem.Space(spaceId, "spaceTitle"),
            LocalDateTime.now(),
            new AssessmentListItem.MaturityLevel(counter, "levelTitle" + counter, 1, 2),
            new Random().nextDouble() * 100,
            Boolean.TRUE,
            Boolean.TRUE,
            manageable,
            Boolean.FALSE);
    }
}
