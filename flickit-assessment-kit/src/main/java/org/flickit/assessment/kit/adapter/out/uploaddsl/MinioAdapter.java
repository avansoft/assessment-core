package org.flickit.assessment.kit.adapter.out.uploaddsl;

import io.minio.*;
import io.minio.messages.VersioningConfiguration;
import io.minio.messages.VersioningConfiguration.Status;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.kitdsl.UploadKitDslToFileStoragePort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;

@Component
@AllArgsConstructor
public class MinioAdapter implements UploadKitDslToFileStoragePort {

    public static final String SLASH = "/";
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @SneakyThrows
    @Override
    public UploadKitDslToFileStoragePort.Result upload(MultipartFile dslZipFile, String dslJsonFile) {
        String bucketName = properties.getBucketName();
        String dslFileNameNoSuffix = Objects.requireNonNull(dslZipFile.getOriginalFilename()).replace(".zip", "");
        String dslFileDirPathAddr = properties.getObjectName() + LocalDate.now() + SLASH + dslFileNameNoSuffix + SLASH;
        String zipFileObjectName = dslFileDirPathAddr + dslZipFile.getOriginalFilename();
        String zipJsonFileObjectName = dslFileDirPathAddr + dslFileNameNoSuffix + ".json";

        checkBucketExistence(bucketName);
        setBucketVersioning(bucketName);

        InputStream zipFileInputStream = dslZipFile.getInputStream();
        ObjectWriteResponse dslZipFileWriteResponse = minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(zipFileObjectName)
            .stream(zipFileInputStream, zipFileInputStream.available(), -1)
            .build());
        String zipFileVersionId = dslZipFileWriteResponse.versionId();

        InputStream jsonFileInputStream = new ByteArrayInputStream(dslJsonFile.getBytes());
        ObjectWriteResponse dslJsonFileWriteResponse = minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(zipJsonFileObjectName)
            .stream(jsonFileInputStream, jsonFileInputStream.available(), -1)
            .build());
        String jsonFileVersionId = dslJsonFileWriteResponse.versionId();

        return new Result(zipFileObjectName + SLASH + zipFileVersionId,
            zipJsonFileObjectName + SLASH + jsonFileVersionId);
    }

    @SneakyThrows
    private void setBucketVersioning(String bucketName) {
        minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
            .bucket(bucketName)
            .config(new VersioningConfiguration(Status.ENABLED, false))
            .build());
    }

    @SneakyThrows
    private void checkBucketExistence(String bucketName) {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .build());
        }
    }
}
