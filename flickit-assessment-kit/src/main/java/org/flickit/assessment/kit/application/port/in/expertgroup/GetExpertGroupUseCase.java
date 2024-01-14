package org.flickit.assessment.kit.application.port.in.expertgroup;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.ExpertGroup;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_EXPERT_GROUP_BY_ID_EXPERT_GROUP_ID_NOT_NULL;

public interface GetExpertGroupUseCase {

    ExpertGroup getExpertGroup(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {
        @NotNull(message = GET_EXPERT_GROUP_BY_ID_EXPERT_GROUP_ID_NOT_NULL)
        long id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;
    }

    record Member(String displayName) {
    }
}
