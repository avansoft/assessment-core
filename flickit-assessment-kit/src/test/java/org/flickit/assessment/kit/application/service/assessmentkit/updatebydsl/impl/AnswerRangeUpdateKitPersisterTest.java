package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_ANSWER_RANGES;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createReusableAnswerRangeWithTwoOptions;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithAnswerRanges;
import static org.flickit.assessment.kit.test.fixture.application.dsl.AnswerRangeDslModelMother.domainToDslModel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerRangeUpdateKitPersisterTest {

    @InjectMocks
    private AnswerRangeUpdateKitPersister persister;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Mock
    private UpdateAnswerRangePort updateAnswerRangePort;

    @Test
    void testOrder() {
        assertEquals(5, persister.order());
    }

    @Test
    void testPersist_TwoAnswerRangesWithoutAnyChange_NoUpdate() {
        var rangeOne = createReusableAnswerRangeWithTwoOptions();
        var rangeTwo = createReusableAnswerRangeWithTwoOptions();
        AssessmentKit savedKit = kitWithAnswerRanges(List.of(rangeOne, rangeTwo));

        var dslRangeOne = domainToDslModel(rangeOne);
        var dslRangeTwo = domainToDslModel(rangeTwo);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .answerRanges(List.of(dslRangeOne, dslRangeTwo))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        ArgumentCaptor<UpdateAnswerRangePort.Param> captor = ArgumentCaptor.forClass(UpdateAnswerRangePort.Param.class);
        Mockito.verify(updateAnswerRangePort, Mockito.times(0)).update(captor.capture());
        assertFalse(result.isMajorUpdate());

        Map<String, Long> codeToIdMap = ctx.get(KEY_ANSWER_RANGES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());
    }

    @Test
    void testPersist_SameRangeCodesWithDifferentTitles_Update() {
        var rangeOne = createReusableAnswerRangeWithTwoOptions();
        var rangeTwo = createReusableAnswerRangeWithTwoOptions();
        AssessmentKit savedKit = kitWithAnswerRanges(List.of(rangeOne, rangeTwo));

        var dslRangeOne = domainToDslModel(rangeOne, b -> b.title("new title2"));
        var dslRangeTwo = domainToDslModel(rangeTwo, b -> b.title("new title3"));
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .answerRanges(List.of(dslRangeOne, dslRangeTwo))
            .build();

        doNothing().when(updateAnswerRangePort).update(any());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        var currentUserId = UUID.randomUUID();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, currentUserId);

        ArgumentCaptor<UpdateAnswerRangePort.Param> param = ArgumentCaptor.forClass(UpdateAnswerRangePort.Param.class);
        verify(updateAnswerRangePort, times(2)).update(param.capture());

        List<UpdateAnswerRangePort.Param> paramList = param.getAllValues();
        UpdateAnswerRangePort.Param firstRange = paramList.getFirst();
        UpdateAnswerRangePort.Param secondRange = paramList.get(1);

        assertEquals(rangeOne.getId(), firstRange.answerRangeId());
        assertEquals(dslRangeOne.getTitle(), firstRange.title());
        assertEquals(savedKit.getActiveVersionId(), firstRange.kitVersionId());
        assertThat(firstRange.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertEquals(currentUserId, firstRange.lastModifiedBy());

        assertEquals(rangeTwo.getId(), secondRange.answerRangeId());
        assertEquals(savedKit.getActiveVersionId(), secondRange.kitVersionId());
        assertEquals(dslRangeTwo.getTitle(), secondRange.title());
        assertThat(secondRange.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertEquals(currentUserId, secondRange.lastModifiedBy());

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_ANSWER_RANGES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());
    }

    @Test
    void testPersist_DslHasOneNewAnswerRange_SaveNewAnswerRange() {
        var rangeOne = createReusableAnswerRangeWithTwoOptions();
        var rangeTwo = createReusableAnswerRangeWithTwoOptions();
        var savedKit = kitWithAnswerRanges(List.of(rangeOne, rangeTwo));

        var rangeThree = createReusableAnswerRangeWithTwoOptions();

        var dslRangeOne = domainToDslModel(rangeOne);
        var dslRangeTwo = domainToDslModel(rangeTwo);
        var dslRangeThree = domainToDslModel(rangeThree);

        var dslKit = AssessmentKitDslModel.builder()
            .answerRanges(List.of(dslRangeOne, dslRangeTwo, dslRangeThree))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        var currentUserId = UUID.randomUUID();

        CreateAnswerRangePort.Param createParam = new CreateAnswerRangePort.Param(savedKit.getActiveVersionId(),
            dslRangeThree.getTitle(), dslRangeThree.getCode(), true, currentUserId);
        long newRangeId = 543L;
        when(createAnswerRangePort.persist(createParam)).thenReturn(newRangeId);

        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, currentUserId);

        ArgumentCaptor<CreateAnswerRangePort.Param> createParamCaptor = ArgumentCaptor.forClass(CreateAnswerRangePort.Param.class);
        Mockito.verify(createAnswerRangePort, Mockito.times(1)).persist(createParamCaptor.capture());

        assertEquals(dslRangeThree.getCode(), createParamCaptor.getValue().code());
        assertEquals(dslRangeThree.getTitle(), createParamCaptor.getValue().title());
        assertEquals(savedKit.getActiveVersionId(), createParamCaptor.getValue().kitVersionId());
        assertEquals(currentUserId, createParamCaptor.getValue().createdBy());
        assertTrue(createParamCaptor.getValue().reusable());
        assertTrue(result.isMajorUpdate());

        Map<String, Long> codeToIdMap = ctx.get(KEY_ANSWER_RANGES);
        assertNotNull(codeToIdMap);
        assertTrue(codeToIdMap.containsKey(rangeThree.getCode()));
        assertEquals(3, codeToIdMap.keySet().size());
    }
}
