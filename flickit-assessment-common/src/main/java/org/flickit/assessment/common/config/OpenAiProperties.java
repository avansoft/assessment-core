package org.flickit.assessment.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties("spring.ai.openai")
@RequiredArgsConstructor
public class OpenAiProperties {

    @NestedConfigurationProperty
    private DefaultChatOptions chatOptions;

    private String attributeAiInsightPrompt = """
        As a software quality assessor, I have evaluated the {title} maturity of a system.
        We define {title} as {description}. The uploaded Excel file contains multiple-choice questions used to assess {title}.
        The Excel columns include the question, a hint, the weight of the question in calculating the overall score,
         and the actual score achieved by the software. Please generate an executive summary highlighting the main strengths and weaknesses in less than 100 words.
        Use polite and considerate language, avoiding any derogatory terms, and do not mention the scores of individual questions.
        Here is the uploaded Excel file: {excelFile}.
        """;

    private String adviceAiNarrationPrompt = """
        As a software quality assessor, I have evaluated the maturity level of a system. Below, I have outlined the selected option and provided the recommended option for improvement.
        Please generate advice in up to 10 concise bullets, with a total character limit of 900 characters, to help achieve the recommended option. You may shorten the bullet list if it improves clarity.
        The advice should be polite, clear, and considerate, avoiding any mention of individual scores or derogatory terms. Focus on offering actionable suggestions. Do not use bold formatting for emphasis.
        Provided advice items: {adviceListItems}.
        Provided attribute level targets: {attributeLevelTargets}.
        """;

    public Prompt createAttributeAiInsightPrompt(String title, String description, String excelFile) {
        var promptTemplate = new PromptTemplate(attributeAiInsightPrompt, Map.of("title", title, "description", description, "excelFile", excelFile));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }

    public Prompt createAdviceAiNarration(String adviceListItems, String attributeLevelTargets) {
        var promptTemplate = new PromptTemplate(adviceAiNarrationPrompt, Map.of("adviceListItems", adviceListItems, "attributeLevelTargets", attributeLevelTargets));
        return new Prompt(promptTemplate.createMessage(), chatOptions);
    }
}
