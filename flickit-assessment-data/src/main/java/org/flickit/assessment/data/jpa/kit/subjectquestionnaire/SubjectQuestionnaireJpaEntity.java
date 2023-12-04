package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "baseinfo_assessmentsubject_questionnaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubjectQuestionnaireJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "assessmentsubject_id", nullable = false)
    private Long subjectId;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;
}
