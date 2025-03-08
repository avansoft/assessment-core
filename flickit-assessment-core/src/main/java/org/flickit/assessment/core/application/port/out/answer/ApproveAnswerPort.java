package org.flickit.assessment.core.application.port.out.answer;

import java.util.UUID;

public interface ApproveAnswerPort {

    void approve(UUID answerId, UUID approvedBy);

    void approveAll(UUID assessmentId, UUID approvedBy);
}
