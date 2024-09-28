package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateAttributeUseCase {

    long createAttribute(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ATTRIBUTE_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = CREATE_ATTRIBUTE_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = CREATE_ATTRIBUTE_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_ATTRIBUTE_TITLE_MIN_SIZE)
        @Size(max = 100, message = CREATE_ATTRIBUTE_TITLE_MAX_SIZE)
        String title;

        @NotBlank(message = CREATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK)
        String description;

        @NotNull(message = CREATE_ATTRIBUTE_WEIGHT_NOT_NULL)
        Integer weight;

        @NotNull(message = CREATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId,
                     Integer index,
                     String title,
                     String description,
                     Integer weight,
                     Long subjectId, UUID currentUserId) {
            this.kitId = kitId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.description = description;
            this.weight = weight;
            this.subjectId = subjectId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
