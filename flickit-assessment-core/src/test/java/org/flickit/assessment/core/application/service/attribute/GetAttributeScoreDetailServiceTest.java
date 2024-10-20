package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.Questionnaire;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.flickit.assessment.core.test.fixture.application.QuestionScoreMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_SCORE_DETAIL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.Param;
import static org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.QuestionScore;
import static org.flickit.assessment.core.test.fixture.application.QuestionScoreMother.questionWithScore;
import static org.flickit.assessment.core.test.fixture.application.QuestionScoreMother.questionWithoutAnswer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreDetailServiceTest {

    @InjectMocks
    private GetAttributeScoreDetailService service;

    @Mock
    private LoadAttributeScoreDetailPort loadAttributeScoreDetailPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAttributeScoreDetail_ValidParam() {
        UUID assessmentId = UUID.randomUUID();
        long attributeId = 123L;
        long maturityLevelId = 124L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            assessmentId,
            attributeId,
            maturityLevelId,
            currentUserId);

        QuestionScore questionWithFullScore = questionWithScore(4, 1.0);
        QuestionScore questionWithHalfScore = questionWithScore(2, 0.5);
        QuestionScore questionWithoutScore = questionWithScore(1, 0.0);
        QuestionScore questionWithoutAnswer = questionWithoutAnswer(4);
        QuestionScore questionMarkedAsNotApplicable = QuestionScoreMother.questionMarkedAsNotApplicable();

        Questionnaire devOpsQuestionnaire = new Questionnaire("DevOps",
            List.of(questionWithFullScore, questionWithHalfScore));
        Questionnaire testQuestionnaire = new Questionnaire("Test",
            List.of(questionWithoutScore, questionWithoutAnswer, questionMarkedAsNotApplicable));

        List<Questionnaire> questionnaires = List.of(devOpsQuestionnaire, testQuestionnaire);

        when(loadAttributeScoreDetailPort.loadScoreDetail(assessmentId, attributeId, maturityLevelId)).thenReturn(questionnaires);
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(true);

        GetAttributeScoreDetailUseCase.Result result = service.getAttributeScoreDetail(param);

        assertNotNull(result);
        assertEquals(5, result.gainedScore());
        assertEquals(11, result.maxPossibleScore());
        assertEquals(5.0 / 11.0, result.gainedScorePercentage());
        assertEquals(5, result.questionsCount());
        assertEquals(2, result.questionnaires().size());
        //order of QuestionScore items should be equal to order of port items
        assertEquals(questionWithFullScore, result.questionnaires().getFirst().questionScores().getFirst());
        assertEquals(questionWithHalfScore, result.questionnaires().getFirst().questionScores().get(1));
        assertEquals(questionWithoutScore, result.questionnaires().get(1).questionScores().getFirst());
        assertEquals(questionWithoutAnswer, result.questionnaires().get(1).questionScores().get(1));
        assertEquals(questionMarkedAsNotApplicable, result.questionnaires().get(1).questionScores().get(2));
    }

    @Test
    void testGetAttributeScoreDetail_ValidParam_NoQuestionScore() {
        UUID assessmentId = UUID.randomUUID();
        long attributeId = 123L;
        long maturityLevelId = 124L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            assessmentId,
            attributeId,
            maturityLevelId,
            currentUserId);

        List<Questionnaire> questionnaires = List.of();
        when(loadAttributeScoreDetailPort.loadScoreDetail(assessmentId, attributeId, maturityLevelId)).thenReturn(questionnaires);
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(true);

        GetAttributeScoreDetailUseCase.Result result = service.getAttributeScoreDetail(param);

        assertNotNull(result);
        assertEquals(0, result.gainedScore());
        assertEquals(0, result.maxPossibleScore());
        assertTrue(result.questionnaires().isEmpty());
    }

    @Test
    void testGetAttributeScoreDetail_InvalidCurrentUser_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        long attributeId = 123L;
        long maturityLevelId = 124L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            assessmentId,
            attributeId,
            maturityLevelId,
            currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAttributeScoreDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
