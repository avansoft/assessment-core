package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireListServiceTest {

    @InjectMocks
    private GetAssessmentQuestionnaireListService service;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;

    @Test
    void testGetQuestionnaireList_InvalidCurrentUser_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID()
        );
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void testGetQuestionnaireList_ValidParams_ReturnListSuccessfully() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID()
        );
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);

        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(param.getAssessmentId(), param.getSize(), param.getPage());
        QuestionnaireListItem.Subject subject = new QuestionnaireListItem.Subject(1, "subject");
        QuestionnaireListItem questionnaire = new QuestionnaireListItem(
            0,
            "questionnaire",
            1,
            1,
            0,
            1,
            0,
            List.of(subject));
        var expectedResult = new PaginatedResponse<>(
            List.of(questionnaire),
            0,
            10,
            "index",
            "asc",
            1);

        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(expectedResult);

        var result = service.getAssessmentQuestionnaireList(param);

        assertEquals(expectedResult, result);
    }
}
