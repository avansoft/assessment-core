package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.RemoveExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.RemoveExpertGroupPictureUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RemoveExpertGroupPictureRestController {

    private final RemoveExpertGroupPictureUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/expert-groups/{id}/picture")
    public ResponseEntity<Void> removeExpertGroupPicture(
        @PathVariable("id") Long id) {
        UUID currentUserId = userContext.getUser().id();
        useCase.remove(toParam(id, currentUserId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(Long id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }
}
