package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITE_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITE_INVITE_TOKEN_NOT_NULL;

public interface ConfirmExpertGroupInviteUseCase {

    void confirmInvitation(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<InviteExpertGroupMemberUseCase.Param> {

        @NotNull(message = CONFIRM_EXPERT_GROUP_INVITE_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = CONFIRM_EXPERT_GROUP_INVITE_INVITE_TOKEN_NOT_NULL)
        UUID inviteToken;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long expertGroupId, UUID inviteToken, UUID currentUserId) {
            this.expertGroupId = expertGroupId;
            this.inviteToken = inviteToken;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
