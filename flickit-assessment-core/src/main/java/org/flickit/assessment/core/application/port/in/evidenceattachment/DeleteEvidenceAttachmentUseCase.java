package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL;

public interface DeleteEvidenceAttachmentUseCase {

    void deleteEvidenceAttachment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL)
        UUID evidenceId;

        @NotNull(message = DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL)
        UUID attachmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID evidenceId, UUID attachmentId, UUID currentUserId) {
            this.evidenceId = evidenceId;
            this.attachmentId = attachmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
