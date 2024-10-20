package org.flickit.assessment.data.jpa.core.assessment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID>, JpaSpecificationExecutor<AssessmentJpaEntity> {

    @Query("""
            SELECT
                a as assessment,
                r as assessmentResult,
                k as assessmentKit,
                s as space
            FROM AssessmentJpaEntity a
            LEFT JOIN AssessmentResultJpaEntity r ON a.id = r.assessment.id
            LEFT JOIN AssessmentKitJpaEntity k ON k.id = a.assessmentKitId
            LEFT JOIN SpaceUserAccessJpaEntity sua ON sua.spaceId = a.spaceId
            LEFT JOIN SpaceJpaEntity s ON s.id = a.spaceId
            LEFT JOIN AssessmentUserRoleJpaEntity ur ON a.id = ur.assessmentId AND ur.userId = :userId
            WHERE sua.userId = :userId
                AND (a.assessmentKitId = :kitId OR :kitId IS NULL)
                AND a.deleted = FALSE
                AND r.lastModificationTime = (SELECT MAX(ar.lastModificationTime) FROM AssessmentResultJpaEntity ar WHERE ar.assessment.id = a.id)
                AND (s.ownerId = :userId OR (ur.roleId is not null AND ur.roleId != :associateRoleId))
            ORDER BY a.lastModificationTime DESC
        """)
    Page<ComparableAssessmentListItemView> findComparableAssessments(@Param("kitId") Long kitId,
                                                                     @Param("userId") UUID userId,
                                                                     @Param("associateRoleId") Integer associateRoleId,
                                                                     Pageable pageable);

    @Query("""
            SELECT
                a as assessment,
                r as assessmentResult,
                CASE
                    WHEN ur.roleId = :managerRoleId OR space.ownerId = :userId THEN TRUE ELSE FALSE
                END as manageable
            FROM AssessmentJpaEntity a
            LEFT JOIN AssessmentResultJpaEntity r ON a.id = r.assessment.id
            LEFT JOIN AssessmentUserRoleJpaEntity ur ON a.id = ur.assessmentId AND ur.userId = :userId
            LEFT JOIN SpaceJpaEntity space ON a.spaceId = space.id
            WHERE a.spaceId = :spaceId
                AND a.deleted=false
                AND r.lastModificationTime = (SELECT MAX(ar.lastModificationTime) FROM AssessmentResultJpaEntity ar WHERE ar.assessment.id = a.id)
                AND (space.ownerId = : userId OR ur.roleId is not null)
            ORDER BY a.lastModificationTime DESC
        """)
    Page<AssessmentJoinResultView> findBySpaceId(@Param("spaceId") Long spaceId,
                                                 @Param("managerRoleId") Integer managerRoleId,
                                                 @Param("userId") UUID userId,
                                                 Pageable pageable);

    @Modifying
    @Query("""
            UPDATE AssessmentJpaEntity a SET
                a.title = :title,
                a.shortTitle = :shortTitle,
                a.code = :code,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id
        """)
    void update(@Param(value = "id") UUID id,
                @Param(value = "title") String title,
                @Param(value = "shortTitle") String shortTitle,
                @Param(value = "code") String code,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE AssessmentJpaEntity a SET
                a.deletionTime = :deletionTime,
                a.deleted = true
            WHERE a.id = :id
        """)
    void delete(@Param(value = "id") UUID id, @Param(value = "deletionTime") Long deletionTime);

    boolean existsByIdAndDeletedFalse(@Param(value = "id") UUID id);

    @Query("""
            SELECT
                a AS assessment,
                k AS kit,
                s AS space
            FROM AssessmentJpaEntity a
            JOIN AssessmentKitJpaEntity k ON a.assessmentKitId = k.id
            JOIN SpaceJpaEntity s ON a.spaceId = s.id
            WHERE a.id = :id AND a.deleted = FALSE
        """)
    Optional<AssessmentKitSpaceJoinView> findByIdAndDeletedFalse(@Param(value = "id") UUID id);

    @Modifying
    @Query("""
        UPDATE AssessmentJpaEntity a SET
        a.lastModificationTime = :lastModificationTime
        WHERE a.id = :id
        """)
    void updateLastModificationTime(UUID id, LocalDateTime lastModificationTime);

    @Query("""
            SELECT a.id
            FROM AssessmentJpaEntity a
            WHERE a.id = :assessmentId AND a.deleted = FALSE AND
            EXISTS (
              SELECT 1 FROM SpaceUserAccessJpaEntity su
              WHERE a.spaceId = su.spaceId AND su.userId = :userId)
        """)
    Optional<UUID> checkIsAssessmentSpaceMember(@Param(value = "assessmentId") UUID assessmentId,
                                                @Param(value = "userId") UUID userId);

    @Query("""
        SELECT attr.id
        FROM AssessmentJpaEntity asm
        JOIN AssessmentKitJpaEntity kit
        ON asm.assessmentKitId = kit.id
        JOIN AttributeJpaEntity attr
        ON attr.kitVersionId = kit.kitVersionId
        WHERE asm.id = :assessmentId
        AND attr.id in :attributeIds
    """)
    Set<Long> findSelectedAttributeIdsRelatedToAssessment(UUID assessmentId, Set<Long> attributeIds);

    @Query("""
        SELECT level.id
        FROM AssessmentJpaEntity asm
        JOIN AssessmentKitJpaEntity kit
        ON asm.assessmentKitId = kit.id
        JOIN MaturityLevelJpaEntity level
        ON level.kitVersionId = kit.kitVersionId
        WHERE asm.id = :assessmentId
        AND level.id in :levelIds
    """)
    Set<Long> findSelectedLevelIdsRelatedToAssessment(UUID assessmentId, Set<Long> levelIds);
}



