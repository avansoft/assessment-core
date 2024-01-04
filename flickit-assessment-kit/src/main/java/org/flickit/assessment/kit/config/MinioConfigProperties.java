package org.flickit.assessment.kit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("minio")
public class MinioConfigProperties {

    private String url = "http://127.0.0.1:9000";
    private int port = 9000;
    private String accessKey = "minioadmin";
    private String accessSecret = "minioadmin";
    private String bucketName = "test";
    private String objectName = "assessmentKit/dsl/";
    private Boolean secure = false;

/*    private String url = "https://flickit-test-cdn.darkube.app:9000";
    private int port = 9000;
    private String accessKey = "";
    private String accessSecret = "";
    private String bucketName = "media";
    private String objectName = "assessment_kit/dsl";
    private Boolean secure = true;*/
}
