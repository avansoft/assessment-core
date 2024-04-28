package org.flickit.assessment.users.application.port.in.spaceaccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_EMAIL_NOT_NULL;

public interface InviteSpaceMemberUseCase {

    void inviteMember(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = INVITE_SPACE_MEMBER_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotNull(message = INVITE_SPACE_MEMBER_EMAIL_NOT_NULL)
        String email;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long spaceId, String email, UUID currentUserId) {
            this.spaceId = spaceId;
            this.email = email;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

}
