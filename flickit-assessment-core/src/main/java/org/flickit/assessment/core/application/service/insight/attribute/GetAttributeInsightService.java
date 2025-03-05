package org.flickit.assessment.core.application.service.insight.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.insight.attribute.GetAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class GetAttributeInsightService implements GetAttributeInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;

    @Override
    public Result getInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT);
        var attributeInsight = loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId());

        if (attributeInsight.isEmpty())
            return new Result(null, null, editable, null);

        var insight = attributeInsight.get();

        if (insight.getAssessorInsight() == null ||
            (insight.getAiInsightTime() != null && insight.getAiInsightTime().isAfter(insight.getAssessorInsightTime()))) {
            Result.Insight aiInsight = new Result.Insight(insight.getAiInsight(),
                insight.getAiInsightTime(),
                isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime()));
            return new Result(aiInsight, null, editable, insight.isApproved());
        }
        Result.Insight assessorInsight = new Result.Insight(insight.getAssessorInsight(),
            insight.getAssessorInsightTime(),
            isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime()));
        return new Result(null, assessorInsight, editable, insight.isApproved());
    }

    private static boolean isValid(LocalDateTime lastCalculationTime, LocalDateTime insightLastModificationTime) {
        return lastCalculationTime.isBefore(insightLastModificationTime);
    }
}
