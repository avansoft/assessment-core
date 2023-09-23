package org.flickit.flickitassessmentcore.application.service.subject;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectReportInfoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.flickit.flickitassessmentcore.application.domain.mother.AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel;
import static org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother.*;
import static org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeMother.simpleAttribute;
import static org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeValueMother.withAttributeAndMaturityLevel;
import static org.flickit.flickitassessmentcore.application.domain.mother.SubjectValueMother.withQAValuesAndMaturityLevelAndSubjectWithQAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSubjectServiceTest {

    @InjectMocks
    private ReportSubjectService service;

    @Mock
    private LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Test
    void testReportSubject_ValidResult() {
        QualityAttribute attribute1 = simpleAttribute();
        QualityAttribute attribute2 = simpleAttribute();
        QualityAttribute attribute3 = simpleAttribute();
        QualityAttribute attribute4 = simpleAttribute();
        QualityAttribute attribute5 = simpleAttribute();
        List<QualityAttribute> attributes = List.of(
            attribute1,
            attribute2,
            attribute3,
            attribute4,
            attribute5
        );
        List<QualityAttributeValue> qaValues = List.of(
            withAttributeAndMaturityLevel(attribute1, levelOne()),
            withAttributeAndMaturityLevel(attribute2, levelTwo()),
            withAttributeAndMaturityLevel(attribute3, levelThree()),
            withAttributeAndMaturityLevel(attribute4, levelFour()),
            withAttributeAndMaturityLevel(attribute5, levelFive())
        );
        SubjectValue subjectValue = withQAValuesAndMaturityLevelAndSubjectWithQAs
            (qaValues, MaturityLevelMother.levelThree(), attributes);
        AssessmentResult assessmentResult = validResultWithSubjectValuesAndMaturityLevel(
            List.of(subjectValue), levelTwo());

        ReportSubjectUseCase.Param param = new ReportSubjectUseCase.Param(
            assessmentResult.getAssessment().getId(),
            subjectValue.getSubject().getId());

        when(loadSubjectReportInfoPort.load(assessmentResult.getAssessment().getId(), subjectValue.getSubject().getId()))
            .thenReturn(assessmentResult);

        SubjectReport subjectReport = service.reportSubject(param);

        assertNotNull(subjectReport);
        assertNotNull(subjectReport.subject());
        assertEquals(subjectValue.getSubject().getId(), subjectReport.subject().id());
        assertEquals(subjectValue.getMaturityLevel().getId(), subjectReport.subject().maturityLevelId());
        assertEquals(assessmentResult.isValid(), subjectReport.subject().isCalculateValid());

        assertEquals(qaValues.size(), subjectReport.attributes().size());

        assertNotNull(subjectReport.topStrengths());
        assertEquals(3, subjectReport.topStrengths().size());

        assertNotNull(subjectReport.topWeaknesses());
        assertEquals(2, subjectReport.topWeaknesses().size());
    }
}
