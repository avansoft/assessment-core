package org.flickit.assessment.core.application.port.in.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAttributeScoreDetailUseCase {

    PaginatedResponse<Result> getAttributeScoreDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_SCORE_DETAIL_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @EnumValue(enumClass = Order.class, message = GET_ATTRIBUTE_SCORE_DETAIL_ORDER_INVALID)
        String order;

        @EnumValue(enumClass = Sort.class, message = GET_ATTRIBUTE_SCORE_DETAIL_SORT_INVALID)
        String sort;

        @Min(value = 1, message = GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MIN)
        @Max(value = 100, message = GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ATTRIBUTE_SCORE_DETAIL_PAGE_MIN)
        int page;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long attributeId, Long maturityLevelId, String sort, String order, Integer size, Integer page, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.maturityLevelId = maturityLevelId;
            this.sort = sort != null && !sort.isBlank() ? sort.strip().toUpperCase() : Sort.DEFAULT.name();
            this.order = order != null && !order.isBlank() ? order.strip().toUpperCase() : Order.DESC.name();
            this.size = size;
            this.page = page;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }

        @Getter
        @RequiredArgsConstructor
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Sort {
            QUESTIONNAIRE("questionnaire"),
            WEIGHT("weight"),
            GAINED_SCORE("gainedScore"),
            MISSED_SCORE("missedScore"),
            CONFIDENCE("confidence"),
            EVIDENCE_COUNT("evidenceCount");

            private final String title;

            public static final Sort DEFAULT = WEIGHT;
        }
    }

    record Result(Questionnaire questionnaire, Question question, Answer answer) {

        public record Questionnaire(long id, String title) {
        }

        public record Question(long id, int index, String title, int weight, int evidenceCount) {
        }

        public record Answer(Integer index,
                             String title,
                             Boolean isNotApplicable,
                             Double gainedScore,
                             Double missedScore,
                             Integer confidenceLevel) {
        }
    }
}
