package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;

import java.util.UUID;

public interface LoadAttributeScoreDetailPort {

    PaginatedResponse<Result> loadScoreDetail(Param param);

    record Param(UUID assessmentId,
                 long attributeId,
                 long maturityLevelId,
                 GetAttributeScoreDetailUseCase.Param.Sort sort,
                 Order order,
                 int size,
                 int page) {}

    record Result(long questionnaireId,
                  String questionnaireTitle,
                  long questionId,
                  int questionIndex,
                  String questionTitle,
                  int questionWeight,
                  Double gainedScore,
                  Double missedScore,
                  Integer confidence,
                  int evidenceCount) {
    }
}
