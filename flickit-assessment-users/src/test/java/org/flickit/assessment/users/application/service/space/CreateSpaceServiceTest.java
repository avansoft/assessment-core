package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSpaceServiceTest {

    @InjectMocks
    CreateSpaceService service;

    @Mock
    CreateSpacePort createSpacePort;

    @Mock
    CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Test
    void testCreateSpace_validParams_successful() {
        var param = createParam(CreateSpaceUseCase.Param.ParamBuilder::build);
        long createdSpaceId = 0L;

        when(createSpacePort.persist(any())).thenReturn(createdSpaceId);

        service.createSpace(param);

        ArgumentCaptor<Space> createSpaceCaptor = ArgumentCaptor.forClass(Space.class);
        verify(createSpacePort).persist(createSpaceCaptor.capture());
        var capturedSpace = createSpaceCaptor.getValue();
        assertEquals(param.getTitle(), capturedSpace.getTitle());
        assertEquals(generateSlugCode(param.getTitle()), capturedSpace.getCode());
        assertEquals(param.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());

        ArgumentCaptor<SpaceUserAccess> userAccessCaptor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(userAccessCaptor.capture());
        var capturedAccess = userAccessCaptor.getValue();
        assertEquals(createdSpaceId, capturedAccess.getSpaceId());
        assertEquals(param.getCurrentUserId(), capturedAccess.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedAccess.getUserId());
    }

    private CreateSpaceUseCase.Param createParam(Consumer<CreateSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSpaceUseCase.Param.builder()
            .title("title")
            .type(SpaceType.BASIC.getCode())
            .currentUserId(UUID.randomUUID());
    }
}
