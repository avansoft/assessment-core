package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AttributeWithSubject;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.Param;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.attributeWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributesServiceTest {

    @InjectMocks
    private GetAttributesService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetAttributes_UserHasNotAccess_ThrowsException() {
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getAttributes(param));
        assertEquals(ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(loadAttributesPort);
    }

    @Test
    void testGetAttributes_ValidParam_ReturnsAttributes() {
        Param param = createParam(Param.ParamBuilder::build);

        var subject = subjectWithTitle("subject");
        var attribute = attributeWithTitle("attribute");

        PaginatedResponse<AttributeWithSubject> paginatedResponse = new PaginatedResponse<>(
            List.of(new AttributeWithSubject(attribute, subject)),
            0,
            15,
            "index",
            "asc",
            1
        );

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAttributesPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage())).thenReturn(paginatedResponse);

        var result = service.getAttributes(param);

        assertEquals(paginatedResponse.getPage(), result.getPage());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getTotal(), result.getTotal());

        var item = result.getItems().getFirst();
        assertEquals(attribute.getId(), item.id());
        assertEquals(attribute.getIndex(), item.index());
        assertEquals(attribute.getTitle(), item.title());
        assertEquals(attribute.getDescription(), item.description());
        assertEquals(attribute.getWeight(), item.weight());

        GetAttributesUseCase.AttributeSubject attributeSubject = item.subject();
        assertEquals(subject.getId(), attributeSubject.id());
        assertEquals(subject.getTitle(), attributeSubject.title());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .size(10)
            .page(2)
            .currentUserId(UUID.randomUUID());
    }
}
