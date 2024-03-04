package org.flickit.assessment.core.adapter.out.persistence.kit.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.flickit.assessment.core.adapter.out.persistence.kit.subject.SubjectMapper.mapToDomainModel;

@Component("coreSubjectPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    LoadSubjectsPort,
    LoadSubjectPort {

    private final SubjectJpaRepository repository;

    @Override
    public List<Subject> loadByKitIdWithAttributes(Long kitId) {
        var views = repository.loadByKitIdWithAttributes(kitId);

        return views.stream().map(entity -> {
            List<QualityAttribute> attributes = entity.getAttributes().stream()
                .map(AttributeMapper::mapToDomainModel)
                .toList();

            return mapToDomainModel(entity, attributes);
        }).toList();
    }

    @Override
    public Optional<Subject> loadByIdAndKitId(long id, long kitId) {
        return repository.findByIdAndKitId(id, kitId)
            .map(entity -> mapToDomainModel(entity, null));
    }
}
