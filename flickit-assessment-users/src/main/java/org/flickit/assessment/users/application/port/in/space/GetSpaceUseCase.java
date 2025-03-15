package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.application.domain.Space;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_SPACE_SPACE_ID_NOT_NULL;

public interface GetSpaceUseCase {

    Result getSpace(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_SPACE_SPACE_ID_NOT_NULL)
        Long id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Space space,
                  boolean editable,
                  int membersCount,
                  int assessmentsCount,
                  boolean canCreateAssessment) {
    }
}
