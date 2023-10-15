package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.domain.AssessmentKit;
import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentMother {

    private static int counter = 341;

    public static Assessment assessment() {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            AssessmentKitMother.kit(),
            AssessmentColor.BLUE.getId(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false
        );
    }

    public static AssessmentListItem assessmentListItem(Long spaceId, Long kitId) {
        counter++;
        return new AssessmentListItem(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            kitId,
            spaceId,
            AssessmentColor.BLUE,
            LocalDateTime.now(),
            1L,
            Boolean.TRUE
        );
    }

    public static Assessment assessmentWithKit(AssessmentKit kit) {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            kit,
            AssessmentColor.BLUE.getId(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false
        );
    }
}
