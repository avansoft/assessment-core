package org.flickit.assessment.data.jpa.core.evidenceattachment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EvidenceAttachmentWithUserView {

    UUID getId();

    String getFilePath();

    String getDescription();

    UUID getUserId();

    String getDisplayName();

    LocalDateTime getCreationTime();
}
