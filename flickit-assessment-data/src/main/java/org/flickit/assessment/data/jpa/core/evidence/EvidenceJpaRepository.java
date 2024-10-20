package org.flickit.assessment.data.jpa.core.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    @Query("""
            SELECT
                e.id as id,
                e.description as description,
                e.type as type,
                e.createdBy as createdBy,
                e.lastModificationTime as lastModificationTime,
                COUNT (a) as attachmentsCount
            FROM EvidenceJpaEntity e
            LEFT JOIN EvidenceAttachmentJpaEntity a on e.id = a.evidenceId
            WHERE e.questionId = :questionId AND e.assessmentId = :assessmentId AND e.deleted = false
            GROUP BY e.id, e.description, e.type, e.createdBy, e.lastModificationTime
        """)
    Page<EvidenceWithAttachmentsCountView> findByQuestionIdAndAssessmentId(@Param("questionId") Long questionId,
                                                                           @Param("assessmentId") UUID assessmentId,
                                                                           Pageable pageable);

    Optional<EvidenceJpaEntity> findByIdAndDeletedFalse(UUID id);

    @Modifying
    @Query("""
            UPDATE EvidenceJpaEntity e
            SET e.description = :description,
                e.type = :type,
                e.lastModificationTime = :lastModificationTime,
                e.lastModifiedBy = :lastModifiedBy
            WHERE e.id = :id
        """)
    void update(@Param(value = "id") UUID id,
                @Param(value = "description") String description,
                @Param(value = "type") Integer type,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE EvidenceJpaEntity e
            SET e.deleted = true
            WHERE e.id = :id
        """)
    void delete(@Param(value = "id") UUID id);

    @Query("""
            SELECT
                evd.id as id,
                evd.description as description,
                COUNT(eva.evidenceId) as attachmentsCount,
                evd.lastModificationTime as lastModificationTime
            FROM QuestionJpaEntity q
            LEFT JOIN EvidenceJpaEntity evd ON q.id = evd.questionId
            LEFT JOIN EvidenceAttachmentJpaEntity eva ON evd.id = eva.evidenceId
            WHERE evd.assessmentId = :assessmentId
                AND evd.type = :type
                AND evd.deleted = false
                AND q.id IN (SELECT qs.id
                             FROM QuestionJpaEntity qs
                             LEFT JOIN AssessmentResultJpaEntity ar ON qs.kitVersionId = ar.kitVersionId
                             LEFT JOIN QuestionImpactJpaEntity qi ON qs.id = qi.questionId AND qs.kitVersionId = qi.kitVersionId
                             WHERE qi.attributeId = :attributeId AND ar.assessment.id = :assessmentId)
            GROUP BY evd.description, evd.lastModificationTime, evd.id
        """)
    Page<MinimalEvidenceView> findAssessmentAttributeEvidencesByType(@Param(value = "assessmentId") UUID assessmentId,
                                                                     @Param(value = "attributeId") Long attributeId,
                                                                     @Param(value = "type") Integer type,
                                                                     Pageable pageable);

    boolean existsByIdAndDeletedFalse(UUID evidenceId);
}
