package org.flickit.assessment.data.jpa.core.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerJpaRepository extends JpaRepository<AnswerJpaEntity, UUID> {

    Optional<AnswerJpaEntity> findByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);

    List<AnswerJpaEntity> findByAssessmentResultIdAndQuestionIdIn(UUID assessmentResultId, List<Long> questionId);

    List<AnswerJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Query("""
        SELECT COUNT(a) as answerCount FROM AnswerJpaEntity a
        WHERE a.assessmentResult.id=:assessmentResultId AND a.questionId IN :questionIds AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        """)
    int getCountByQuestionIds(UUID assessmentResultId, List<Long> questionIds);

    @Query("""
            SELECT COUNT(a)
            FROM AnswerJpaEntity a
            WHERE a.assessmentResult.id=:assessmentResultId
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        """)
    int getCountByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId);

    @Modifying
    @Query("""
        UPDATE AnswerJpaEntity a SET
        a.answerOptionId = :answerOptionId,
        a.confidenceLevelId = :confidenceLevelId,
        a.isNotApplicable = :isNotApplicable,
        a.lastModifiedBy = :currentUserId
        WHERE a.id = :answerId
        """)
    void update(UUID answerId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable, UUID currentUserId);

    @Query("""
        SELECT a.questionnaireId AS questionnaireId, COUNT(a.questionnaireId) AS answerCount
        FROM AnswerJpaEntity a
        WHERE a.assessmentResult.id=:assessmentResultId AND a.questionnaireId IN :questionnaireIds
            AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        GROUP BY a.questionnaireId
        """)
    List<QuestionnaireIdAndAnswerCountView> getQuestionnairesProgressByAssessmentResultId(
        @Param(value = "assessmentResultId") UUID assessmentResultId,
        @Param(value = "questionnaireIds") List<Long> questionnaireIds);
}
