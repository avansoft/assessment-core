package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

public record GetKitEditableInfoResponseDto(Long id,
                                            String title,
                                            String summary,
                                            Boolean published,
                                            Boolean isPrivate,
                                            Double price,
                                            String about,
                                            List<KitTag> tags,
                                            boolean editable) {
}
