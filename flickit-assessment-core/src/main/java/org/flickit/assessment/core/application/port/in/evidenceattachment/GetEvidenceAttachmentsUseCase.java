package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ATTACHMENTS_EVIDENCE_ID_NULL;

public interface GetEvidenceAttachmentsUseCase {

    List<Attachment> getEvidenceAttachments(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_ATTACHMENTS_EVIDENCE_ID_NULL)
        UUID evidenceId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID evidenceId, UUID currentUserId) {
            this.evidenceId = evidenceId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Attachment(
        UUID id,
        String link,
        String description,
        User createdBy,
        LocalDateTime creationTime) {
    }
}
