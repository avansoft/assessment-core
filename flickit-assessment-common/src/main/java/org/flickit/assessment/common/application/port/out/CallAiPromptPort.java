package org.flickit.assessment.common.application.port.out;

import org.springframework.ai.chat.prompt.Prompt;

public interface CallAiPromptPort {

    String call(Prompt prompt);
}
