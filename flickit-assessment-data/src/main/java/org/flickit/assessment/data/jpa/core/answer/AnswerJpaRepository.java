package org.flickit.assessment.data.jpa.core.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface AnswerJpaRepository extends JpaRepository<AnswerJpaEntity, UUID> {

    Optional<AnswerJpaEntity> findByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);

    List<AnswerJpaEntity> findByAssessmentResultIdAndQuestionIdIn(UUID assessmentResultId, List<Long> questionId);

    List<AnswerJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Query("""
            SELECT COUNT(a) as answerCount
            FROM AnswerJpaEntity a
            WHERE a.assessmentResult.id=:assessmentResultId
                AND a.questionId IN :questionIds
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        """)
    int getCountByQuestionIds(@Param("assessmentResultId") UUID assessmentResultId, @Param("questionIds") List<Long> questionIds);

    @Query("""
            SELECT COUNT(a)
            FROM AnswerJpaEntity a
            WHERE a.assessmentResult.id=:assessmentResultId
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        """)
    int getCountByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE AnswerJpaEntity a
            SET a.answerOptionId = :answerOptionId,
                a.confidenceLevelId = :confidenceLevelId,
                a.isNotApplicable = :isNotApplicable,
                a.status = :status,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :answerId
        """)
    void update(@Param("answerId") UUID answerId,
                @Param("answerOptionId") Long answerOptionId,
                @Param("confidenceLevelId") Integer confidenceLevelId,
                @Param("isNotApplicable") Boolean isNotApplicable,
                @Param("status") Integer status,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT a.questionnaireId AS questionnaireId,
                COUNT(a.questionnaireId) AS answerCount
            FROM AnswerJpaEntity a
            WHERE a.assessmentResult.id=:assessmentResultId
                AND a.questionnaireId IN :questionnaireIds
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
            GROUP BY a.questionnaireId
        """)
    List<QuestionnaireIdAndAnswerCountView> getQuestionnairesProgressByAssessmentResultId(
        @Param(value = "assessmentResultId") UUID assessmentResultId,
        @Param(value = "questionnaireIds") List<Long> questionnaireIds);

    @Query("""
            SELECT COUNT(a)
            FROM AnswerJpaEntity a
            WHERE a.assessmentResult.id=:assessmentResultId
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
                AND a.confidenceLevelId < :confidence
        """)
    int countWithConfidenceLessThan(@Param("assessmentResultId") UUID assessmentResultId,
                                    @Param("confidence") int confidence);

    @Query("""
            SELECT q.questionnaireId AS questionnaireId,
                COUNT(a) AS answerCount
            FROM AnswerJpaEntity a
            LEFT JOIN QuestionJpaEntity q ON a.questionnaireId = q.questionnaireId AND a.questionId = q.id
            JOIN AssessmentResultJpaEntity ar ON a.assessmentResult.id = ar.id AND ar.kitVersionId = q.kitVersionId
            WHERE a.assessmentResult.id=:assessmentResultId
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
                AND a.confidenceLevelId < :confidence
                AND a.questionnaireId in :questionnaireIds
            GROUP BY q.questionnaireId
        """)
    List<QuestionnaireIdAndAnswerCountView> countByQuestionnaireIdWithConfidenceLessThan(@Param("assessmentResultId") UUID assessmentResultId,
                                                                                         @Param("questionnaireIds") Collection<Long> questionnaireId,
                                                                                         @Param("confidence") int confidence);

    @Modifying
    @Query("""
            UPDATE AnswerJpaEntity a
            SET a.status = :approvedStatusId,
                a.lastModifiedBy = :approvedBy
            WHERE a.id = :answerId
        """)
    void approve(@Param("answerId") UUID answerId,
                 @Param("approvedBy") UUID approvedBy,
                 @Param("approvedStatusId") int approvedStatusId);

    @Query("""
            SELECT COUNT(a)
            FROM AnswerJpaEntity a
            WHERE a.assessmentResult.id = :assessmentResultId
                AND (a.status = :status)
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        """)
    int countUnapprovedAnswersByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId,
                                                   @Param("status") Integer status);

    @Query("""
            SELECT q.questionnaireId as questionnaireId,
                COUNT(q) as count
            FROM AnswerJpaEntity a
            JOIN QuestionJpaEntity q ON a.questionId = q.id
            JOIN AssessmentResultJpaEntity r ON a.assessmentResult.id = r.id AND q.kitVersionId = r.kitVersionId
            WHERE a.assessmentResult.id = :assessmentResultId
                AND q.questionnaireId IN :questionnaireIds
                AND (a.status = :status)
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
            GROUP BY questionnaireId
        """)
    List<AnswersQuestionnaireAndCountView> countQuestionnairesUnapprovedAnswers(@Param("assessmentResultId") UUID assessmentResultId,
                                                                                @Param("questionnaireIds") Set<Long> questionnaireIds,
                                                                                @Param("status") Integer status);

    @Modifying
    @Query("""
            UPDATE AnswerJpaEntity a
                SET a.status = :status,
                    a.lastModifiedBy = :lastModifiedBy
            WHERE a.assessmentResult.id = :assessmentResultId
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
        """)
    void approveByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId,
                                     @Param("lastModifiedBy") UUID lastModifiedBy,
                                     @Param("status") Integer status);
}
