package org.flickit.assessment.advice.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL = "create-advice.assessmentId.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_SCORES_NOT_NULL = "create-advice.attributeLevelScores.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_SCORES_SIZE_MIN = "create-advice.attributeLevelScores.size.min";
    public static final String CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND = "create-advice.assessmentResult.notFound";
    public static final String CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID = "create-advice.assessmentResult.notValid";
    public static final String CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION = "create-advice.finding-best-solution.execution";
}
