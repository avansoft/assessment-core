package org.flickit.assessment.core.application.service.assessment;


import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.flickit.assessment.core.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentServiceTest {

    @InjectMocks
    private CreateAssessmentService service;

    @Mock
    private CreateAssessmentPort createAssessmentPort;

    @Mock
    private CreateAssessmentResultPort createAssessmentResultPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateAttributeValuePort createAttributeValuePort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private CheckKitAccessPort checkKitAccessPort;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private CountAssessmentsPort countAssessmentsPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    private final AssessmentKit privateKit = AssessmentKitMother.kit();
    private final AssessmentKit publicKit = AssessmentKitMother.publicKit();

    private final Attribute qa1 = AttributeMother.simpleAttribute();
    private final Attribute qa2 = AttributeMother.simpleAttribute();
    private final Attribute qa3 = AttributeMother.simpleAttribute();
    private final Attribute qa4 = AttributeMother.simpleAttribute();
    private final Attribute qa5 = AttributeMother.simpleAttribute();

    private final List<Subject> expectedSubjects = List.of(
        new Subject(1L, "subject2", "description2", 1, List.of(qa1, qa2)),
        new Subject(2L, "subject1", "description1", 1, List.of(qa3, qa4)),
        new Subject(3L, "subject3", "description3", 1, List.of(qa5))
    );

    private final Param param = createParam(Param.ParamBuilder::build);
    private final Space space = SpaceMother.createBasicSpaceWithOwnerId(UUID.randomUUID());
    private final UUID expectedId = UUID.randomUUID();

    @Test
    void testCreateAssessment_whenValidValidParameters_thenPersistsAndReturnsId() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals(param.getTitle(), createPortParam.getValue().code());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getKitId(), createPortParam.getValue().assessmentKitId());
        assertNotNull(createPortParam.getValue().creationTime());

        ArgumentCaptor<UUID> grantPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> grantPortUserId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> grantPortRoleId = ArgumentCaptor.forClass(Integer.class);
        verify(grantUserAssessmentRolePort, times(2)).persist(grantPortAssessmentId.capture(),
            grantPortUserId.capture(),
            grantPortRoleId.capture());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().getFirst());
        assertEquals(space.getOwnerId(), grantPortUserId.getAllValues().getFirst());
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().getFirst());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().get(1));
        assertEquals(param.getCurrentUserId(), grantPortUserId.getAllValues().get(1));
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().get(1));

        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenCurrentUserIsSpaceOwner_thenPersistOneAssessmentUserRole() {
        var spaceUserOwner = SpaceMother.createBasicSpaceWithOwnerId(param.getCurrentUserId());

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(spaceUserOwner);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals(param.getTitle(), createPortParam.getValue().code());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getKitId(), createPortParam.getValue().assessmentKitId());
        assertNotNull(createPortParam.getValue().creationTime());

        ArgumentCaptor<UUID> grantPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> grantPortUserId = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> grantPortRoleId = ArgumentCaptor.forClass(Integer.class);
        verify(grantUserAssessmentRolePort, times(1)).persist(grantPortAssessmentId.capture(),
            grantPortUserId.capture(),
            grantPortRoleId.capture());

        assertEquals(expectedId, grantPortAssessmentId.getAllValues().getFirst());
        assertEquals(param.getCurrentUserId(), grantPortUserId.getAllValues().getFirst());
        assertEquals(AssessmentUserRole.MANAGER.getId(), grantPortRoleId.getAllValues().getFirst());

        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenValidParametersWithPersonalSpaceAndPublicKit_thenPersistsAssessmentResult() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        UUID expectedResultId = UUID.randomUUID();
        when(createAssessmentResultPort.persist(any(CreateAssessmentResultPort.Param.class))).thenReturn(expectedResultId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(expectedResponse);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);

        service.createAssessment(param);

        ArgumentCaptor<CreateAssessmentResultPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(createPortParam.capture());

        assertEquals(expectedId, createPortParam.getValue().assessmentId());
        assertNotNull(createPortParam.getValue().lastModificationTime());
        assertFalse(createPortParam.getValue().isCalculateValid());

        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenValidParameters_thenPersistsSubjectValues() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(publicKit.getKitVersion())).thenReturn(expectedSubjects);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);

        service.createAssessment(param);

        verify(createSubjectValuePort, times(1)).persistAll(anyList(), any());
        verify(grantUserAssessmentRolePort, times(2)).persist(any(), any(UUID.class), anyInt());

        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenValidCommand_thenPersistsAttributeValue() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(publicKit.getKitVersion())).thenReturn(expectedSubjects);
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(publicKit);

        service.createAssessment(param);

        verify(grantUserAssessmentRolePort, times(2)).persist(any(), any(UUID.class), anyInt());
        verify(createAttributeValuePort, times(1)).persistAll(anySet(), any());
        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenUserDoesNotHaveAccessToSpace_thenThrowsReturnUpgradeRequiredException() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(appSpecProperties);
    }

    @Test
    void testCreateAssessment_whenUserDoesNotHaveAccessToKit_thenThrowsException() {
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ValidationException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_KIT_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(appSpecProperties);
    }

    @Test
    void testCreateAssessment_whenSpaceIsPersonalAndExceedsMaxAssessmentLimits_thenThrowsException() {
        var kit = AssessmentKitMother.kit();

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(kit);
        when(countAssessmentsPort.countSpaceAssessments(param.getSpaceId())).thenReturn(2);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_BASIC_SPACE_ASSESSMENTS_MAX, throwable.getMessage());
    }

    @Test
    void testCreateAssessment_whenSpaceIsPersonalAndAssessmentKitIsPrivate_thenThrowsException() {
        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(space);
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(privateKit);
        when(countAssessmentsPort.countSpaceAssessments(param.getSpaceId())).thenReturn(0);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_BASIC_SPACE_PRIVATE_KIT_NOT_ALLOWED, throwable.getMessage());

        verify(appSpecProperties).getSpace();
    }

    @Test
    void testCreateAssessment_whenSpaceIsPremiumAndSubscriptionExpired_thenThrowsException() {
        var premiumExpiredSpace = SpaceMother.createExpiredPremiumSpace(UUID.randomUUID());
        var kit = AssessmentKitMother.kit();

        when(loadSpacePort.loadSpace(param.getSpaceId())).thenReturn(premiumExpiredSpace);
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(Optional.of(param.getKitId()));
        when(loadAssessmentKitPort.loadAssessmentKit(param.getKitId())).thenReturn(kit);

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.createAssessment(param));
        assertEquals(CREATE_ASSESSMENT_PREMIUM_SPACE_EXPIRED, throwable.getMessage());

        verifyNoInteractions(appSpecProperties);
    }

    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxBasicSpaces(1);
        properties.getSpace().setMaxBasicSpaceAssessments(2);
        return properties;
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentUseCase.Param.builder()
            .title("title")
            .shortTitle("shortTitle")
            .spaceId(0L)
            .kitId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
