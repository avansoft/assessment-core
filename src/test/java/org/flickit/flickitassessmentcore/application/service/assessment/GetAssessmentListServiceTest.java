package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentKitMother;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentMother;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentListServiceTest {

    @InjectMocks
    private GetAssessmentListService service;

    @Mock
    private LoadAssessmentListItemsBySpacePort loadAssessmentPort;

    @Test
    void testGetAssessmentList_ResultsFoundForSingleSpace_ItemsReturned() {
        Long spaceId = 1L;
        Long kitId = AssessmentKitMother.kit().getId();
        List<Long> spaceIds = List.of(spaceId);
        AssessmentListItem assessment1S1 = AssessmentMother.assessmentListItem(spaceId, kitId);
        AssessmentListItem assessment2S1 = AssessmentMother.assessmentListItem(spaceId, kitId);

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            List.of(assessment1S1, assessment2S1),
            0,
            2,
            "lastModificationTime",
            "DESC",
            2);
        when(loadAssessmentPort.loadAssessments(spaceIds, null, 0L, 0, 10)).thenReturn(paginatedResponse);

        PaginatedResponse<AssessmentListItem> result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceIds, null, 10, 0));
        assertEquals(paginatedResponse, result);
    }

    @Test
    void testGetAssessmentList_NoResultsFound_NoItemReturned() {
        List<Long> spaceIds = List.of(2L);

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            new ArrayList<>(),
            0,
            0,
            "lastModificationTime",
            "DESC",
            2);
        when(loadAssessmentPort.loadAssessments(spaceIds, null, 0L, 0, 10)).thenReturn(paginatedResponse);

        PaginatedResponse<AssessmentListItem> result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceIds, null, 10, 0));
        assertEquals(paginatedResponse, result);
    }

    @Test
    void testGetAssessmentList_ResultsFoundForSpaceIds_ItemsReturned() {
        Long kitId = AssessmentKitMother.kit().getId();
        var assessment1 = AssessmentMother.assessmentListItem(1L, kitId);
        var assessment2 = AssessmentMother.assessmentListItem(2L, kitId);

        var spaceIds = List.of(assessment1.spaceId(), assessment2.spaceId());
        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2),
            0,
            20,
            AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            2
        );

        when(loadAssessmentPort.loadAssessments(spaceIds, null, 0L, 0, 20))
            .thenReturn(paginatedRes);

        var param = new GetAssessmentListUseCase.Param(spaceIds, null, 0L, 20, 0);
        var assessments = service.getAssessmentList(param);

        ArgumentCaptor<List<Long>> spaceIdsArgument = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Long> kitIdArgument = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> sizeArgument = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pageArgument = ArgumentCaptor.forClass(Integer.class);
        verify(loadAssessmentPort).loadAssessments(
            spaceIdsArgument.capture(),
            kitIdArgument.capture(),
            pageArgument.capture(),
            sizeArgument.capture());

        assertEquals(spaceIds, spaceIdsArgument.getValue());
        assertNull(kitIdArgument.getValue());
        assertEquals(20, sizeArgument.getValue());
        assertEquals(0, pageArgument.getValue());

        assertEquals(2, assessments.getItems().size());
        assertEquals(20, assessments.getSize());
        assertEquals(0, assessments.getPage());
        assertEquals(2, assessments.getTotal());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), assessments.getOrder());
        assertEquals(AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME, assessments.getSort());

        verify(loadAssessmentPort, times(1)).loadAssessments(any(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetAssessmentList_ResultsFoundForSpaceIdsWithKit_ItemsReturned() {
        Long spaceId = 1L;
        Long kitId = AssessmentKitMother.kit().getId();
        var assessment1 = AssessmentMother.assessmentListItem(1L, kitId);
        var assessment2 = AssessmentMother.assessmentListItem(1L, kitId);

        var spaceIds = List.of(spaceId);
        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2),
            0,
            20,
            AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            2
        );

        when(loadAssessmentPort.loadAssessments(spaceIds, kitId, 0L, 0, 20)).thenReturn(paginatedRes);

        var param = new GetAssessmentListUseCase.Param(spaceIds, kitId, 0L, 20, 0);
        var assessments = service.getAssessmentList(param);

        assertEquals(2, assessments.getItems().size());
        assertEquals(20, assessments.getSize());
        assertEquals(0, assessments.getPage());
        assertEquals(2, assessments.getTotal());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), assessments.getOrder());
        assertEquals(AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME, assessments.getSort());
    }

}
