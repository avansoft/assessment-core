package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.answer.ApproveAssessmentAnswersUseCase;
import org.flickit.assessment.core.application.port.out.answer.ApproveAnswerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ANSWERS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAssessmentAnswersService implements ApproveAssessmentAnswersUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ApproveAnswerPort approveAnswerPort;

    @Override
    public void approveAllAnswers(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveAnswerPort.approveAll(param.getAssessmentId(), param.getCurrentUserId());
    }
}
