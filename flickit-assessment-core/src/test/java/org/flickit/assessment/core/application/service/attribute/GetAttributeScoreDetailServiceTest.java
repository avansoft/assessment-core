package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoresPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_SCORE_DETAIL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.Param;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreDetailServiceTest {

    @InjectMocks
    private GetAttributeScoreDetailService service;

    @Mock
    private LoadAttributeScoreDetailPort loadAttributeScoreDetailPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributeScoresPort loadAttributeScoresPort;

    @Test
    void testGetAttributeScoreDetail_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAttributeScoreDetailUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAttributeScoreDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributeScoreDetailPort, loadAttributeScoresPort);
    }

    @Test
    void testGetAttributeScoreDetail_ValidParam() {
        var param = createParam(GetAttributeScoreDetailUseCase.Param.ParamBuilder::build);

        var questionWithFullScore = questionWithScore(4, 1.0);
        var questionWithHalfScore = questionWithScore(2, 0.5);
        var questionWithoutScore = questionWithScore(1, 0.0);
        var questionWithoutAnswer = questionWithoutAnswer();
        var questionMarkedAsNotApplicable = questionMarkedAsNotApplicable();
        PaginatedResponse<LoadAttributeScoreDetailPort.Result> portResult = new PaginatedResponse<>(
            List.of(questionWithFullScore, questionWithHalfScore, questionWithoutScore, questionWithoutAnswer, questionMarkedAsNotApplicable),
            1,
            10,
            "title",
            "desc",
            5
        );

        when(loadAttributeScoreDetailPort.loadScoreDetail(any())).thenReturn(portResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(true);

        var result = service.getAttributeScoreDetail(param);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(portResult.getItems().size(), result.getItems().size());
        assertPaginationProperties(portResult, result);

        assertThat(result.getItems())
            .zipSatisfy(portResult.getItems(), (actual, expected) -> {
                assertEquals(expected.gainedScore(), actual.answer().gainedScore());
                assertEquals(expected.missedScore(), actual.answer().missedScore());
                assertEquals(expected.confidence(), actual.answer().confidenceLevel());
            });
    }

    @Test
    void testGetAttributeScoreDetail_ValidParam_NoQuestionScore() {
        var param = createParam(GetAttributeScoreDetailUseCase.Param.ParamBuilder::build);

        PaginatedResponse<LoadAttributeScoreDetailPort.Result> portResult = new PaginatedResponse<>(
            List.of(),
            0,
            10,
            "title",
            "desc",
            0
        );
        when(loadAttributeScoreDetailPort.loadScoreDetail(any())).thenReturn(portResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL))
            .thenReturn(true);

        var result = service.getAttributeScoreDetail(param);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(portResult.getItems().size(), result.getItems().size());
        assertPaginationProperties(portResult, result);
    }

    private void assertPaginationProperties(PaginatedResponse<LoadAttributeScoreDetailPort.Result> portResult,
                                            PaginatedResponse<GetAttributeScoreDetailUseCase.Result> result) {
        assertAll(
            () -> assertEquals(portResult.getSize(), result.getSize()),
            () -> assertEquals(portResult.getTotal(), result.getTotal()),
            () -> assertEquals(portResult.getOrder(), result.getOrder()),
            () -> assertEquals(portResult.getPage(), result.getPage()),
            () -> assertEquals(portResult.getSort(), result.getSort())
        );
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeScoreDetailUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeScoreDetailUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .maturityLevelId(1L)
            .sort("weight")
            .order("asc")
            .size(50)
            .page(0)
            .currentUserId(UUID.randomUUID());
    }

    private LoadAttributeScoreDetailPort.Result questionWithScore(int weight, double score) {
        return new LoadAttributeScoreDetailPort.Result(
            333L,
            "title",
            123L,
            1,
            "Do you have CI/CD?",
            weight,
            weight * score,
            score,
            1,
            2);
    }

    private LoadAttributeScoreDetailPort.Result questionWithoutAnswer() {
        return new LoadAttributeScoreDetailPort.Result(
            333L,
            "title",
            124L,
            1,
            "Do you have CI/CD?",
            4,
            null,
            null,
            1,
            3);
    }

    private LoadAttributeScoreDetailPort.Result questionMarkedAsNotApplicable() {
        return new LoadAttributeScoreDetailPort.Result(
            333L,
            "title",
            125L,
            1,
            "Do you have CI/CD?",
            1,
            null,
            null,
            1,
            0);
    }
}
