package org.flickit.assessment.common.application.domain.assessment;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentPermissionTest {

    @Test
    void testAssessmentPermission_PermissionCodesShouldNotBeChanged() {
        Map<AssessmentPermission, String> permissionCodesMap = Map.ofEntries(
            Map.entry(CREATE_ASSESSMENT, "createAssessment"),
            Map.entry(VIEW_ASSESSMENT, "viewAssessment"),
            Map.entry(UPDATE_ASSESSMENT, "updateAssessment"),
            Map.entry(DELETE_ASSESSMENT, "deleteAssessment"),
            Map.entry(VIEW_ASSESSMENT_LIST, "viewAssessmentList"),
            Map.entry(VIEW_ASSESSMENT_PROGRESS, "viewAssessmentProgress"),
            Map.entry(VIEW_ASSESSMENT_REPORT, "viewAssessmentReport"),
            Map.entry(CALCULATE_ASSESSMENT, "calculateAssessment"),
            Map.entry(MIGRATE_KIT_VERSION, "migrateKitVersion"),
            Map.entry(CALCULATE_CONFIDENCE, "calculateConfidence"),
            Map.entry(VIEW_ATTRIBUTE_SCORE_DETAIL, "viewAttributeScoreDetail"),
            Map.entry(VIEW_SUBJECT_PROGRESS, "viewSubjectProgress"),
            Map.entry(VIEW_SUBJECT_REPORT, "viewSubjectReport"),
            Map.entry(CREATE_ADVICE, "createAdvice"),
            Map.entry(ADD_EVIDENCE, "addEvidence"),
            Map.entry(UPDATE_EVIDENCE, "updateEvidence"),
            Map.entry(DELETE_EVIDENCE, "deleteEvidence"),
            Map.entry(VIEW_EVIDENCE_LIST, "viewEvidenceList"),
            Map.entry(ADD_EVIDENCE_ATTACHMENT, "addEvidenceAttachment"),
            Map.entry(VIEW_EVIDENCE_ATTACHMENT, "viewEvidenceAttachment"),
            Map.entry(DELETE_EVIDENCE_ATTACHMENT, "deleteEvidenceAttachment"),
            Map.entry(ANSWER_QUESTION, "answerQuestion"),
            Map.entry(VIEW_QUESTIONNAIRE_QUESTIONS, "viewQuestionnaireQuestions"),
            Map.entry(VIEW_ASSESSMENT_QUESTIONNAIRE_LIST, "viewAssessmentQuestionnaireList"),
            Map.entry(GRANT_USER_ASSESSMENT_ROLE, "grantUserAssessmentRole"),
            Map.entry(UPDATE_USER_ASSESSMENT_ROLE, "updateUserAssessmentRole"),
            Map.entry(DELETE_USER_ASSESSMENT_ROLE, "deleteUserAssessmentRole"),
            Map.entry(VIEW_ASSESSMENT_USER_LIST, "viewAssessmentUserList"),
            Map.entry(VIEW_ASSESSMENT_INVITEE_LIST, "viewAssessmentInviteeList"),
            Map.entry(VIEW_EVIDENCE, "viewEvidence"),
            Map.entry(VIEW_ANSWER_HISTORY_LIST, "viewAnswerHistory"),
            Map.entry(EXPORT_ASSESSMENT_REPORT, "exportAssessmentReport"),
            Map.entry(CREATE_ATTRIBUTE_INSIGHT, "createAttributeInsight"),
            Map.entry(DELETE_ASSESSMENT_INVITE, "deleteAssessmentInvite"),
            Map.entry(MANAGE_ADVICE_ITEM, "manageAdviceItem"),
            Map.entry(CREATE_ASSESSMENT_INSIGHT, "createAssessmentInsight"),
            Map.entry(CREATE_SUBJECT_INSIGHT, "createSubjectInsight"),
            Map.entry(MANAGE_ADD_ON, "manageAddOn"),
            Map.entry(MANAGE_KIT_CUSTOM, "manageKitCustom"),
            Map.entry(RESOLVE_COMMENT, "resolveComment"),
            Map.entry(RESOLVE_OWN_COMMENT, "resolveOwnComment"),
            Map.entry(VIEW_DASHBOARD, "viewDashboard"),
            Map.entry(APPROVE_ATTRIBUTE_INSIGHT, "approveAttributeInsight"),
            Map.entry(GRANT_ACCESS_TO_REPORT, "grantAccessToReport"),
            Map.entry(APPROVE_SUBJECT_INSIGHT, "approveSubjectInsight"),
            Map.entry(APPROVE_ASSIGNMENT_INSIGHT, "approveAssignmentInsight"),
            Map.entry(VIEW_GRAPHICAL_REPORT, "viewGraphicalReport"),
            Map.entry(MANAGE_REPORT_METADATA, "manageReportMetadata"),
            Map.entry(PUBLISH_ASSESSMENT_REPORT, "publishAssessmentReport"),
            Map.entry(VIEW_REPORT_PREVIEW, "viewReportPreview"),
            Map.entry(VIEW_ASSESSMENT_ATTRIBUTES, "viewAssessmentAttributes"),
            Map.entry(VIEW_ASSESSMENT_MATURITY_LEVELS, "viewAssessmentMaturityLevels"),
            Map.entry(APPROVE_ANSWER, "approveAnswer"),
            Map.entry(APPROVE_ALL_ASSESSMENT_INSIGHTS, "approveAllAssessmentInsights"),
            Map.entry(GENERATE_ALL_ASSESSMENT_INSIGHTS, "generateAllAssessmentInsights"),
            Map.entry(VIEW_COMMENT_LIST, "viewCommentList"),
            Map.entry(RESOLVE_ALL_COMMENTS, "resolveAllComments"),
            Map.entry(VIEW_ASSESSMENT_INSIGHTS, "viewAssessmentInsights"),
            Map.entry(APPROVE_ALL_ANSWERS, "approveAllAnswers"));

        permissionCodesMap.forEach((key, value) -> assertEquals(value, key.getCode()));
        Arrays.stream(AssessmentPermission.values()).forEach(e ->
            assertTrue(permissionCodesMap.containsValue(e.getCode()))
        );
        assertEquals(59, AssessmentPermission.values().length);
    }
}
