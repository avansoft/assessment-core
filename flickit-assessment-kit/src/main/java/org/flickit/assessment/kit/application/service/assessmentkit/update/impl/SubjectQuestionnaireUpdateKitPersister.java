package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.DeleteSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;


@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectQuestionnaireUpdateKitPersister implements UpdateKitPersister {

    private final LoadSubjectQuestionnairePort loadPort;
    private final DeleteSubjectQuestionnairePort deletePort;
    private final CreateSubjectQuestionnairePort createPort;

    @Override
    public int order() {
        return 6;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        var questionnaireIdToSubjectIdMap = extractQuestionnaireIdToSubjectIdMap(ctx, savedKit, dslKit);
        var savedSubjectQuestionnaires = loadPort.loadByKitId(savedKit.getId());
        var savedQuestionnaireIdToSubjectIdToIdMap =
            questionnaireIdToSubjectIdToIdMap(savedSubjectQuestionnaires);

        updateSubjectQuestionnaires(savedQuestionnaireIdToSubjectIdToIdMap, questionnaireIdToSubjectIdMap);

        return new UpdateKitPersisterResult(false);
    }

    private Map<Long, Set<Long>> extractQuestionnaireIdToSubjectIdMap(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        var attributeCodeToSubjectIdMap = attributeCodeToSubjectIdMap(savedKit);
        Map<String, Long> questionnaireCodeToIdMap = ctx.get(KEY_QUESTIONNAIRES);

        return dslKit.getQuestions().stream()
            .collect(Collectors.toMap(
                q -> questionnaireCodeToIdMap.get(q.getQuestionnaireCode()),
                q ->
                    q.getQuestionImpacts().stream()
                        .map(QuestionImpactDslModel::getAttributeCode)
                        .map(attributeCodeToSubjectIdMap::get)
                            .collect(Collectors.toSet())
                )
            );
    }

    private Map<String, Long> attributeCodeToSubjectIdMap(AssessmentKit savedKit) {
        Map<String, Long> attributeCodeToSubjectIdMap = new HashMap<>();
        for (Subject subject : savedKit.getSubjects()) {
            long subjectId = subject.getId();
            var attributeCodes = subject.getAttributes().stream()
                .map(Attribute::getCode)
                .collect(Collectors.toSet());
            for (String attributeCode : attributeCodes) {
                attributeCodeToSubjectIdMap.put(attributeCode, subjectId);
            }
        }
        return attributeCodeToSubjectIdMap;
    }

    private Map<Long, HashMap<Long, Long>> questionnaireIdToSubjectIdToIdMap(List<SubjectQuestionnaire> subjectQuestionnaires) {
        Map<Long, HashMap<Long, Long>> result = new HashMap<>();
        for (SubjectQuestionnaire entity : subjectQuestionnaires) {
            Long questionnaireId = entity.getQuestionnaireId();
            if (result.containsKey(questionnaireId)) {
                result.get(questionnaireId).put(entity.getSubjectId(), entity.getId());
            } else {
                HashMap<Long, Long> subjectIdToIdMap = new HashMap<>();
                subjectIdToIdMap.put(entity.getSubjectId(), entity.getId());
                result.put(questionnaireId, subjectIdToIdMap);
            }
        }
        return result;
    }

    private void updateSubjectQuestionnaires(Map<Long, HashMap<Long, Long>> savedQuestionnaireIdToSubjectIdMap,
                                             Map<Long, Set<Long>> questionnaireIdToSubjectIdMap) {
        for (Map.Entry<Long, Set<Long>> entry : questionnaireIdToSubjectIdMap.entrySet()) {
            Long questionnaireId = entry.getKey();
            var savedSubjectIdToIdMap = savedQuestionnaireIdToSubjectIdMap.getOrDefault(questionnaireId, new HashMap<>());

            var savedSubjectIds = savedSubjectIdToIdMap.keySet();
            var subjectIds = entry.getValue();

            var deletedSubjectIds = savedSubjectIds.stream().filter(id -> !subjectIds.contains(id))
                .collect(Collectors.toSet());
            var addedSubjectIds = subjectIds.stream().filter(id -> !savedSubjectIds.contains(id))
                .collect(Collectors.toSet());

            for (Long subjectId : deletedSubjectIds) {
                deletePort.delete(savedSubjectIdToIdMap.get(subjectId));
            }
            for (Long subjectId : addedSubjectIds) {
                createPort.persist(subjectId, questionnaireId);
            }
        }
    }
}
