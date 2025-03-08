package org.flickit.assessment.core.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.answer.ApproveAssessmentAnswersUseCase;
import org.flickit.assessment.core.application.port.in.answer.ApproveAssessmentAnswersUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApproveAssessmentAnswersRestController {

    private final ApproveAssessmentAnswersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/approve-answers")
    ResponseEntity<Void> approveAssessmentAnswers(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();

        useCase.approveAllAnswers(toParam(assessmentId, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
