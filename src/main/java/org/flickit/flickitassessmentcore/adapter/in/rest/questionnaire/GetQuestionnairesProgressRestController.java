package org.flickit.flickitassessmentcore.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionnairesProgressRestController {

    private final GetQuestionnairesProgressUseCase useCase;

    @GetMapping("assessments/{assessmentId}/questionnaires-progress")
    ResponseEntity<GetQuestionnairesProgressResponseDto> getResult(@PathVariable("assessmentId") UUID assessmentId) {
        return new ResponseEntity<>(toResponseDto(useCase.getQuestionnairesProgress(toParam(assessmentId))), HttpStatus.OK);
    }

    private GetQuestionnairesProgressResponseDto toResponseDto(Result result) {
        return new GetQuestionnairesProgressResponseDto(result.assessmentProgress(), result.questionnairesProgress());
    }

    private GetQuestionnairesProgressUseCase.Param toParam(UUID assessmentId) {
        return new GetQuestionnairesProgressUseCase.Param(assessmentId);
    }
}
