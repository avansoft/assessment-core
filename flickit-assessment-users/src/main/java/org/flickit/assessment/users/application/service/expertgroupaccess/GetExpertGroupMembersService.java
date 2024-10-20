package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.GET_EXPERT_GROUP_MEMBERS_EXPERT_GROUP_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupMembersService implements GetExpertGroupMembersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    private final LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<Member> getExpertGroupMembers(Param param) {
        if (!checkExpertGroupExistsPort.existsById(param.getId()))
            throw new ResourceNotFoundException(GET_EXPERT_GROUP_MEMBERS_EXPERT_GROUP_NOT_FOUND);

        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(param.getId());

        boolean userIsOwner = ownerId.equals(param.getCurrentUserId());

        if (!userIsOwner && param.getStatus() == ExpertGroupAccessStatus.PENDING)
            return new PaginatedResponse<>(List.of(), 0, 0, null, null, 0);

        ExpertGroupAccessStatus requiredStatus = param.getStatus() != null ? param.getStatus() : ExpertGroupAccessStatus.ACTIVE;

        var portResult = loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), requiredStatus.ordinal(), param.getPage(), param.getSize());
        var members = mapToMembers(portResult.getItems(), userIsOwner);

        return new PaginatedResponse<>(
            members,
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private List<Member> mapToMembers(List<LoadExpertGroupMembersPort.Member> items, boolean userIsOwner) {
        return items.stream()
            .map(item -> new Member(
                item.id(),
                userIsOwner ? item.email() : null,
                item.displayName(),
                item.bio(),
                createFileDownloadLinkPort.createDownloadLink(item.picture(), EXPIRY_DURATION),
                item.linkedin(),
                ExpertGroupAccessStatus.values()[item.status()],
                item.inviteExpirationDate()
            ))
            .toList();
    }
}
