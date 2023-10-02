package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateEvidenceService implements UpdateEvidenceUseCase {

    private final UpdateEvidencePort updateEvidencePort;

    @Override
    public Result updateEvidence(Param param) {
        var updateParam = new UpdateEvidencePort.Param(
            param.getId(),
            param.getDescription(),
            LocalDateTime.now()
        );
        return new UpdateEvidenceUseCase.Result(updateEvidencePort.update(updateParam).id());
    }
}
