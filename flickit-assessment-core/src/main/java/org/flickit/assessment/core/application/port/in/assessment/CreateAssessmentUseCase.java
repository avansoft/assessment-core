package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.notification.HasNotificationCmd;
import org.flickit.assessment.core.application.domain.notification.CreateAssessmentNotificationCmd;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface CreateAssessmentUseCase {

    Result createAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = CREATE_ASSESSMENT_TITLE_NOT_BLANK)
        @Size(min = 3, message = CREATE_ASSESSMENT_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_ASSESSMENT_TITLE_SIZE_MAX)
        String title;

        @Size(min = 3, message = CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MIN)
        @Size(max = 20, message = CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX)
        String shortTitle;

        @NotNull(message = CREATE_ASSESSMENT_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotNull(message = CREATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long spaceId, String title, String shortTitle, Long kitId, UUID currentUserId) {
            this.title = title != null ? title.strip() : null;
            this.shortTitle = shortTitle != null && !shortTitle.isBlank() ? shortTitle.strip() : null;
            this.spaceId = spaceId;
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id, CreateAssessmentNotificationCmd notificationCmd) implements HasNotificationCmd {
    }
}
