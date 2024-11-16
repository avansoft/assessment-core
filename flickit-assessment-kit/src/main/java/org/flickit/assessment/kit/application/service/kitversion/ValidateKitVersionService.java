package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
public class ValidateKitVersionService implements ValidateKitVersionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadQuestionsPort loadQuestionsPort;
    private final LoadAnswerRangesPort loadAnswerRangesPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadAttributePort loadAttributePort;

    @Override
    public Result validate(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!KitVersionStatus.UPDATING.equals(kitVersion.getStatus()))
            throw new ValidationException(VALIDATE_KIT_VERSION_STATUS_INVALID);

        var kit = kitVersion.getKit();
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        List<String> errors = new LinkedList<>();
        if (!loadQuestionsPort.loadQuestionsWithoutImpact(param.getKitVersionId()).isEmpty())
            errors.add(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_QUESTION_IMPACT_UNSUPPORTED));
        if (!loadQuestionsPort.loadQuestionsWithoutAnswerRange(param.getKitVersionId()).isEmpty())
            errors.add(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_QUESTION_ANSWER_RANGE_UNSUPPORTED));
        if (!loadAnswerRangesPort.loadByKitVersionIdAndWithoutAnswerOptions(param.getKitVersionId()).isEmpty())
            errors.add(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_ANSWER_RANGE_OPTION_UNSUPPORTED));
        if (!loadSubjectsPort.loadByKitVersionIdAndWithoutAttribute(param.getKitVersionId()).isEmpty())
            errors.add(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_ATTRIBUTE_SUBJECT_UNSUPPORTED));
        if (!loadAttributePort.loadByKitVersionIdAndQuestionsWithoutImpact(param.getKitVersionId()).isEmpty())
            errors.add(MessageBundle.message(VALIDATE_KIT_VERSION_EMPTY_ATTRIBUTE_QUESTION_IMPACT_UNSUPPORTED));

        return toResult(errors);
    }

    private Result toResult(List<String> errors) {
        return new Result(errors.isEmpty(), errors);
    }
}
