package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.core.adapter.in.rest.assessment.CreateAssessmentRequestDto;
import org.flickit.assessment.core.adapter.in.rest.assessment.CreateAssessmentRequestDto.CreateAssessmentRequestDtoBuilder;

import java.util.function.Consumer;

@UtilityClass
public class CreateAssessmentRequestDtoMother {

    private static int index = 0;

    public static CreateAssessmentRequestDto createAssessmentRequestDto() {
        return builder().build();
    }

    public static CreateAssessmentRequestDto createAssessmentRequestDto(Consumer<CreateAssessmentRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static CreateAssessmentRequestDtoBuilder builder() {
        index++;
        return CreateAssessmentRequestDto.builder()
            .title("My Assessment " + index)
            .shortTitle("sht");
    }
}
