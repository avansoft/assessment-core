package org.flickit.assessment.kit.application.service.answeroptions;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOptionOrder;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroptions.UpdateAnswerOptionOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAnswerOptionOrdersServiceTest {

    @InjectMocks
    private UpdateAnswerOptionOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerOptionPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateAnswerOptionOrders_currentUserIsNotOwner_AccessDeniedException() {
        var param = createParam(UpdateAnswerOptionOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.changeOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateAnswerOptionPort);
    }

    @Test
    void testUpdateAnswerOptionOrders_validParameters_shouldUpdateAnswerOptionOrders() {
        var param = createParam(UpdateAnswerOptionOrdersUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        service.changeOrders(param);

        var answerOptionOrder = List.of(new AnswerOptionOrder(123L, 3), new AnswerOptionOrder(124L, 2));

        verify(updateAnswerOptionPort).updateOrders(answerOptionOrder, param.getKitVersionId(), param.getCurrentUserId());
    }

    private UpdateAnswerOptionOrdersUseCase.Param createParam(Consumer<UpdateAnswerOptionOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAnswerOptionOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerOptionOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new UpdateAnswerOptionOrdersUseCase.AnswerOptionParam(123L, 3),
                new UpdateAnswerOptionOrdersUseCase.AnswerOptionParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
