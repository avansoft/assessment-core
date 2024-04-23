package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttrLevelQuestionsInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttrLevelQuestionsInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttrLevelQuestionsInfoServiceTest {

    @InjectMocks
    private GetAttrLevelQuestionsInfoService service;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadAttrLevelQuestionsInfoPort loadAttrLevelQuestionsInfoPort;

    @Test
    void getAttrLevelQuestionsInfo_CurrentUserIsNotMemberOfKitExpertGroup_ThrowsException() {
        GetAttrLevelQuestionsInfoUseCase.Param param = new GetAttrLevelQuestionsInfoUseCase.Param(
            1L,
            1L,
            1L,
            UUID.randomUUID());

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getAttrLevelQuestionsInfo(param));
    }

    @Test
    void getAttrLevelQuestionsInfo_AttributeWithGivenAttributeIdAndKitIdDoesNotExist_ThrowsException() {
        GetAttrLevelQuestionsInfoUseCase.Param param = new GetAttrLevelQuestionsInfoUseCase.Param(
            1L,
            1L,
            1L,
            UUID.randomUUID());

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> service.getAttrLevelQuestionsInfo(param));
    }

    @Test
    void getAttrLevelQuestionsInfo_MaturityLevelWithGivenMaturityLevelIdAndKitIdDoesNotExist_ThrowsException() {
        GetAttrLevelQuestionsInfoUseCase.Param param = new GetAttrLevelQuestionsInfoUseCase.Param(
            1L,
            1L,
            1L,
            UUID.randomUUID());

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> service.getAttrLevelQuestionsInfo(param));
    }

    @Test
    void getAttrLevelQuestionsInfo_ValidInput_ValidResult() {
        GetAttrLevelQuestionsInfoUseCase.Param param = new GetAttrLevelQuestionsInfoUseCase.Param(
            1L,
            1L,
            1L,
            UUID.randomUUID());

        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        var answerOption = new LoadAttrLevelQuestionsInfoPort.Result.Question.AnswerOption(
            1,
            "answerOptionTitle",
            0.1);
        var question = new LoadAttrLevelQuestionsInfoPort.Result.Question(
            1,
            "questionTitle",
            true,
            1,
            "questionnaireTitle",
            List.of(answerOption));
        LoadAttrLevelQuestionsInfoPort.Result result = new LoadAttrLevelQuestionsInfoPort.Result(
            1L,
            "title1",
            1,
            1,
            List.of(question));

        when(loadAttrLevelQuestionsInfoPort.loadAttrLevelQuestionsInfo(param.getAttributeId(), param.getMaturityLevelId()))
            .thenReturn(result);

        GetAttrLevelQuestionsInfoUseCase.Result attrLevelQuestionsInfo = service.getAttrLevelQuestionsInfo(param);

        assertNotNull(attrLevelQuestionsInfo);
        assertEquals(result.id(), attrLevelQuestionsInfo.id());
        assertEquals(result.title(), attrLevelQuestionsInfo.title());
        assertEquals(result.index(), attrLevelQuestionsInfo.index());
        assertEquals(result.questionsCount(), attrLevelQuestionsInfo.questionsCount());
        GetAttrLevelQuestionsInfoUseCase.Result.Question actualQuestion = attrLevelQuestionsInfo.questions().get(0);
        assertNotNull(actualQuestion);
        assertEquals(question.title(), actualQuestion.title());
        assertEquals(question.index(), actualQuestion.index());
        assertTrue(actualQuestion.mayNotBeApplicable());
        assertEquals(question.weight(), actualQuestion.weight());
        assertEquals(question.questionnaire(), actualQuestion.questionnaire());
        GetAttrLevelQuestionsInfoUseCase.Result.Question.AnswerOption actualAnswerOption = actualQuestion.answerOptions().get(0);
        assertNotNull(actualAnswerOption);
        assertEquals(answerOption.title(), actualAnswerOption.title());
        assertEquals(answerOption.index(), actualAnswerOption.index());
        assertEquals(answerOption.value(), actualAnswerOption.value());
    }
}
