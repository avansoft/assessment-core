package org.flickit.assessment.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {
    public static final String ASSESSMENT_KIT_ID_NOT_FOUND = "assessment-kit-id.notFound";
    public static final String EVIDENCE_ID_NOT_FOUND = "evidence-id.notFound";
    public static final String EVIDENCE_ATTACHMENT_ID_NOT_FOUND = "evidence-attachment-id.notFound";
    public static final String ASSESSMENT_ID_NOT_FOUND = "assessment-id.notFound";
    public static final String USER_ID_NOT_FOUND = "user.id.notFound";
    public static final String QUESTIONNAIRE_ID_NOT_FOUND = "questionnaire.id.notFound";
    public static final String QUESTION_ID_NOT_FOUND = "question.id.notFound";
    public static final String ATTRIBUTE_ID_NOT_FOUND = "attribute.id.notFound";
    public static final String MATURITY_LEVEL_ID_NOT_FOUND = "maturity-level.id.notFound";
    public static final String ASSESSMENT_INVITE_ID_NOT_FOUND = "assessment-invite.id.notFound";
    public static final String ANALYSIS_TYPE_ID_NOT_VALID = "analysis-type.id.notValid";
    public static final String KIT_CUSTOM_ID_NOT_FOUND = "kit-custom.id.notFound";
    public static final String ATTRIBUTE_INSIGHT_ID_NOT_FOUND = "attribute-insight.id.notFound";
    public static final String SUBJECT_INSIGHT_ID_NOT_FOUND = "subject-insight.id.notFound";
    public static final String ASSESSMENT_INSIGHT_ID_NOT_FOUND = "assessment-insight.id.notFound";
    public static final String SUBJECT_VALUE_NOT_FOUND = "subjectValue.subjectIdAssessmentResultId.notFound";
    public static final String SUBJECT_NOT_FOUND = "subjectId.notFound";

    public static final String CREATE_ASSESSMENT_TITLE_NOT_BLANK = "create-assessment.title.notBlank";
    public static final String CREATE_ASSESSMENT_TITLE_SIZE_MIN = "create-assessment.title.size.min";
    public static final String CREATE_ASSESSMENT_TITLE_SIZE_MAX = "create-assessment.title.size.max";
    public static final String CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MIN = "create-assessment.shortTitle.size.min";
    public static final String CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX = "create-assessment.shortTitle.size.max";
    public static final String CREATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL = "create-assessment.assessmentKitId.notNull";
    public static final String CREATE_ASSESSMENT_SPACE_ID_NOT_NULL = "create-assessment.spaceId.notNull";
    public static final String CREATE_ASSESSMENT_DUPLICATE_TITLE_AND_SPACE_ID = "create-assessment.duplicate.titleAndSpaceId";
    public static final String CREATE_ASSESSMENT_KIT_NOT_ALLOWED = "create-assessment.kit.notAllowed";
    public static final String CREATE_ASSESSMENT_BASIC_SPACE_ASSESSMENTS_MAX = "create-assessment.basicSpaceAssessments.max";
    public static final String CREATE_ASSESSMENT_BASIC_SPACE_PRIVATE_KIT_NOT_ALLOWED = "create-assessment.basicSpacePrivateKits.notAllowed";
    public static final String CREATE_ASSESSMENT_PREMIUM_SPACE_EXPIRED = "create-assessment.premiumSpace.expired";

    public static final String CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND = "create-assessmentresult.assessmentId.notFound";

    public static final String CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND = "create-attributevalue.assessmentResultId.notFound";

    public static final String CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND = "crate-subjectvalue.assessmentResultId.notFound";

    public static final String SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND = "submit-answer.assessmentResult.notFound";
    public static final String SUBMIT_ANSWER_ASSESSMENT_ID_NOT_NULL = "submit-answer.assessmentId.notNull";
    public static final String SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL = "submit-answer.questionnaireId.notNull";
    public static final String SUBMIT_ANSWER_QUESTION_ID_NOT_NULL = "submit-answer.questionId.notNull";
    public static final String SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND = "submit-answer.questionId.notFound";
    public static final String SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE = "submit-answer.questionId.notMayNotBeApplicable";
    public static final String SUBMIT_ANSWER_ANSWER_OPTION_ID_NOT_FOUND = "submit-answer.answerOptionId.notFound";
    public static final String SUBMIT_ANSWER_ANSWER_ID_NOT_FOUND = "submit-answer.answerId.notFound";

    public static final String GET_ASSESSMENT_LIST_SIZE_MIN = "get-assessment-list.size.min";
    public static final String GET_ASSESSMENT_LIST_SIZE_MAX = "get-assessment-list.size.max";
    public static final String GET_ASSESSMENT_LIST_PAGE_MIN = "get-assessment-list.page.min";

    public static final String GET_SPACE_ASSESSMENT_LIST_SPACE_ID_NOT_NULL = "get-space-assessment-list.spaceId.notNull";
    public static final String GET_SPACE_ASSESSMENT_LIST_SIZE_MIN = "get-space-assessment-list.size.min";
    public static final String GET_SPACE_ASSESSMENT_LIST_SIZE_MAX = "get-space-assessment-list.size.max";
    public static final String GET_SPACE_ASSESSMENT_LIST_PAGE_MIN = "get-space-assessment-list.page.min";

    public static final String ADD_EVIDENCE_ASSESSMENT_ID_NOT_FOUND = "add-evidence.assessmentId.notFound";
    public static final String ADD_EVIDENCE_DESC_NOT_BLANK = "add-evidence.desc.notBlank";
    public static final String ADD_EVIDENCE_DESC_SIZE_MIN = "add-evidence.desc.size.min";
    public static final String ADD_EVIDENCE_DESC_SIZE_MAX = "add-evidence.desc.size.max";
    public static final String ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL = "add-evidence.assessmentId.notNull";
    public static final String ADD_EVIDENCE_QUESTION_ID_NOT_NULL = "add-evidence.questionId.notNull";
    public static final String ADD_EVIDENCE_QUESTION_ID_NOT_FOUND = "add-evidence.questionId.notFound";
    public static final String ADD_EVIDENCE_TYPE_INVALID = "add-evidence.type.invalid";

    public static final String UPDATE_ASSESSMENT_ID_NOT_NULL = "update-assessment.id.notNull";
    public static final String UPDATE_ASSESSMENT_ID_NOT_FOUND = "update-assessment.id.notFound";
    public static final String UPDATE_ASSESSMENT_TITLE_NOT_BLANK = "update-assessment.title.notBlank";
    public static final String UPDATE_ASSESSMENT_TITLE_SIZE_MIN = "update-assessment.title.min";
    public static final String UPDATE_ASSESSMENT_TITLE_SIZE_MAX = "update-assessment.title.max";
    public static final String UPDATE_ASSESSMENT_SHORT_TITLE_SIZE_MIN = "update-assessment.shortTitle.size.min";
    public static final String UPDATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX = "update-assessment.shortTitle.size.max";

    public static final String CALCULATE_ASSESSMENT_ID_NOT_NULL = "calculate-assessment.assessment.id.notNull";
    public static final String CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND = "calculate-assessment.assessmentResult.notFount";

    public static final String GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL = "get-evidence-list.questionId.notNull";
    public static final String GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL = "get-evidence-list.assessmentId.notNull";
    public static final String GET_EVIDENCE_LIST_SIZE_MIN = "get-evidence-list.size.min";
    public static final String GET_EVIDENCE_LIST_SIZE_MAX = "get-evidence-list.size.max";
    public static final String GET_EVIDENCE_LIST_PAGE_MIN = "get-evidence-list.page.min";

    public static final String REPORT_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND = "report-assessment-assessment.id.notFound";
    public static final String REPORT_ASSESSMENT_ID_NOT_NULL = "report-assessment.assessment.id.notNull";
    public static final String REPORT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND = "report-assessment.assessmentResult.notFound";
    public static final String REPORT_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND = "report-assessment.assessmentKit.notFound";
    public static final String REPORT_ASSESSMENT_EXPERT_GROUP_NOT_FOUND = "report-assessment.expertGroup.notFound";
    public static final String REPORT_ASSESSMENT_SPACE_NOT_FOUND = "report-assessment.space.notFound";

    public static final String GET_ASSESSMENT_PROGRESS_ASSESSMENT_ID_NOT_NULL = "get-assessment-progress.assessment.id.notNull";
    public static final String GET_ASSESSMENT_PROGRESS_ASSESSMENT_NOT_FOUND = "get-assessment-progress.assessment.notFound";

    public static final String REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL = "report-subject.assessment.id.notNull";
    public static final String REPORT_SUBJECT_ID_NOT_NULL = "report-subject.subject.id.notNull";
    public static final String REPORT_SUBJECT_ID_NOT_FOUND = "report-subject.subject.id.notFound";
    public static final String REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND = "report-subject.assessmentResult.notFound";
    public static final String REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND = "report-subject.subjectValue.notFound";

    public static final String UPDATE_EVIDENCE_ID_NOT_NULL = "update-evidence.id.notNull";
    public static final String UPDATE_EVIDENCE_DESC_NOT_BLANK = "update-evidence.description.notBlank";
    public static final String UPDATE_EVIDENCE_DESC_MIN_SIZE = "update-evidence.description.size.min";
    public static final String UPDATE_EVIDENCE_DESC_MAX_SIZE = "update-evidence.description.size.max";
    public static final String UPDATE_EVIDENCE_TYPE_INVALID = "update-evidence.type.invalid";

    public static final String GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL = "get-subject-progress.assessment.id.notNull";
    public static final String GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND = "get-subject-progress.assessmentResultId.notFound";
    public static final String GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL = "get-subject-progress.subject.id.notNull";
    public static final String GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_FOUND = "get-subject-progress.subject.id.notFound";

    public static final String GET_ASSESSMENT_ASSESSMENT_ID_NOT_NULL = "get-assessment.assessmentId.notNull";
    public static final String GET_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND = "get-assessment.assessmentId.notFound";
    public static final String GET_ASSESSMENT_ASSESSMENT_CREATED_BY_ID_NOT_FOUND = "get-assessment.createdById.notFound";
    public static final String GET_ASSESSMENT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND = "get-assessment.assessmentResultId.notFound";

    public static final String DELETE_ASSESSMENT_ID_NOT_NULL = "delete-assessment.id.notNull";
    public static final String DELETE_ASSESSMENT_ID_NOT_FOUND = "delete-assessment.id.notFound";

    public static final String DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL = "delete-evidence.id.notNull";

    public static final String CALCULATE_CONFIDENCE_ASSESSMENT_ID_NOT_NULL = "calculate-confidence.assessment.id.notNull";
    public static final String CALCULATE_CONFIDENCE_ASSESSMENT_RESULT_NOT_FOUND = "calculate-confidence.assessmentResult.notFount";

    public static final String GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND = "get-attribute-score-detail.assessmentResult.notFound";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_ID_NOT_NULL = "get-attribute-score-detail.assessment.id.notNull";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_ATTRIBUTE_ID_NOT_NULL = "get-attribute-score-detail.attribute.id.notNull";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_MATURITY_LEVEL_ID_NOT_NULL = "get-attribute-score-detail.maturityLevel.id.notNull";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_SORT_INVALID = "get-attribute-score-detail.sort.invalid";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_ORDER_INVALID = "get-attribute-score-detail.order.invalid";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MIN = "get-attribute-score-detail.size.min";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_SIZE_MAX = "get-attribute-score-detail.size.max";
    public static final String GET_ATTRIBUTE_SCORE_DETAIL_PAGE_MIN = "get-attribute-score-detail.page.min";

    public static final String GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL = "get-assessment-questionnaire-list.assessmentId.notNull";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_LIST_CURRENT_USER_ID_NOT_NULL = "get-assessment-questionnaire-list.currentUserId.notNull";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MIN = "get-assessment-questionnaire-list.size.min";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MAX = "get-assessment-questionnaire-list.size.max";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_LIST_PAGE_MIN = "get-assessment-questionnaire-list.page.min";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND = "get-assessment-questionnaire-list.assessmentResultId.notFound";

    public static final String GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_NULL = "get-assessment-questionnaire-question-list.assessmentId.notNull";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_QUESTIONNAIRE_ID_NOT_NULL = "get-assessment-questionnaire-question-list.questionnaireId.notNull";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_SIZE_MIN = "get-assessment-questionnaire-question-list.size.min";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_SIZE_MAX = "get-assessment-questionnaire-question-list.size.max";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_PAGE_MIN = "get-assessment-questionnaire-question-list.page.min";
    public static final String GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_FOUND = "get-assessment-questionnaire-question-list.assessmentResultId.notFound";

    public static final String GRANT_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL = "grant-assessment-user-role.assessmentId.notNull";
    public static final String GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL = "grant-assessment-user-role.userId.notNull";
    public static final String GRANT_ASSESSMENT_USER_ROLE_USER_ROLE_DUPLICATE = "grant-assessment-user-role.user.role.duplicate";
    public static final String GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_NULL = "grant-assessment-user-role.roleId.notNull";
    public static final String GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_FOUND = "grant-assessment-user-role.roleId.notFound";
    public static final String GRANT_ASSESSMENT_USER_ROLE_DUPLICATE_USER_ACCESS = "grant-assessment-user-role.duplicateUserAccess";

    public static final String UPDATE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL = "update-assessment-user-role.assessmentId.notNull";
    public static final String UPDATE_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL = "update-assessment-user-role.userId.notNull";
    public static final String UPDATE_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_NULL = "update-assessment-user-role.roleId.notNull";
    public static final String UPDATE_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER = "update-assessment-user-role.userId.notMember";
    public static final String UPDATE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_USER_ID_NOT_FOUND = "update-assessment-user-role.assessmentId.userId.notFound";
    public static final String UPDATE_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_FOUND = "update-assessment-user-role.roleId.notFound";
    public static final String UPDATE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER = "update-assessment-user-role.userId.isSpaceOwner";

    public static final String DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL = "delete-assessment-user-role.assessmentId.notNull";
    public static final String DELETE_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL = "delete-assessment-user-role.userId.notNull";
    public static final String DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_USER_ID_NOT_FOUND = "delete-assessment-user-role.assessmentId.userId.notFound";
    public static final String DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER = "delete-assessment-user-role.assessmentId.userId.isSpaceOwner";

    public static final String GET_ASSESSMENT_USERS_ASSESSMENT_ID_NOT_NULL = "get-assessment-users.assessmentId.notNull";
    public static final String GET_ASSESSMENT_USERS_SIZE_MIN = "get-assessment-users.size.min";
    public static final String GET_ASSESSMENT_USERS_SIZE_MAX = "get-assessment-users.size.max";
    public static final String GET_ASSESSMENT_USERS_PAGE_MIN = "get-assessment-users.page.min";

    public static final String ADD_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL = "add-evidence-attachment.evidenceId.notNull";
    public static final String ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_NOT_NULL = "add-evidence-attachment.attachment.notNull";
    public static final String ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_COUNT_MAX = "add-evidence-attachment.attachmentsCount.max";
    public static final String ADD_EVIDENCE_ATTACHMENT_DESCRIPTION_SIZE_MIN = "add-evidence-attachment.description.size.min";
    public static final String ADD_EVIDENCE_ATTACHMENT_DESCRIPTION_SIZE_MAX = "add-evidence-attachment.description.size.max";

    public static final String GET_EVIDENCE_ATTACHMENTS_EVIDENCE_ID_NULL = "get-evidence-attachments.evidenceId.notNull";

    public static final String GET_ASSESSMENT_INVITEE_LIST_ASSESSMENT_ID_NOT_NULL = "get-assessment-invitee-list.assessmentId.notNull";
    public static final String GET_ASSESSMENT_INVITEE_LIST_SIZE_MIN = "get-assessment-invitee-list.size.min";
    public static final String GET_ASSESSMENT_INVITEE_LIST_SIZE_MAX = "get-assessment-invitee-list.size.max";
    public static final String GET_ASSESSMENT_INVITEE_LIST_PAGE_MIN = "get-assessment-invitee-list.page.min";

    public static final String INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL = "invite-assessment-user.assessmentId.notNull";
    public static final String INVITE_ASSESSMENT_USER_EMAIL_NOT_NULL = "invite-assessment-user.email.notNull";
    public static final String INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL = "invite-assessment-user.roleId.notNull";
    public static final String INVITE_ASSESSMENT_USER_ROLE_ID_NOT_FOUND = "invite-assessment-user.roleId.notFound";

    public static final String DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL = "delete-evidence-attachment.evidenceId.notNull";
    public static final String DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL = "delete-evidence-attachment.attachmentId.notNull";

    public static final String ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_NULL = "accept-assessment-invitations.userId.notNull";

    public static final String GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL = "get-assessment-current-user-permissions.assessmentId.notNull";

    public static final String GET_EVIDENCE_ID_NOT_NULL = "get-evidence.id.notNull";
    public static final String GET_EVIDENCE_ASSESSMENT_RESULT_NOT_FOUND = "get-evidence.assessmentResultId.notFound";

    public static final String GET_ANSWER_HISTORY_LIST_ASSESSMENT_ID_NOT_NULL = "get-answer-history-list.assessmentId.notNull";
    public static final String GET_ANSWER_HISTORY_LIST_QUESTION_ID_NOT_NULL = "get-answer-history-list.questionId.notNull";
    public static final String GET_ANSWER_HISTORY_LIST_SIZE_MIN = "get-answer-history-list.size.min";
    public static final String GET_ANSWER_HISTORY_LIST_SIZE_MAX = "get-answer-history-list.size.max";
    public static final String GET_ANSWER_HISTORY_LIST_PAGE_MIN = "get-answer-history-list.page.min";
    public static final String GET_ANSWER_HISTORY_LIST_ASSESSMENT_RESULT_NOT_FOUND = "get-answer-history-list.assessmentResult.notFound";

    public static final String CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_ID_NOT_NULL = "create-attribute-ai-insight.assessmentId.notNull";
    public static final String CREATE_ATTRIBUTE_AI_INSIGHT_ATTRIBUTE_ID_NOT_NULL = "create-attribute-ai-insight.attributeId.notNull";
    public static final String CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "create-attribute-ai-insight.assessmentResult.notFound";
    public static final String CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED = "create-attribute-ai-insight.all-questions.notAnswered";

    public static final String DELETE_ASSESSMENT_INVITE_ID_NOT_NULL = "delete-assessment-invite.id.notNull";

    public static final String UPDATE_ASSESSMENT_INVITE_ID_NOT_NULL = "update-assessment-invite.inviteId.notNull";
    public static final String UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_NULL = "update-assessment-invite.roleId.notNull";
    public static final String UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_FOUND = "update-assessment-invite.roleId.notFound";

    public static final String CREATE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL = "create-attribute-insight.assessmentId.notNull";
    public static final String CREATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL = "create-attribute-insight.attributeId.notNull";
    public static final String CREATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_NOT_NULL = "create-attribute-insight.assessorInsight.notNull";
    public static final String CREATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_SIZE_MAX = "create-attribute-insight.assessorInsight.size.max";
    public static final String CREATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "create-attribute-insight.assessmentResult.notFound";

    public static final String GET_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL = "get-attribute-insight.assessmentId.notNull";
    public static final String GET_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL = "get-attribute-insight.attributeId.notNull";
    public static final String GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "get-attribute-insight.assessmentResult.notFound";

    public static final String CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "create-assessment-insight.assessmentId.notNull";
    public static final String CREATE_ASSESSMENT_INSIGHT_INSIGHT_NOT_NULL = "create-assessment-insight.insight.notNull";
    public static final String CREATE_ASSESSMENT_INSIGHT_INSIGHT_SIZE_MIN = "create-assessment-insight.insight.size.min";
    public static final String CREATE_ASSESSMENT_INSIGHT_INSIGHT_SIZE_MAX = "create-assessment-insight.insight.size.max";
    public static final String CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "create-assessment-insight.assessmentResult.notFound";
    public static final String CREATE_ASSESSMENT_INSIGHT_INSIGHT_NOT_FOUND = "create-assessment-insight.insight.notFound";
    public static final String CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_DUPLICATE = "create-assessment-insight.assessmentResult.duplicate";

    public static final String CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "create-subject-insight.assessmentId.notNull";
    public static final String CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL = "create-subject-insight.subjectId.notNull";
    public static final String CREATE_SUBJECT_INSIGHT_INSIGHT_NOT_NULL = "create-subject-insight.insight.notNull";
    public static final String CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MIN = "create-subject-insight.insight.size.min";
    public static final String CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MAX = "create-subject-insight.insight.size.max";
    public static final String CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "create-subject-insight.assessmentResult.notFound";

    public static final String GET_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "get-assessment-insight.assessmentId.notNull";
    public static final String GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "get-assessment-insight.assessmentResult.notFound";

    public static final String GET_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "get-subject-insight.assessmentId.notNull";
    public static final String GET_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL = "get-subject-insight.subjectId.notNull";

    public static final String ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_ASSESSMENT_ID_NOT_NULL = "add-assessment-analysis-input-file.assessmentId.notNull";
    public static final String ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_INPUT_FILE_NOT_NULL = "add-assessment-analysis-input-file.inputFile.notNull";
    public static final String ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_ANALYSIS_TYPE_NOT_NULL = "add-assessment-analysis-input-file.analysisType.notNull";

    public static final String MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_NULL = "migrate-assessment-result-kit-version.assessmentId.notNull";
    public static final String MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND = "migrate-assessment-result-kit-version.assessmentResultId.notFound";
    public static final String MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND = "migrate-assessment-result-kit-version.activeVersion.notFound";

    public static final String ASSIGN_KIT_CUSTOM_ASSESSMENT_ID_NOT_NULL = "assign-kit-custom.assessmentId.notNull";
    public static final String ASSIGN_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL = "assign-kit-custom.kitCustomId.notNull";

    public static final String GET_ATTRIBUTE_SCORE_STATS_ASSESSMENT_RESULT_NOT_FOUND = "get-attribute-score-stats.assessmentResult.notFound";
    public static final String GET_ATTRIBUTE_SCORE_STATS_ASSESSMENT_ID_NOT_NULL = "get-attribute-score-stats.assessment.id.notNull";
    public static final String GET_ATTRIBUTE_SCORE_STATS_ATTRIBUTE_ID_NOT_NULL = "get-attribute-score-stats.attribute.id.notNull";
    public static final String GET_ATTRIBUTE_SCORE_STATS_MATURITY_LEVEL_ID_NOT_NULL = "get-attribute-score-stats.maturityLevel.id.notNull";

    public static final String RESOLVE_COMMENT_EVIDENCE_ID_NOT_NULL = "resolve-comment.evidenceId.notNull";
    public static final String RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE = "resolve-comment.incorrect.evidenceType";

    public static final String GET_ASSESSMENT_DASHBOARD_ASSESSMENT_ID_NOT_NULL = "get-assessment-dashboard.assessmentId.notNull";
    public static final String GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND = "get-assessment-dashboard.assessmentResult.notFound";

    public static final String INIT_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "init-assessment-insight.assessmentId.notNull";
    public static final String INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "init-assessment-insight.assessmentResult.notFound";

    public static final String APPROVE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL = "approve-attribute-insight.assessmentId.notNull";
    public static final String APPROVE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL = "approve-attribute-insight.attributeId.notNull";
    public static final String APPROVE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "approve-attribute-insight.assessmentResult.notFound";

    public static final String APPROVE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "approve-subject-insight.assessmentId.notNull";
    public static final String APPROVE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL = "approve-subject-insight.subjectId.notNull";
    public static final String APPROVE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "approve-subject-insight.assessmentResult.notFound";

    public static final String INIT_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "init-subject-insight.assessmentId.notNull";
    public static final String INIT_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL = "init-subject-insight.subjectId.notNull";
    public static final String INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "init-subject-insight.assessmentResult.notFound";

    public static final String APPROVE_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL = "approve-assessment-insight.assessmentId.notNull";
    public static final String APPROVE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND = "approve-assessment-insight.assessmentResult.notFound";

    public static final String GRANT_ACCESS_TO_REPORT_EMAIL_NOT_NULL = "grant-access-to-report.email.notNull";
    public static final String GRANT_ACCESS_TO_REPORT_ASSESSMENT_ID_NOT_NULL = "grant-access-to-report.assessmentId.notNull";
    public static final String GRANT_ACCESS_TO_REPORT_NOT_ALLOWED_CONTACT_ASSESSMENT_MANAGER = "grant-access-to-report.not-allowed.contact-assessment-manager";
    public static final String GRANT_ACCESS_TO_REPORT_USER_ALREADY_GRANTED = "grant-access-to-report.user-already-granted";

    public static final String GET_GRAPHICAL_REPORT_USERS_ASSESSMENT_ID_NOT_NULL = "get-graphical-report-users.assessmentId.notNull";

    public static final String GET_QUESTION_ISSUES_QUESTION_ID_NOT_NULL = "get-question-issues.questionId.notNull";
    public static final String GET_QUESTION_ISSUES_ASSESSMENT_ID_NOT_NULL = "get-question-issues.assessmentId.notNull";
    public static final String GET_QUESTION_ISSUES_ASSESSMENT_RESULT_NOT_FOUND = "get-question-issues.assessmentResult.notFound";

    public static final String GET_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL = "get-assessment-report-metadata.assessmentId.notNull";

    public static final String CREATE_ASSESSMENT_REPORT_METADATA_METADATA_NOT_NULL = "create-assessment-report-metadata.metadata.notNull";
    public static final String CREATE_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL = "create-assessment-report-metadata.assessmentId.notNull";
    public static final String CREATE_ASSESSMENT_REPORT_METADATA_INTRO_SIZE_MAX = "create-assessment-report-metadata.intro.size.max";
    public static final String CREATE_ASSESSMENT_REPORT_METADATA_PROS_AND_CONS_SIZE_MAX = "create-assessment-report-metadata.prosAndCons.size.max";

    public static final String GET_ASSESSMENT_REPORT_ASSESSMENT_ID_NOT_NULL = "get-assessment-report.assessmentId.notNull";
    public static final String GET_ASSESSMENT_REPORT_REPORT_NOT_PUBLISHED = "get-assessment-report.report-not-published";

    public static final String UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_ID_NOT_NULL = "update-assessment-report-publish-status.assessmentId.notNull";
    public static final String UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_REPORT_NOT_FOUND = "update-assessment-report-publish-status.assessmentReport.notFound";
    public static final String UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_PUBLISH_NOT_NULL = "update-assessment-report-publish-status.published.notNull";

    public static final String GET_ASSESSMENT_ATTRIBUTES_ASSESSMENT_ID_NOT_NULL = "get-assessment-attributes.assessmentId.notNull";

    public static final String GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_ID_NOT_NULL = "get-assessment-maturity-levels.assessmentId.notNull";
    public static final String GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_RESULT_NOT_FOUND = "get-assessment-maturity-levels.assessmentResult.notFound";

    public static final String APPROVE_ANSWER_ASSESSMENT_ID_NOT_NULL = "approve-answer.assessmentId.notNull";
    public static final String APPROVE_ANSWER_QUESTION_ID_NOT_NULL = "approve-answer.questionId.notNull";
    public static final String APPROVE_ANSWER_QUESTION_NOT_ANSWERED = "approve-answer.question-not-answered";

    public static final String APPROVE_ALL_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL = "approve-all-assessment-insights.assessmentId.notNull";

    public static final String GENERATE_ALL_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL = "generate-all-assessment-insights.assessmentId.notNull";

    public static final String REGENERATE_EXPIRED_INSIGHTS_ASSESSMENT_ID_NOT_NULL = "regenerate-expired-insights.assessmentId.notNull";

    public static final String APPROVE_EXPIRED_INSIGHTS_ASSESSMENT_ID_NOT_NULL = "approve-expired-insights.assessmentId.notNull";

    public static final String GET_COMMENT_LIST_QUESTION_ID_NOT_NULL = "get-comment-list.questionId.notNull";
    public static final String GET_COMMENT_LIST_ASSESSMENT_ID_NOT_NULL = "get-comment-list.assessmentId.notNull";
    public static final String GET_COMMENT_LIST_SIZE_MIN = "get-comment-list.size.min";
    public static final String GET_COMMENT_LIST_SIZE_MAX = "get-comment-list.size.max";
    public static final String GET_COMMENT_LIST_PAGE_MIN = "get-comment-list.page.min";

    public static final String RESOLVE_ASSESSMENT_COMMENTS_ASSESSMENT_ID_NOT_NULL = "resolve-assessment-comments.assessmentId.notNull";
}
