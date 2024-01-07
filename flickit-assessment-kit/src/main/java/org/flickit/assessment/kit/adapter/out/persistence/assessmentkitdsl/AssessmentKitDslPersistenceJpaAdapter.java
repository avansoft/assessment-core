package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitDslPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentKitDslPersistenceJpaAdapter implements CreateAssessmentKitDslPort {

    private final AssessmentKitDslJpaRepository repository;

    @Override
    public Long create(Param param) {
        //Save json file
        repository.save(AssessmentKitDslMapper.toJpaEntity(param.jsonFilePath()));
        //Save zip file
        return repository.save(AssessmentKitDslMapper.toJpaEntity(param.zipFilePath())).getId();
    }
}
