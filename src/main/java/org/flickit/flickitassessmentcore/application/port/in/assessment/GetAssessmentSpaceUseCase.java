package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_SPACE_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentSpaceUseCase {

    Result getAssessmentSpace(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_SPACE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record Result(Long spaceId) {
    }
}
