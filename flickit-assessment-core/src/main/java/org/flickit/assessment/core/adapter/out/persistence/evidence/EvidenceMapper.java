package org.flickit.assessment.core.adapter.out.persistence.evidence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceMapper {

    public static EvidenceJpaEntity mapCreateParamToJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            param.creationTime(),
            param.lastModificationTime(),
            param.createdBy(),
            param.createdBy(),
            param.assessmentId(),
            param.questionRefNum(),
            param.type(),
            false
        );
    }

    public static EvidenceListItem toEvidenceListItem(EvidenceJpaEntity entity) {
        return new EvidenceListItem(
            entity.getId(),
            entity.getDescription(),
            entity.getCreatedBy(),
            entity.getAssessmentId(),
            entity.getType() != null ? EvidenceType.values()[entity.getType()].getTitle() : null,
            entity.getLastModificationTime()
        );
    }
}
