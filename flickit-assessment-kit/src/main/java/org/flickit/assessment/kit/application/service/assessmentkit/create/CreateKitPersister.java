package org.flickit.assessment.kit.application.service.assessmentkit.create;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

import java.util.UUID;

public interface CreateKitPersister {

    void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId, UUID currentUserId);

    int order();

}
