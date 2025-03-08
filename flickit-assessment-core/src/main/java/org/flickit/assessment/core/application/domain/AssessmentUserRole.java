package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.PermissionGroup.*;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentUserRole {

    VIEWER(2, "Viewer", VIEWER_PERMISSIONS, REPORT_VIEWER_PERMISSIONS),
    COMMENTER(3, "Commenter", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, REPORT_VIEWER_PERMISSIONS),
    ASSESSOR(5, "Assessor", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS, REPORT_VIEWER_PERMISSIONS),
    MANAGER(6, "Manager", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS, MANAGER_PERMISSIONS, REPORT_VIEWER_PERMISSIONS),
    ASSOCIATE(4, "Associate", ASSOCIATE_PERMISSIONS),
    REPORT_VIEWER(1, "ReportViewer", REPORT_VIEWER_PERMISSIONS);

    private final int index;
    private final String title;
    private final Set<AssessmentPermission> permissions;

    AssessmentUserRole(int index, String title, PermissionGroup... permissionsGroups) {
        this.index = index;
        this.title = title;
        this.permissions = Arrays.stream(permissionsGroups)
            .flatMap(x -> x.getPermissions().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    public int getId() {
        return this.ordinal();
    }

    public static AssessmentUserRole valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AssessmentUserRole.values().length;
    }

    public boolean hasAccess(AssessmentPermission permission) {
        return this.getPermissions().contains(permission);
    }

    public String getDescription() {
        return MessageBundle.message("AssessmentUserRole_" + name());
    }

    @Getter
    @RequiredArgsConstructor
    enum PermissionGroup {

        VIEWER_PERMISSIONS(Set.of(
            VIEW_ASSESSMENT_REPORT,
            CALCULATE_ASSESSMENT,
            MIGRATE_KIT_VERSION,
            CALCULATE_CONFIDENCE,
            VIEW_ASSESSMENT_LIST,
            VIEW_ASSESSMENT_PROGRESS,
            VIEW_SUBJECT_PROGRESS,
            VIEW_SUBJECT_REPORT,
            VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
            VIEW_EVIDENCE_ATTACHMENT,
            EXPORT_ASSESSMENT_REPORT,
            VIEW_ASSESSMENT_INSIGHTS)),
        COMMENTER_PERMISSIONS(Set.of(
            ADD_EVIDENCE,
            DELETE_EVIDENCE,
            VIEW_EVIDENCE_LIST,
            VIEW_COMMENT_LIST,
            UPDATE_EVIDENCE,
            ADD_EVIDENCE_ATTACHMENT,
            DELETE_EVIDENCE_ATTACHMENT,
            VIEW_QUESTIONNAIRE_QUESTIONS,
            VIEW_EVIDENCE)),
        ASSESSOR_PERMISSIONS(Set.of(
            ANSWER_QUESTION,
            VIEW_ATTRIBUTE_SCORE_DETAIL,
            CREATE_ADVICE,
            VIEW_ANSWER_HISTORY_LIST,
            CREATE_ATTRIBUTE_INSIGHT,
            CREATE_ASSESSMENT_INSIGHT,
            CREATE_SUBJECT_INSIGHT,
            MANAGE_ADD_ON,
            MANAGE_KIT_CUSTOM,
            MANAGE_ADVICE_ITEM,
            RESOLVE_COMMENT,
            RESOLVE_OWN_COMMENT,
            VIEW_DASHBOARD,
            APPROVE_ATTRIBUTE_INSIGHT,
            APPROVE_SUBJECT_INSIGHT,
            APPROVE_ASSIGNMENT_INSIGHT,
            MANAGE_REPORT_METADATA,
            PUBLISH_ASSESSMENT_REPORT,
            VIEW_REPORT_PREVIEW,
            APPROVE_ANSWER,
            APPROVE_ALL_ASSESSMENT_INSIGHTS,
            GENERATE_ALL_ASSESSMENT_INSIGHTS,
            RESOLVE_ALL_COMMENTS,
            APPROVE_ALL_ANSWERS)),
        MANAGER_PERMISSIONS(Set.of(
            CREATE_ASSESSMENT,
            DELETE_ASSESSMENT,
            UPDATE_ASSESSMENT,
            GRANT_USER_ASSESSMENT_ROLE,
            UPDATE_USER_ASSESSMENT_ROLE,
            DELETE_USER_ASSESSMENT_ROLE,
            VIEW_ASSESSMENT_USER_LIST,
            VIEW_ASSESSMENT_INVITEE_LIST,
            DELETE_ASSESSMENT_INVITE)),
        ASSOCIATE_PERMISSIONS(Set.of(
            VIEW_ASSESSMENT_LIST,
            VIEW_ASSESSMENT_PROGRESS,
            VIEW_ASSESSMENT,
            VIEW_SUBJECT_PROGRESS,
            ADD_EVIDENCE,
            DELETE_EVIDENCE,
            VIEW_EVIDENCE_LIST,
            VIEW_COMMENT_LIST,
            UPDATE_EVIDENCE,
            VIEW_EVIDENCE_ATTACHMENT,
            ADD_EVIDENCE_ATTACHMENT,
            DELETE_EVIDENCE_ATTACHMENT,
            VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
            VIEW_QUESTIONNAIRE_QUESTIONS,
            ANSWER_QUESTION,
            VIEW_EVIDENCE,
            RESOLVE_OWN_COMMENT)),
        REPORT_VIEWER_PERMISSIONS(Set.of(
            VIEW_ASSESSMENT,
            GRANT_ACCESS_TO_REPORT,
            VIEW_GRAPHICAL_REPORT,
            VIEW_ASSESSMENT_ATTRIBUTES,
            VIEW_ASSESSMENT_MATURITY_LEVELS
        ));

        private final Set<AssessmentPermission> permissions;
    }
}
