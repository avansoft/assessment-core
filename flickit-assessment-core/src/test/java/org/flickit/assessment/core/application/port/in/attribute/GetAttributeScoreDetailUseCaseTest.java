package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreDetailUseCaseTest {

    @Test
    void testGetAttributeScoreDetailUseCaseParam_AttributeLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_SCORE_DETAIL_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_MaturityLevelIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + GET_ATTRIBUTE_SCORE_DETAIL_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_SortParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.sort("invalid_sort")));
        assertThat(throwable).hasMessage("sort: " + GET_ATTRIBUTE_SCORE_DETAIL_SORT_INVALID);

        assertDoesNotThrow(() -> createParam(b -> b.sort("    weight     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    MISSED_SCORE     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    missed_Score     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    confIDence     ")));
        assertDoesNotThrow(() -> createParam(b -> b.sort("    eviDence_CounT     ")));
        var params = assertDoesNotThrow(() -> createParam(b -> b.sort(null)));
        assertEquals(GetAttributeScoreDetailUseCase.Param.Sort.DEFAULT.name(), params.getSort());
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_OrderParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.order("invalid_order")));
        assertThat(throwable).hasMessage("order: " + GET_ATTRIBUTE_SCORE_DETAIL_ORDER_INVALID);

        assertDoesNotThrow(() -> createParam(b -> b.order("    asc     ")));
        assertDoesNotThrow(() -> createParam(b -> b.order("    DESC     ")));
        var params = assertDoesNotThrow(() -> createParam(b -> b.order(null)));
        assertEquals(Order.DESC.name(), params.getOrder());
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_SizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(-1)));
        assertThat(throwable).hasMessage("size: " + GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MAX);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_PageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_ATTRIBUTE_SCORE_DETAIL_PAGE_MIN);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_CurrentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_SortItemNamesShouldNotBeChanged() {
        assertEquals("QUESTIONNAIRE", GetAttributeScoreDetailUseCase.Param.Sort.QUESTIONNAIRE.name());
        assertEquals("WEIGHT", GetAttributeScoreDetailUseCase.Param.Sort.WEIGHT.name());
        assertEquals("MISSED_SCORE", GetAttributeScoreDetailUseCase.Param.Sort.MISSED_SCORE.name());
        assertEquals("GAINED_SCORE", GetAttributeScoreDetailUseCase.Param.Sort.GAINED_SCORE.name());
        assertEquals("CONFIDENCE", GetAttributeScoreDetailUseCase.Param.Sort.CONFIDENCE.name());
        assertEquals("EVIDENCE_COUNT", GetAttributeScoreDetailUseCase.Param.Sort.EVIDENCE_COUNT.name());
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_SortItemTitlesShouldNotBeChanged() {
        assertEquals("questionnaire", GetAttributeScoreDetailUseCase.Param.Sort.QUESTIONNAIRE.getTitle());
        assertEquals("weight", GetAttributeScoreDetailUseCase.Param.Sort.WEIGHT.getTitle());
        assertEquals("missedScore", GetAttributeScoreDetailUseCase.Param.Sort.MISSED_SCORE.getTitle());
        assertEquals("gainedScore", GetAttributeScoreDetailUseCase.Param.Sort.GAINED_SCORE.getTitle());
        assertEquals("confidence", GetAttributeScoreDetailUseCase.Param.Sort.CONFIDENCE.getTitle());
        assertEquals("evidenceCount", GetAttributeScoreDetailUseCase.Param.Sort.EVIDENCE_COUNT.getTitle());
    }

    @Test
    void testGetAttributeScoreDetailUseCaseParam_SortItemSizeShouldNotBeChanged() {
        assertEquals(6, GetAttributeScoreDetailUseCase.Param.Sort.values().length);
    }

    private GetAttributeScoreDetailUseCase.Param createParam(Consumer<GetAttributeScoreDetailUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeScoreDetailUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeScoreDetailUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .maturityLevelId(1L)
            .size(50)
            .page(0)
            .sort("weight")
            .order("asc")
            .currentUserId(UUID.randomUUID());
    }
}
