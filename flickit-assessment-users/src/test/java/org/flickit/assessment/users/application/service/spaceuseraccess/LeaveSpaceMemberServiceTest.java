package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.DeleteSpaceUserAccessPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveSpaceMemberServiceTest {

    @InjectMocks
    LeaveSpaceMemberService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    DeleteSpaceUserAccessPort deleteSpaceUserAccessPort;

    @Test
    @DisplayName("If there space is not exists or user does not access to space, service should throw AccessDeniedException")
    void testLeaveSpaceMember_userDoesNotAccess_accessDeniedError(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceMemberUseCase.Param param = new LeaveSpaceMemberUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.leaveMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(deleteSpaceUserAccessPort);
    }

    @Test
    @DisplayName("If there are valid inputs, service should remove the access successfully")
    void testLeaveSpaceMember_validParameters_success(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceMemberUseCase.Param param = new LeaveSpaceMemberUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);

        assertDoesNotThrow(()-> service.leaveMember(param));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(deleteSpaceUserAccessPort).deleteAccess(param.getId(), param.getCurrentUserId());
    }
}
