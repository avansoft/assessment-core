package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerCommand;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final LoadAnswerByAssessmentResultIdAndQuestionIdPort loadAnswerPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;
    private final CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort checkAnswerExistencePort;

    @Override
    public UUID submitAnswer(SubmitAnswerCommand command) {
        SaveOrUpdateResponse response = saveOrUpdate(command);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateById(command.getAssessmentResultId());
        return response.answerId();
    }

    private SaveOrUpdateResponse saveOrUpdate(SubmitAnswerCommand command) {
        boolean exists = checkAnswerExistencePort.existsByAssessmentResultIdAndQuestionId(command.getAssessmentResultId(), command.getQuestionId());
        if (exists) {
            return update(command);
        }
        return new SaveOrUpdateResponse(true, save(command));
    }

    private UUID save(SubmitAnswerCommand command) {
        return saveAnswerPort.persist(toSaveParam(command));
    }

    private SaveOrUpdateResponse update(SubmitAnswerCommand command) {
        Answer answer = loadAnswerPort.loadByAssessmentResultIdAndQuestionId(command.getAssessmentResultId(), command.getQuestionId());
        if (answerHasChanged(command, answer)) {
            updateAnswerOptionPort.updateAnswerOptionById(toUpdateParam(answer.getId(), command));
            return new SaveOrUpdateResponse(true, answer.getId());
        }
        return new SaveOrUpdateResponse(false, answer.getId());
    }

    private boolean answerHasChanged(SubmitAnswerCommand command, Answer answer) {
        return !Objects.equals(command.getAnswerOptionId(), answer.getAnswerOptionId());
    }

    private SaveAnswerPort.Param toSaveParam(SubmitAnswerCommand command) {
        return new SaveAnswerPort.Param(
            command.getAssessmentResultId(),
            command.getQuestionId(),
            command.getAnswerOptionId()
        );
    }

    private UpdateAnswerOptionPort.Param toUpdateParam(UUID id, SubmitAnswerCommand command) {
        return new UpdateAnswerOptionPort.Param(id, command.getAnswerOptionId());
    }

    record SaveOrUpdateResponse(boolean hasChanged, UUID answerId) {
    }
}
