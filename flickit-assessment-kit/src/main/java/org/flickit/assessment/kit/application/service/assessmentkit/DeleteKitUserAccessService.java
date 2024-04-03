package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteKitUserAccessUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.LoadKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteKitUserAccessService implements DeleteKitUserAccessUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadKitUserAccessPort loadKitUserAccessPort;
    private final DeleteKitUserAccessPort deleteKitUserAccessPort;
    private final LoadUserPort loadUserPort;

    @Override
    public void delete(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        checkAccessExistence(param);

        deleteKitUserAccessPort.delete(new DeleteKitUserAccessPort.Param(param.getKitId(), param.getUserId()));
        log.debug("User [{}] access to private kit [{}] is removed.", param.getCurrentUserId(), param.getCurrentUserId());
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(kitId);
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private void checkAccessExistence(Param param) {
        User user = loadUserPort.loadById(param.getUserId()).orElseThrow(
            () -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_USER_NOT_FOUND));
        loadKitUserAccessPort.loadByKitIdAndUserId(param.getKitId(), user.getId()).orElseThrow(
            () -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_KIT_USER_NOT_FOUND)
        );
    }
}
