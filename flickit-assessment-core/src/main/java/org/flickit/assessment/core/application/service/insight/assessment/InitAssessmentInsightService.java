package org.flickit.assessment.core.application.service.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.port.in.insight.assessment.InitAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;

@Service
@Transactional
@RequiredArgsConstructor
public class InitAssessmentInsightService implements InitAssessmentInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final UpdateAssessmentInsightPort updateAssessmentInsightPort;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;

    @Override
    public void initAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());

        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        int questionsCount = progress.questionsCount();
        int answersCount = progress.answersCount();
        int confidenceValue = assessmentResult.getConfidenceValue() != null ? (int) Math.ceil(assessmentResult.getConfidenceValue()) : 0;
        String maturityLevelTitle = assessmentResult.getMaturityLevel().getTitle();

        AssessmentKit kit = assessmentResult.getAssessment().getAssessmentKit();
        var locale = Locale.of(kit.getLanguage().getCode());
        String insight = (questionsCount == answersCount)
            ? MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED, locale, maturityLevelTitle, questionsCount, confidenceValue)
            : MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE, locale, maturityLevelTitle, answersCount, questionsCount, confidenceValue);

        assessmentInsight.ifPresentOrElse(
            existingInsight -> updateAssessmentInsightPort.updateInsight(toAssessmentInsight(existingInsight.getId(), assessmentResult.getId(), insight)),
            () -> createAssessmentInsightPort.persist(toAssessmentInsight(null, assessmentResult.getId(), insight))
        );
    }

    AssessmentInsight toAssessmentInsight(UUID assessmentInsightId, UUID assessmentResultId, String insight) {
        return new AssessmentInsight(assessmentInsightId,
            assessmentResultId,
            insight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);
    }
}
