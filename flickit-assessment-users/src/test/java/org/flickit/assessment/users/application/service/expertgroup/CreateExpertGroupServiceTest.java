package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.flickit.assessment.users.application.port.in.expertgroup.CreateExpertGroupUseCase.Param;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateExpertGroupServiceTest {

    @InjectMocks
    private CreateExpertGroupService service;
    @Mock
    private CreateExpertGroupPort createExpertGroupPort;
    @Mock
    private CreateExpertGroupAccessPort createExpertGroupAccessPort;

    @Test
    void testCreateExpertGroup_validParams_persistResult() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            null,
            "http://www.example.com",
            currentUserId);

        long expectedId = new Random().nextLong();
        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(expectedId);
        doNothing().when(createExpertGroupAccessPort).persist(any(CreateExpertGroupAccessPort.Param.class));

        var result = service.createExpertGroup(param);
        assertNotNull(result, "The result of createExpertGroup service" +
            "should be CreateExpertGroupUseCase.Member");
        assertEquals(expectedId, result.id(), "The result should be long ID");
    }

    @Test
    void testCreateExpertGroup_expertGroupPersistProblem_transactionRollback() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            null,
            "http://www.example.com",
            currentUserId);

        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(new Random().nextLong());
        doThrow(new RuntimeException("Simulated exception"))
            .when(createExpertGroupAccessPort).persist(any(CreateExpertGroupAccessPort.Param.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createExpertGroup(param));
        assertNotNull(exception);

        verify(createExpertGroupPort, times(1)).persist(any(CreateExpertGroupPort.Param.class));
    }
}
