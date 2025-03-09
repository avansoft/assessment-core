package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.Builder;

@Builder
public record CreateAssessmentRequestDto(Long spaceId,
                                         String title,
                                         String shortTitle,
                                         Long assessmentKitId) {
}
