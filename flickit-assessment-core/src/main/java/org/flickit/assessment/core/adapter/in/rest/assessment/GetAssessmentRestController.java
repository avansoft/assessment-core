package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.in.rest.assessment.GetAssessmentResponseDto.User;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result;
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
            result.id(),
            result.title(),
            new GetAssessmentResponseDto.Space(result.space().getId(), result.space().getTitle()),
            new GetAssessmentResponseDto.Kit(result.kit().getId(), result.kit().getTitle()),
            result.creationTime(),
            result.lastModificationTime(),
            new User(result.createdBy().getId(), result.createdBy().getDisplayName()));
    }
}
