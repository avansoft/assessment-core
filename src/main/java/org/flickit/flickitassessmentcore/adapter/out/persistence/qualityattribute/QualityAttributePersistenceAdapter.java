package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattribute;

import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QualityAttributePersistenceAdapter implements LoadQualityAttributeBySubPort {

    @Override
    public List<QualityAttribute> loadQABySubId(Long subId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("subId", subId);
        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<List<QualityAttribute>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<QualityAttribute>>() {
            }
        );
        List<QualityAttribute> responseBody = responseEntity.getBody();
        return responseBody;
    }

    /*
     * TODO:
     *  - must complete this class with true data
     * */
}
