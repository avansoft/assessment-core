package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitMapper {

    public static AssessmentKit mapToDomainModel(AssessmentKitJpaEntity entity, List<MaturityLevel> maturityLevels) {
        return new AssessmentKit(
            entity.getId(),
            entity.getTitle(),
            entity.getKitVersionId(),
            KitLanguage.valueOfById(entity.getLanguageId()),
            maturityLevels,
            entity.getIsPrivate()
        );
    }
}
