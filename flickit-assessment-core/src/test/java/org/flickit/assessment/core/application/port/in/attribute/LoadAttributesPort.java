package org.flickit.assessment.core.application.port.in.attribute;

import java.util.List;
import java.util.UUID;

public interface LoadAttributesPort {

    Result loadAttributes(UUID assessmentId);

    record Result(List<Attribute> attributes) {

        record Attribute(long id,
                         String title,
                         String description,
                         int index,
                         int weight,
                         double confidenceValue,
                         MaturityLevel maturityLevel,
                         Subject subject) {
        }

        public record MaturityLevel(long id,
                                    String title,
                                    String description,
                                    int index,
                                    int value) {
        }

        public record Subject(long id,
                              String title) {
        }
    }
}
