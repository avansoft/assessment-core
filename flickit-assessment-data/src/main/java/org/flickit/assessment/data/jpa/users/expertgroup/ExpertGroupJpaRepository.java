package org.flickit.assessment.data.jpa.users.expertgroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpertGroupJpaRepository extends JpaRepository<ExpertGroupJpaEntity, Long> {

    @Query("SELECT e.ownerId FROM ExpertGroupJpaEntity as e where e.id = :id and deleted=false")
    Optional<UUID> loadOwnerIdById(@Param("id") Long id);

    @Query("""
            SELECT
                e.id as id,
                e.title as title,
                e.picture as picture,
                e.bio as bio,
                e.ownerId as ownerId,
                COUNT(DISTINCT CASE WHEN ak.published = true THEN ak.id ELSE NULL END) as publishedKitsCount,
                COUNT(DISTINCT ac.userId) as membersCount
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity ak on e.id = ak.expertGroupId
            LEFT JOIN ExpertGroupAccessJpaEntity ac on e.id = ac.expertGroupId
            WHERE EXISTS (
                SELECT 1 FROM ExpertGroupAccessJpaEntity ac
                WHERE ac.expertGroupId = e.id AND ac.userId = :userId
                    AND ac.status = 1
            )
            GROUP BY
                e.id,
                e.title,
                e.picture,
                e.bio,
                e.ownerId
        """)
    Page<ExpertGroupWithDetailsView> findByUserId(@Param(value = "userId") UUID userId, Pageable pageable);

    @Query("""
        SELECT
        u.displayName as displayName
        FROM ExpertGroupAccessJpaEntity a
        LEFT JOIN UserJpaEntity u on a.userId = u.id
        LEFT JOIN ExpertGroupJpaEntity e on a.expertGroupId = e.id
        WHERE a.status = 1 AND a.expertGroupId = :expertGroupId
        """)
    List<String> findMembersByExpertGroupId(@Param(value = "expertGroupId") Long expertGroupId, Pageable pageable);

    @Query("""
        SELECT
        e.userId as userId
        FROM ExpertGroupAccessJpaEntity e
        WHERE e.expertGroupId = :expertGroupId and e.status = 1
        """)
    List<UUID> findMemberIdsByExpertGroupId(@Param(value = "expertGroupId") Long expertGroupId);

    @Modifying
    @Query("""
        DELETE
        FROM ExpertGroupJpaEntity e
        WHERE e.id = :expertGroupId
        """)
    void delete(@Param("expertGroupId") Long expertGroupId);

    @Query("""
            SELECT
                COUNT(DISTINCT CASE WHEN ak.published = true THEN ak.id ELSE NULL END) as publishedKitsCount,
                COUNT(DISTINCT CASE WHEN ak.published = false THEN ak.id ELSE NULL END) as unPublishedKitsCount
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity ak on e.id = ak.expertGroupId
            WHERE e.id = :expertGroupId
        """)
    KitsCountView countKits(@Param("expertGroupId") long expertGroupId);

    Optional<ExpertGroupJpaEntity> findById(long id);

    boolean existsById(@Param(value = "id") long id);
}
