package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.EvidenceAttachment;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CreateEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.DeleteEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentFilePathPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaRepository;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ATTACHMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements
    CreateEvidenceAttachmentPort,
    CountEvidenceAttachmentsPort,
    LoadEvidenceAttachmentsPort,
    DeleteEvidenceAttachmentPort,
    LoadEvidenceAttachmentFilePathPort {

    private final EvidenceAttachmentJpaRepository repository;
    private final EvidenceJpaRepository evidenceRepository;

    @Override
    public UUID persist(EvidenceAttachment attachment) {
        if (!evidenceRepository.existsByIdAndDeletedFalse(attachment.getEvidenceId()))
            throw new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND);
        var unsavedEntity = EvidenceAttachmentMapper.mapToJpaEntity(attachment);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public int countAttachments(UUID evidenceId) {
        return repository.countByEvidenceId(evidenceId);
    }

    @Override
    public List<Result> loadEvidenceAttachments(UUID evidenceId) {
        if (!evidenceRepository.existsByIdAndDeletedFalse(evidenceId))
            throw new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND);

        return repository.findByEvidenceId(evidenceId).stream()
            .map(EvidenceAttachmentMapper::mapToLoadPortResult)
            .toList();
    }

    @Override
    public void deleteEvidenceAttachment(UUID attachmentId) {
        if (repository.findById(attachmentId).isEmpty())
            throw new ResourceNotFoundException(EVIDENCE_ATTACHMENT_ID_NOT_FOUND);

        repository.deleteById(attachmentId);
    }

    @Override
    public String loadEvidenceAttachmentFilePath(UUID attachmentId) {
        return repository.findById(attachmentId).map(EvidenceAttachmentJpaEntity::getFilePath)
            .orElseThrow(() -> new ResourceNotFoundException(EVIDENCE_ATTACHMENT_ID_NOT_FOUND));
    }
}
