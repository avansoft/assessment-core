package org.flickit.assessment.scenario.fixture.request;

import lombok.experimental.UtilityClass;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.UpdateKitInfoRequestDto;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.UpdateKitInfoRequestDto.UpdateKitInfoRequestDtoBuilder;

import java.util.function.Consumer;

@UtilityClass
public class UpdateKitInfoRequestDtoMother {

    public static UpdateKitInfoRequestDto createKitByDslRequestDto() {
        return builder().build();
    }

    public static UpdateKitInfoRequestDto createKitByDslRequestDto(Consumer<UpdateKitInfoRequestDtoBuilder> changer) {
        var builder = builder();
        changer.accept(builder);
        return builder.build();
    }

    private static UpdateKitInfoRequestDtoBuilder builder() {
        return UpdateKitInfoRequestDto.builder();
    }
}
