package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE_ATTACHMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceAttachmentsService implements GetEvidenceAttachmentsUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final LoadEvidenceAttachmentsPort loadEvidenceAttachmentsPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Override
    public List<Attachment> getEvidenceAttachments(Param param) {
        var evidence = loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId());
        if (!assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), VIEW_EVIDENCE_ATTACHMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadEvidenceAttachmentsPort.loadEvidenceAttachments(param.getEvidenceId());
        return portResult
            .stream()
            .map(this::mapToEvidenceAttachmentsItem)
            .toList();
    }

    private Attachment mapToEvidenceAttachmentsItem(LoadEvidenceAttachmentsPort.Result attachment) {
        return new Attachment(
            attachment.id(),
            createFileDownloadLinkPort.createDownloadLink(attachment.filePath(), EXPIRY_DURATION),
            attachment.description(),
            attachment.createdBy(),
            attachment.creationTime());
    }
}
