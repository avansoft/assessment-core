package org.flickit.assessment.kit.application.service.kitdsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.in.kitdsl.UploadKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.ParsDslFilePort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UploadKitDslToFileStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadKitDSLServiceTest {

    @InjectMocks
    private UploadKitDslService service;

    @Mock
    FileProperties fileProperties;

    @Mock
    private UploadKitDslToFileStoragePort uploadKitDslToFileStoragePort;

    @Mock
    private ParsDslFilePort parsDslFilePort;

    @Mock
    private CreateKitDslPort createKitDslPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @SneakyThrows
    @Test
    void testUploadKitDsl_ValidKitFile_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofMegabytes(10));
        when(fileProperties.getKitDslContentTypes()).thenReturn(List.of("application/zip", "application/x-zip"));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);

        MockMultipartFile dslFile = new MockMultipartFile("dsl", "dsl.zip", "application/x-zip", "some file".getBytes());
        QuestionnaireDslModel q1 = QuestionnaireDslModel.builder().title("Clean Architecture").description("desc").build();
        QuestionnaireDslModel q2 = QuestionnaireDslModel.builder().title("Code Quality").description("desc").build();
        SubjectDslModel s1 = SubjectDslModel.builder().title("Software").description("desc").build();
        SubjectDslModel s2 = SubjectDslModel.builder().title("Team").description("desc").build();
        AssessmentKitDslModel kitDslModel = AssessmentKitDslModel.builder()
            .questionnaires(List.of(q1, q2))
            .subjects(List.of(s1, s2))
            .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(kitDslModel);
        String dslFilePath = "sample/zip/file/path";
        String jsonFilePath = "sample/json/file/path";
        long kitDslId = 1L;
        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(uploadKitDslToFileStoragePort.uploadKitDsl(dslFile, json)).thenReturn(new UploadKitDslToFileStoragePort.Result(dslFilePath, jsonFilePath));
        when(createKitDslPort.create(dslFilePath, jsonFilePath, currentUserId)).thenReturn(kitDslId);
        when(parsDslFilePort.parsToDslModel(dslFile)).thenReturn(kitDslModel);

        Long resultKitDslId = assertDoesNotThrow(() -> service.upload(param));

        assertEquals(kitDslId, resultKitDslId);
    }

    @SneakyThrows
    @Test
    void testUploadKit_CurrentUserNotExpertGroupOwner_CurrentUserValidationFail() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        MockMultipartFile dslFile = new MockMultipartFile("dsl", "dsl.zip", "application/zip", "some file".getBytes());
        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(UUID.randomUUID());
        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofMegabytes(10));
        when(fileProperties.getKitDslContentTypes()).thenReturn(List.of("application/zip", "application/x-zip"));

        var throwable = assertThrows(AccessDeniedException.class, () -> service.upload(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @SneakyThrows
    @Test
    void testUploadKit_InvalidFileSize_ThrowException() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        MockMultipartFile dslFile = new MockMultipartFile("dsl", "dsl.zip", "application/zip", "some file".getBytes());
        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofBytes(1));

        var throwable = assertThrows(ValidationException.class, () -> service.upload(param));
        assertEquals(UPLOAD_FILE_DSL_SIZE_MAX, throwable.getMessageKey());
    }

    @SneakyThrows
    @Test
    void testUploadKit_InvalidFileType_ThrowException() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        MockMultipartFile dslFile = new MockMultipartFile("pic", "pic.png", "application/png", "some file".getBytes());
        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofMegabytes(5));

        var throwable = assertThrows(ValidationException.class, () -> service.upload(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());
    }
}
