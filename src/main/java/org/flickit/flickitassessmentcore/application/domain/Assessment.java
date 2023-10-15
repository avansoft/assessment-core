package org.flickit.flickitassessmentcore.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Assessment {

    private final UUID id;
    private final String code;
    private final String title;
    private final AssessmentKit assessmentKit;
    private final int colorId;
    private final long spaceId;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final long deletionTime;
    private final boolean deleted;

    public static String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }
}
