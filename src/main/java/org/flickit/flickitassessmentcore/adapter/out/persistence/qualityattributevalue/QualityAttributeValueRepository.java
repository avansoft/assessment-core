package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QualityAttributeValueRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {
}
