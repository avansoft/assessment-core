package org.flickit.assessment.kit.adapter.out.persistence.constraint;

import org.flickit.assessment.common.exception.handler.DataConstraintErrorMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Map.entry;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_EXPERT_GROUP_TITLE_DUPLICATE;

@Component
public class AssessmentKitDataConstraintErrorMapper implements DataConstraintErrorMapper {

    Map<String, String> constraintToErrorMsg = Map.ofEntries(
        entry("uq_fak_assessmentkit_code", CREATE_KIT_BY_DSL_KIT_TITLE_DUPLICATE),
        entry("fk_fak_kit_user_access_account_user", GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_FOUND),
        entry("fk_fak_kit_user_access_assessmentkit", GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND),
        entry("fak_kit_user_access_pkey", GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE),
        entry("baseinfo_expertgroup_name_key", CREATE_EXPERT_GROUP_TITLE_DUPLICATE));

    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
