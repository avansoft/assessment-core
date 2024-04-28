package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitEditableInfoServiceTest {

    @InjectMocks
    private GetKitEditableInfoService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadKitTagListPort loadKitTagListPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Test
    void testGetKitEditableInfo_KitNotFound_ErrorMessage() {
        long kitId = 123L;
        UUID currentUserId = UUID.randomUUID();
        GetKitEditableInfoUseCase.Param param = new GetKitEditableInfoUseCase.Param(kitId, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(1L, null, null);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadAssessmentKitPort.load(kitId)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitEditableInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testGetKitEditableInfo_ValidInput_ValidResults() {
        long kitId = 123L;
        UUID currentUserId = UUID.randomUUID();
        GetKitEditableInfoUseCase.Param param = new GetKitEditableInfoUseCase.Param(kitId, currentUserId);

        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        List<KitTag> tags = List.of(new KitTag(1L, "tagCode1", "Tag1"));
        ExpertGroup expertGroup = new ExpertGroup(1L, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadAssessmentKitPort.load(kitId)).thenReturn(assessmentKit);
        when(loadKitTagListPort.loadByKitId(kitId)).thenReturn(tags);

        GetKitEditableInfoUseCase.KitEditableInfo kitEditableInfo = service.getKitEditableInfo(param);

        assertEquals(assessmentKit.getId(), kitEditableInfo.id());
        assertEquals(assessmentKit.getTitle(), kitEditableInfo.title());
        assertEquals(assessmentKit.getSummary(), kitEditableInfo.summary());
        assertEquals(assessmentKit.isPublished(), kitEditableInfo.published());
        assertEquals(assessmentKit.isPrivate(), kitEditableInfo.isPrivate());
        assertEquals(0, kitEditableInfo.price());
        assertEquals(assessmentKit.getAbout(), kitEditableInfo.about());
        assertEquals(tags.size(), kitEditableInfo.tags().size());
        assertTrue(kitEditableInfo.editable());
    }

    @Test
    void testGetKitEditableInfo_CurrentUserIsNotExpertGroupMember_ErrorMessage() {
        long kitId = 123L;
        UUID currentUserId = UUID.randomUUID();
        GetKitEditableInfoUseCase.Param param = new GetKitEditableInfoUseCase.Param(kitId, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(1L, null, null);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.getKitEditableInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
