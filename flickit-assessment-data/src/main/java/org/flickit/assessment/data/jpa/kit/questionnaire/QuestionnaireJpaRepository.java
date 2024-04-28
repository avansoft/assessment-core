package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> findAllByKitVersionIdOrderByIndex(Long kitVersionId);

    @Modifying
    @Query("""
        UPDATE QuestionnaireJpaEntity q
        SET q.title = :title,
        q.index = :index,
        q.description = :description,
        q.lastModificationTime = :lastModificationTime,
        q.lastModifiedBy = :lastModifiedBy
        WHERE q.id = :id
        """)
    void update(
        @Param(value = "id") long id,
        @Param(value = "title") String title,
        @Param(value = "index") int index,
        @Param(value = "description") String description,
        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
        @Param(value = "lastModifiedBy") UUID lastModifiedBy
    );

    @Query("""
            SELECT
                q.id as id,
                q.title as title,
                q.index as index,
                COUNT(DISTINCT question.id) as questionCount
            FROM QuestionnaireJpaEntity q
            JOIN QuestionJpaEntity question
                ON q.id = question.questionnaireId
            WHERE q.kitVersionId = :kitVersionId
            GROUP BY q.id
            ORDER BY q.index
        """)
    Page<QuestionnaireListItemView> findAllWithQuestionCountByKitVersionId(@Param(value = "kitVersionId") long kitVersionId, Pageable pageable);

    @Query("""
            SELECT qn
            FROM AssessmentKitJpaEntity k
                JOIN KitVersionJpaEntity kv ON k.id = kv.kit.id
                JOIN QuestionnaireJpaEntity qn ON qn.kitVersionId = kv.id
            WHERE qn.id = :questionnaireId AND k.id = :kitId
        """)
    Optional<QuestionnaireJpaEntity> findQuestionnaireByIdAndKitId(@Param("questionnaireId") Long questionnaireId, @Param("kitId") Long kitId);
}
