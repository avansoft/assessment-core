package org.flickit.assessment.core.adapter.out.calculate;

import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionRestAdapter;
import org.flickit.assessment.core.adapter.out.rest.question.QuestionDto;
import org.flickit.assessment.core.adapter.out.rest.question.QuestionRestAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.test.fixture.adapter.jpa.AssessmentResultJpaEntityMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.test.fixture.adapter.dto.AnswerOptionDtoMother.createAnswerOptionDto;
import static org.flickit.assessment.core.test.fixture.adapter.dto.QuestionDtoMother.createQuestionDtoWithAffectedLevelAndAttributes;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AnswerJpaEntityMother.*;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AttributeJapEntityMother.createAttributeEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AttributeValueJpaEntityMother.attributeValueWithNullMaturityLevel;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.SubjectJpaEntityMother.subjectWithAttributes;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.SubjectValueJpaEntityMother.subjectValueWithNullMaturityLevel;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentCalculateInfoLoadAdapterTest {

    @InjectMocks
    AssessmentCalculateInfoLoadAdapter adapter;
    @Mock
    private AssessmentResultJpaRepository assessmentResultRepo;
    @Mock
    private AnswerJpaRepository answerRepo;
    @Mock
    private QualityAttributeValueJpaRepository qualityAttrValueRepo;
    @Mock
    private SubjectValueJpaRepository subjectValueRepo;
    @Mock
    private SubjectJpaRepository subjectRepository;
    @Mock
    private QuestionRestAdapter questionRestAdapter;
    @Mock
    private AnswerOptionRestAdapter answerOptionRestAdapter;
    @Mock
    private MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;

    private static void assertAssessment(AssessmentJpaEntity assessmentEntity, Assessment resultAssessment) {
        assertEquals(assessmentEntity.getId(), resultAssessment.getId());
        assertEquals(assessmentEntity.getAssessmentKitId(), resultAssessment.getAssessmentKit().getId());
        assertEquals(allLevels().size(), resultAssessment.getAssessmentKit().getMaturityLevels().size());
    }

    private static void assertSubjectValues(List<SubjectValueJpaEntity> subjectValueEntities, List<SubjectValue> resultSubjectValues) {
        subjectValueEntities.forEach(entity ->
            resultSubjectValues.stream()
                .filter(sv -> sv.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    sv -> assertEquals(entity.getSubjectId(), sv.getSubject().getId()),
                    Assertions::fail
                )
        );
    }

    private static void assertAttributeValues(List<QualityAttributeValueJpaEntity> attributeValueJpaEntities, List<QualityAttributeValue> resultAttributeValues) {
        attributeValueJpaEntities.forEach(entity ->
            resultAttributeValues.stream()
                .filter(av -> av.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    av -> {
                        assertNotNull(av.getQualityAttribute());
                        assertNotNull(av.getQualityAttribute().getQuestions());
                        assertEquals(5, av.getQualityAttribute().getQuestions().size());
                    },
                    Assertions::fail
                )
        );
    }

    private static void assertAnswers(List<AnswerJpaEntity> answerEntities, List<Answer> loadedAnswers) {
        assertEquals(answerEntities.size(), loadedAnswers.size());

        answerEntities.forEach(entity ->
            loadedAnswers.stream()
                .filter(a -> a.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    a -> {
                        assertEquals(entity.getQuestionId(), a.getQuestionId());
                        if (entity.getAnswerOptionId() == null)
                            assertNull(a.getSelectedOption());
                        else {
                            assertNotNull(a.getSelectedOption());
                            assertEquals(entity.getAnswerOptionId(), a.getSelectedOption().getId());
                        }
                        assertEquals(entity.getIsNotApplicable(), a.getIsNotApplicable());
                    },
                    Assertions::fail
                )
        );
    }

    private static Context createContext() {
        var assessmentResultEntity = AssessmentResultJpaEntityMother.validSimpleAssessmentResultEntity(null, Boolean.FALSE, Boolean.FALSE);

        var attributeId = 134L;
        var attribute1Id = attributeId++;
        var attribute2Id = attributeId++;
        var attribute3Id = attributeId++;
        var attribute4Id = attributeId++;
        var attribute5Id = attributeId++;
        var attribute6Id = attributeId;

        Long kitId = 123L;

        AttributeJpaEntity attribute1 = createAttributeEntity(attribute1Id, 1, kitId);
        AttributeJpaEntity attribute2 = createAttributeEntity(attribute2Id, 2, kitId);
        AttributeJpaEntity attribute3 = createAttributeEntity(attribute3Id, 3, kitId);
        AttributeJpaEntity attribute4 = createAttributeEntity(attribute4Id, 4, kitId);
        AttributeJpaEntity attribute5 = createAttributeEntity(attribute5Id, 5, kitId);
        AttributeJpaEntity attribute6 = createAttributeEntity(attribute6Id, 6, kitId);

        var qav1 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute1Id);
        var qav2 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute2Id);
        var qav3 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute3Id);
        var qav4 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute4Id);
        var qav5 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute5Id);
        var qav6 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute6Id);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);

        var subjectValue1 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue2 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue3 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);

        var subject1 = subjectWithAttributes(subjectValue1.getSubjectId(), 1, List.of(attribute1, attribute2));
        var subject2 = subjectWithAttributes(subjectValue2.getSubjectId(), 1, List.of(attribute3, attribute4));
        var subject3 = subjectWithAttributes(subjectValue3.getSubjectId(), 1, List.of(attribute5, attribute6));
        List<SubjectJpaEntity> subjects = List.of(subject1, subject2, subject3);

        var questionDto1 = createQuestionDtoWithAffectedLevelAndAttributes(1L, LEVEL_ONE_ID, attribute1Id, attribute2Id, attribute3Id);
        var questionDto2 = createQuestionDtoWithAffectedLevelAndAttributes(2L, LEVEL_ONE_ID, attribute4Id, attribute5Id, attribute6Id);
        var questionDto3 = createQuestionDtoWithAffectedLevelAndAttributes(3L, LEVEL_TWO_ID, attribute1Id, attribute2Id, attribute3Id);
        var questionDto4 = createQuestionDtoWithAffectedLevelAndAttributes(4L, LEVEL_TWO_ID, attribute4Id, attribute5Id, attribute6Id);
        var questionDto5 = createQuestionDtoWithAffectedLevelAndAttributes(5L, LEVEL_THREE_ID, attribute1Id, attribute2Id, attribute3Id);
        var questionDto6 = createQuestionDtoWithAffectedLevelAndAttributes(6L, LEVEL_THREE_ID, attribute4Id, attribute5Id, attribute6Id);
        var questionDto7 = createQuestionDtoWithAffectedLevelAndAttributes(7L, LEVEL_FOUR_ID, attribute1Id, attribute2Id, attribute3Id);
        var questionDto8 = createQuestionDtoWithAffectedLevelAndAttributes(8L, LEVEL_FOUR_ID, attribute4Id, attribute5Id, attribute6Id);
        var questionDto9 = createQuestionDtoWithAffectedLevelAndAttributes(9L, LEVEL_FIVE_ID, attribute1Id, attribute2Id, attribute3Id);
        var questionDto10 = createQuestionDtoWithAffectedLevelAndAttributes(10L, LEVEL_FIVE_ID, attribute4Id, attribute5Id, attribute6Id);

        List<QuestionDto> questionDtos = List.of(questionDto1, questionDto2, questionDto3, questionDto4, questionDto5, questionDto6, questionDto7, questionDto8, questionDto9, questionDto10);

        var answerQ1 = answerEntityWithOption(assessmentResultEntity, questionDto1.id(), 1L);
        var answerQ2 = answerEntityWithOption(assessmentResultEntity, questionDto2.id(), 2L);
        // no answer for question 3
        var answerQ4 = answerEntityWithNoOption(assessmentResultEntity, questionDto4.id());
        var answerQ5 = answerEntityWithOption(assessmentResultEntity, questionDto5.id(), 5L);
        var answerQ6 = answerEntityWithIsNotApplicableTrue(assessmentResultEntity, questionDto6.id());
        // no answer question 7, 8
        var answerQ9 = answerEntityWithIsNotApplicableTrue(assessmentResultEntity, questionDto9.id());
        var answerQ10 = answerEntityWithNoOption(assessmentResultEntity, questionDto10.id());

        List<AnswerJpaEntity> answerEntities = new ArrayList<>(List.of(answerQ1, answerQ2, answerQ4, answerQ5, answerQ6, answerQ9, answerQ10));

        var answerOptionDto1 = createAnswerOptionDto(1L, questionDto1);
        var answerOptionDto2 = createAnswerOptionDto(2L, questionDto2);
        var answerOptionDto3 = createAnswerOptionDto(5L, questionDto5);
        List<AnswerOptionDto> answerOptionDtos = new ArrayList<>(List.of(answerOptionDto1, answerOptionDto2, answerOptionDto3));

        return new Context(
            assessmentResultEntity,
            subjectValues,
            qualityAttributeValues,
            subjects,
            questionDtos,
            answerEntities,
            answerOptionDtos);
    }

    @Test
    void testLoad() {
        Context context = createContext();

        doMocks(context);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        when(maturityLevelJpaAdapter.loadByKitIdWithCompetences(context.assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(maturityLevels);

        var loadedAssessmentResult = adapter.load(context.assessmentResultEntity().getAssessment().getId());

        assertEquals(context.assessmentResultEntity().getId(), loadedAssessmentResult.getId());

        assertAssessment(context.assessmentResultEntity().getAssessment(), loadedAssessmentResult.getAssessment());

        assertSubjectValues(context.subjectValues, loadedAssessmentResult.getSubjectValues());

        var resultAttributeValues = loadedAssessmentResult.getSubjectValues().stream()
            .flatMap(sv -> sv.getQualityAttributeValues().stream())
            .toList();
        assertAttributeValues(context.qualityAttributeValues, resultAttributeValues);

        List<Answer> answers = new ArrayList<>(resultAttributeValues.stream()
            .flatMap(qav -> qav.getAnswers().stream())
            .collect(toMap(Answer::getId, Function.identity(), (a, b) -> a))
            .values());

        assertAnswers(context.answerEntities, answers);

    }

    private void doMocks(Context context) {
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(context.assessmentResultEntity().getAssessment().getId()))
            .thenReturn(Optional.of(context.assessmentResultEntity()));
        when(subjectValueRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.subjectValues());
        when(qualityAttrValueRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.qualityAttributeValues());
        when(subjectRepository.loadByKitIdWithAttributes(context.assessmentResultEntity().getAssessment().getAssessmentKitId()))
            .thenReturn(context.subjects);
        when(questionRestAdapter.loadByAssessmentKitId(context.assessmentResultEntity().getAssessment().getAssessmentKitId()))
            .thenReturn(context.questionDtos());
        when(answerRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.answerEntities());
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any()))
            .thenReturn(context.answerOptionDtos());
    }

    record Context(
        AssessmentResultJpaEntity assessmentResultEntity,
        List<SubjectValueJpaEntity> subjectValues,
        List<QualityAttributeValueJpaEntity> qualityAttributeValues,
        List<SubjectJpaEntity> subjects,
        List<QuestionDto> questionDtos,
        List<AnswerJpaEntity> answerEntities,
        List<AnswerOptionDto> answerOptionDtos
    ) {
    }
}
