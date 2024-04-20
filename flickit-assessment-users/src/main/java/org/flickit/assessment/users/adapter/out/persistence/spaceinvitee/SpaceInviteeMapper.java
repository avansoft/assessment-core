package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SaveSpaceMemberInviteePort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceInviteeMapper {

    static SpaceInviteeJpaEntity mapCreateParamToJpaEntity(SaveSpaceMemberInviteePort.Param param) {
        return new SpaceInviteeJpaEntity(
            null,
            param.spaceId(),
            param.inviteeMail(),
            param.inviteDate(),
            param.inviteExpirationDate(),
            param.inviterId()
        );
    }
}
