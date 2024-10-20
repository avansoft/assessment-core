package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.User;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;

public interface GetEvidenceUseCase {

    Result getEvidence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_ID_NOT_NULL)
        UUID id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Evidence evidence, Question question, QuestionAnswer answer, User user) {
    }

    record Option(Long id, Integer index, String title) {
    }

    record QuestionAnswer(Option selectedOption, ConfidenceLevel confidenceLevel, Boolean isNotApplicable) {
    }
}
