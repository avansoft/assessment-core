package org.flickit.assessment.scenario.test.users.expertgroup;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class CreateExpertGroupScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);

        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        final Number expertGroupId = response.path("id");

        ExpertGroupJpaEntity loadedExpertGroup = jpaTemplate.load(expertGroupId, ExpertGroupJpaEntity.class);
        assertEquals(request.title(), loadedExpertGroup.getTitle());
        assertEquals(request.bio(), loadedExpertGroup.getBio());
        assertEquals(request.about(), loadedExpertGroup.getAbout());
        assertEquals(generateSlugCode(request.title()), loadedExpertGroup.getCode());
        assertEquals(request.website(), loadedExpertGroup.getWebsite());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getOwnerId());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getCreatedBy());
        assertEquals(getCurrentUserId(), loadedExpertGroup.getLastModifiedBy());
        assertNotNull(loadedExpertGroup.getPicture());
        assertNotEquals("", loadedExpertGroup.getPicture());
        assertNotNull(loadedExpertGroup.getCreationTime());
        assertNotNull(loadedExpertGroup.getLastModificationTime());
        assertEquals(0, loadedExpertGroup.getDeletionTime());

        ExpertGroupAccessJpaEntity userAccess = jpaTemplate.load(
            new ExpertGroupAccessJpaEntity.EntityId(expertGroupId.longValue(), getCurrentUserId()),
            ExpertGroupAccessJpaEntity.class);
        assertNotNull(userAccess);
        assertEquals(ExpertGroupAccessStatus.ACTIVE, ExpertGroupAccessStatus.values()[userAccess.getStatus()]);
    }

}
