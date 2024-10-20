package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionnaireOrdersServiceTest {

    @InjectMocks
    private UpdateQuestionnaireOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateQuestionnairePort updateQuestionnairePort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestionnaireOrdersService_currentUserIsNotExpertGroupOwner_AccessDeniedException() {
        var param = createParam(UpdateQuestionnaireOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.changeOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateQuestionnairePort);
    }

    @Test
    void testUpdateQuestionnaireOrdersService_validParameters_SuccessfulUpdate() {
        var param = createParam(UpdateQuestionnaireOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        service.changeOrders(param);
        ArgumentCaptor<UpdateQuestionnairePort.UpdateOrderParam> portParamCaptor = ArgumentCaptor.forClass(UpdateQuestionnairePort.UpdateOrderParam.class);
        verify(updateQuestionnairePort, times(1)).updateOrders(portParamCaptor.capture());

        assertEquals(param.getKitVersionId(), portParamCaptor.getValue().kitVersionId());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertNotNull(portParamCaptor.getValue().orders());
        assertEquals(param.getOrders().size(), portParamCaptor.getValue().orders().size());
        assertEquals(param.getOrders().getFirst().getId(), portParamCaptor.getValue().orders().getFirst().questionnaireId());
        assertEquals(param.getOrders().getFirst().getIndex(), portParamCaptor.getValue().orders().getFirst().index());
        assertEquals(param.getOrders().getLast().getId(), portParamCaptor.getValue().orders().getLast().questionnaireId());
        assertEquals(param.getOrders().getLast().getIndex(), portParamCaptor.getValue().orders().getLast().index());
    }

    private UpdateQuestionnaireOrdersUseCase.Param createParam(Consumer<UpdateQuestionnaireOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
        return paramBuilder.build();
    }

    private UpdateQuestionnaireOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionnaireOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new UpdateQuestionnaireOrdersUseCase.QuestionnaireParam(123L, 3),
                new UpdateQuestionnaireOrdersUseCase.QuestionnaireParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
