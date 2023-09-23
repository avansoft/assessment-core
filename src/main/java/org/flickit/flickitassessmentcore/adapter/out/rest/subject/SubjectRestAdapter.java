package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.domain.Subject;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@RequiredArgsConstructor
public class SubjectRestAdapter implements LoadSubjectByAssessmentKitIdPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public List<Subject> loadByAssessmentKitId(Long assessmentKitId) {
        return loadSubjectsDtoByAssessmentKitId(assessmentKitId).stream()
            .map(SubjectDto::dtoToDomain)
            .toList();
    }

    public List<SubjectDto> loadSubjectsDtoByAssessmentKitId(Long assessmentKitId) {
        String url = properties.getBaseUrl() + String.format(properties.getGetSubjectsUrl(), assessmentKitId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<SubjectDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        return responseEntity.getBody() != null && responseEntity.getBody().items() != null ?
            responseEntity.getBody().items() :
            List.of();
    }

}

