package org.flickit.assessment.core.application.port.out.attribute;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeScoreStatsPort {

    List<Result> loadScoreStats(UUID assessmentId, long attributeId, long maturityLevelId);

    record Result(Long questionId,
                  Double questionWeight,
                  Double answerScore,
                  Boolean answerIsNotApplicable) {
    }
}
