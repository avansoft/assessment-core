package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.ConfirmExpertGroupInvitationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ConfirmExpertGroupInvitationRestController {

    private final ConfirmExpertGroupInvitationUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}/invite/{inviteToken}/confirm")
    public ResponseEntity<Void> confirmExpertGroupMember(
        @PathVariable("id") Long expertGroupId,
        @PathVariable("inviteToken") String inviteToken) {
        UUID currentUserId = userContext.getUser().id();
        useCase.confirmInvitation(toParam(expertGroupId, currentUserId, UUID.fromString(inviteToken)));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ConfirmExpertGroupInvitationUseCase.Param toParam(long expertGroupId,
                                                              UUID currentUserId,
                                                              UUID inviteToken) {
        return new ConfirmExpertGroupInvitationUseCase.Param(expertGroupId, currentUserId, inviteToken);
    }
}
