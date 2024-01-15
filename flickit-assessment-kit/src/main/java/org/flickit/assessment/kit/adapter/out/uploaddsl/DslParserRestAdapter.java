package org.flickit.assessment.kit.adapter.out.uploaddsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DSLSyntaxErrorException;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DslParserRestException;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.ZipBombException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.kitdsl.ParsDslFilePort;
import org.flickit.assessment.kit.config.DslParserRestProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@AllArgsConstructor
public class DslParserRestAdapter implements ParsDslFilePort {

    private final RestTemplate dslParserRestTemplate;
    private final DslParserRestProperties properties;

    @Override
    public AssessmentKitDslModel parsToDslModel(MultipartFile dslFile) throws IOException {
        String dslContent = uniteDslFiles(dslFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AssessmentKitRequest> requestEntity = new HttpEntity<>(new AssessmentKitRequest(dslContent), headers);
        try {
            var responseEntity = dslParserRestTemplate.exchange(
                properties.getUrl(),
                HttpMethod.POST,
                requestEntity,
                AssessmentKitDslModel.class
            );

            if (!responseEntity.getStatusCode().is2xxSuccessful())
                throw new DslParserRestException(responseEntity.getStatusCode().value());

            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new DSLSyntaxErrorException(e, responseBody);
        }
    }

    private String uniteDslFiles(MultipartFile dslFile) throws IOException {
        InputStream dslFileStream = dslFile.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(dslFileStream);
        StringBuilder allContent = new StringBuilder();
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String name = entry.getName();
            if (name.endsWith(".ak")) {
                String fileBaseName = name.substring(name.lastIndexOf('/') + 1);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = zipInputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                byte[] contentBytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();

                if (isZipBombAttack(contentBytes, entry)) {
                    throw new ZipBombException();
                }

                String content = new String(contentBytes, StandardCharsets.UTF_8);
                String trimContent = trimContent(content);
                allContent.append("\n")
                    .append("// BEGIN FILE ").append(fileBaseName)
                    .append(trimContent);
            }
        }
        return allContent.toString();
    }

    private boolean isZipBombAttack(byte[] contentBytes, ZipEntry entry) {
        int thresholdSize = 1000000000; // 1 GB
        double thresholdRatio = 10;
        int totalSizeEntry = contentBytes.length;
        double compressionRatio = entry.getCompressedSize() / (double) totalSizeEntry;

        return compressionRatio > thresholdRatio || // Ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack
            totalSizeEntry > thresholdSize; // The uncompressed data size is too much for the application resource capacity
    }


    private String trimContent(String content) {
        StringBuilder newContent = new StringBuilder();
        for (String line : content.split("\\r?\\n")) {
            if (!line.trim().startsWith("import")) {
                newContent.append('\n').append(line);
            } else {
                newContent.append("\n//").append(line);
            }
        }
        return newContent.toString();
    }

    record AssessmentKitRequest(String dslContent) {
    }

}
