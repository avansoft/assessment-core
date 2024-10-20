package org.flickit.assessment.core.application.service.assessment.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope.User;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.notification.CreateAssessmentNotificationCmd;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitInfoPort;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.KitModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.core.common.MessageKey.NOTIFICATION_TITLE_CREATE_ASSESSMENT;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateAssessmentNotificationCreator implements NotificationCreator<CreateAssessmentNotificationCmd> {

    private final LoadKitInfoPort loadKitInfoPort;

    @Override
    public List<NotificationEnvelope> create(CreateAssessmentNotificationCmd cmd) {
        try {
            var title = MessageBundle.message(NOTIFICATION_TITLE_CREATE_ASSESSMENT);
            var kitInfo = loadKitInfoPort.loadKitInfo(cmd.kitId());
            KitModel kitModel = new KitModel(cmd.kitId(), kitInfo.title());
            var payload = new CreateAssessmentNotificationPayload(kitModel);
            return List.of(new NotificationEnvelope(new User(kitInfo.createdBy(), null), title, payload));
        } catch (ResourceNotFoundException e) {
            log.warn("kit not found");
            return List.of();
        }
    }

    @Override
    public Class<CreateAssessmentNotificationCmd> cmdClass() {
        return CreateAssessmentNotificationCmd.class;
    }
}
