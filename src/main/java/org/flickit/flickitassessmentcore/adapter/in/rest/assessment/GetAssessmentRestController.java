package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentRestController {

    private final GetAssessmentUseCase useCase;

    @GetMapping("/assessments/{assessmentId}")
    public ResponseEntity<GetAssessmentResponseDto> getAssessment(@PathVariable("assessmentId") UUID assessmentId) {
        var response = toResponse(useCase.getAssessment(new Param(assessmentId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAssessmentResponseDto toResponse(Result result) {
        return new GetAssessmentResponseDto(
            result.assessmentId(),
            result.assessmentTitle(),
            result.spaceId(),
            result.kitId());
    }
}
