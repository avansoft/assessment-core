package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSpaceService implements GetSpaceUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpaceDetailsPort loadSpaceDetailsPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    public Result getSpace(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        LoadSpaceDetailsPort.Result spaceDetails = loadSpaceDetailsPort.loadSpace(param.getId());
        boolean editable = param.getCurrentUserId().equals(spaceDetails.space().getOwnerId());
        boolean canCreateAssessment = spaceDetails.assessmentsCount() < appSpecProperties.getSpace().getMaxBasicSpaceAssessments();

        return new Result(spaceDetails.space(),
            editable,
            spaceDetails.membersCount(),
            spaceDetails.assessmentsCount(),
            canCreateAssessment);
    }
}
