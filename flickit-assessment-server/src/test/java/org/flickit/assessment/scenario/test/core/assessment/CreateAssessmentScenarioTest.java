package org.flickit.assessment.scenario.test.core.assessment;

import org.flickit.assessment.core.adapter.in.rest.assessment.CreateAssessmentRequestDto;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.scenario.fixture.request.CreateAssessmentRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.flickit.assessment.scenario.test.users.space.SpaceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static java.util.Comparator.comparingLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class CreateAssessmentScenarioTest extends AbstractScenarioTest {

    @Autowired
    AssessmentTestHelper assessmentHelper;

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Autowired
    SpaceTestHelper spaceHelper;

    int assessmentsCountBefore;
    int assessmentResultsCountBefore;
    int attributeValuesCountBefore;
    int subjectValuesCountBefore;
    int assessmentUserRolesCountBefore;

    @BeforeEach
    void before() {
        assessmentsCountBefore = jpaTemplate.count(AssessmentJpaEntity.class);
        assessmentResultsCountBefore = jpaTemplate.count(AssessmentResultJpaEntity.class);
        attributeValuesCountBefore = jpaTemplate.count(AttributeJpaEntity.class);
        subjectValuesCountBefore = jpaTemplate.count(SubjectJpaEntity.class);
        assessmentUserRolesCountBefore = jpaTemplate.count(AssessmentUserRoleJpaEntity.class);
    }

    @Test
    void createAssessment() {
        var spaceId = createSpace();
        var kitId = createKit();
        kitHelper.publishKit(context, kitId);

        var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
            .spaceId(spaceId)
            .assessmentKitId(kitId));

        var response = assessmentHelper.create(context, request);
        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        UUID assessmentId = UUID.fromString(response.path("id"));

        assertAssessment(assessmentId, request, kitId);
        assertAssessmentResult(assessmentId, kitId);
        assertAssessmentUserRoles(assessmentId);
    }

    private Long createSpace() {
        var response = spaceHelper.create(context, createSpaceRequestDto());
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createKit() {
        Long expertGroupId = createExpertGroup();
        Long kitDslId = uploadDsl(expertGroupId);
        Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(expertGroupId)
            .kitDslId(kitDslId)
            .tagIds(List.of(kitTagId))
        );

        var response = kitHelper.create(context, request);

        Number kitId = response.path("kitId");
        return kitId.longValue();
    }

    private Long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private Long uploadDsl(Long expertGroupId) {
        var response = kitDslHelper.uploadDsl(context, "dummy-dsl.zip", "dsl.json", expertGroupId);
        Number id = response.path("kitDslId");
        return id.longValue();
    }

    private void assertAssessment(UUID assessmentId, CreateAssessmentRequestDto request, long kitId) {
        AssessmentJpaEntity loadedAssessment = jpaTemplate.load(assessmentId, AssessmentJpaEntity.class);
        assertEquals(request.title(), loadedAssessment.getTitle());
        assertEquals(generateSlugCode(request.title()), loadedAssessment.getCode());
        assertEquals(request.shortTitle(), loadedAssessment.getShortTitle());
        assertEquals(kitId, loadedAssessment.getAssessmentKitId());
        assertNull(loadedAssessment.getKitCustomId());

        assertEquals(getCurrentUserId(), loadedAssessment.getCreatedBy());
        assertEquals(getCurrentUserId(), loadedAssessment.getLastModifiedBy());
        assertNotNull(loadedAssessment.getCreationTime());
        assertNotNull(loadedAssessment.getLastModificationTime());
        assertEquals(0, loadedAssessment.getDeletionTime());
        assertFalse(loadedAssessment.isDeleted());
    }

    private void assertAssessmentResult(UUID assessmentId, long kitId) {
        int assessmentResultsCountAfter = jpaTemplate.count(AssessmentResultJpaEntity.class);
        assertEquals(assessmentResultsCountBefore + 1, assessmentResultsCountAfter);

        var loadedAssessmentResult = jpaTemplate.findSingle(AssessmentResultJpaEntity.class, (root, query, cb) ->
            cb.equal(root.get(AssessmentResultJpaEntity.Fields.assessment).get(AssessmentJpaEntity.Fields.id), assessmentId)
        );

        var loadedKit = jpaTemplate.load(kitId, AssessmentKitJpaEntity.class);

        assertEquals(loadedKit.getKitVersionId(), loadedAssessmentResult.getKitVersionId());
        assertNull(loadedAssessmentResult.getMaturityLevelId());
        assertNull(loadedAssessmentResult.getConfidenceValue());
        assertFalse(loadedAssessmentResult.getIsCalculateValid());
        assertFalse(loadedAssessmentResult.getIsConfidenceValid());
        assertNotNull(loadedAssessmentResult.getLastModificationTime());
        assertNotNull(loadedAssessmentResult.getLastCalculationTime());
        assertNotNull(loadedAssessmentResult.getLastConfidenceCalculationTime());

        assertAttributeValues(loadedAssessmentResult.getId(), loadedKit.getKitVersionId());
        assertSubjectValues(loadedAssessmentResult.getId(), loadedKit.getKitVersionId());
    }

    private void assertSubjectValues(UUID assessmentResultId, long kitVersionId) {
        var subjectValues = loadByAssessmentResultId(SubjectValueJpaEntity.class, assessmentResultId).stream()
            .sorted(comparingLong(SubjectValueJpaEntity::getSubjectId))
            .toList();
        var subjects = loadByKitVersionId(SubjectJpaEntity.class, kitVersionId).stream()
            .sorted(comparingLong(SubjectJpaEntity::getId))
            .toList();
        var subjectValuesCountAfter = jpaTemplate.count(SubjectJpaEntity.class);
        assertEquals(subjects.size(), subjectValuesCountAfter - subjectValuesCountBefore);

        assertThat(subjectValues)
            .zipSatisfy(subjects, (actual, expected) -> {
                assertEquals(expected.getId(), actual.getSubjectId());
                assertNull(actual.getMaturityLevelId());
                assertNull(actual.getConfidenceValue());
            });
    }

    private void assertAttributeValues(UUID assessmentResultId, long kitVersionId) {
        var attributeValues = loadByAssessmentResultId(AttributeValueJpaEntity.class, assessmentResultId).stream()
            .sorted(comparingLong(AttributeValueJpaEntity::getAttributeId))
            .toList();
        var attributes = loadByKitVersionId(AttributeJpaEntity.class, kitVersionId).stream()
            .sorted(comparingLong(AttributeJpaEntity::getId))
            .toList();
        var attributeValuesCountAfter = jpaTemplate.count(AttributeJpaEntity.class);
        assertEquals(attributes.size(), attributeValuesCountAfter - attributeValuesCountBefore);

        assertThat(attributeValues)
            .zipSatisfy(attributes, (actual, expected) -> {
                assertEquals(expected.getId(), actual.getAttributeId());
                assertNull(actual.getMaturityLevelId());
                assertNull(actual.getConfidenceValue());
            });
    }

    private void assertAssessmentUserRoles(UUID assessmentId) {
        var countAfter = jpaTemplate.count(AssessmentUserRoleJpaEntity.class);
        var assessmentUserRole = jpaTemplate.load(new AssessmentUserRoleJpaEntity.EntityId(assessmentId, getCurrentUserId()),
            AssessmentUserRoleJpaEntity.class);
        assertNotNull(assessmentUserRole);
        assertEquals(AssessmentUserRole.MANAGER.getId(), assessmentUserRole.getRoleId());
        assertEquals(assessmentUserRolesCountBefore + 1, countAfter);
    }

    private <T> List<T> loadByAssessmentResultId(Class<T> clazz, UUID assessmentResultId) {
        return jpaTemplate.search(clazz,
            (root, query, cb) -> cb.equal(root.get("assessmentResult").get("id"), assessmentResultId));
    }

    private <T> List<T> loadByKitVersionId(Class<T> clazz, Long kitVersionId) {
        return jpaTemplate.search(clazz,
            (root, query, cb) -> cb.equal(root.get("kitVersionId"), kitVersionId));
    }
}
