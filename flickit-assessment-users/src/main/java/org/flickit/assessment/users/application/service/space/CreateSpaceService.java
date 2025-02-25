package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacePort;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_BASIC_SPACE_MAX;

@Service
@RequiredArgsConstructor
public class CreateSpaceService implements CreateSpaceUseCase {

    private final CreateSpacePort createSpacePort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final CountSpacePort countSpacePort;
    private final AppSpecProperties appSpecProperties;

    @Override
    public Result createSpace(Param param) {
        if (countSpacePort.countBasicSpaces(param.getCurrentUserId()) >= appSpecProperties.getSpace().getMaxBasicSpaces())
            throw new UpgradeRequiredException(CREATE_SPACE_BASIC_SPACE_MAX);

        long id = createSpacePort.persist(mapToDomain(param));

        createOwnerAccessToSpace(id, param.getCurrentUserId(), param.getCurrentUserId());
        return new Result(id);
    }

    private Space mapToDomain(Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new Space(null,
            generateSlugCode(param.getTitle()),
            param.getTitle(),
            SpaceType.valueOf(param.getType()),
            param.getCurrentUserId(),
            null,
            creationTime,
            creationTime,
            param.getCurrentUserId(),
            param.getCurrentUserId()
        );
    }

    private void createOwnerAccessToSpace(long id, UUID invitee, UUID inviter) {
        createSpaceUserAccessPort.persist(new SpaceUserAccess(id, invitee, inviter, LocalDateTime.now()));
    }
}
