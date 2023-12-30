package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupListPort {

    private final ExpertGroupJpaRepository repository;

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }

    @Override
    public PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> loadExpertGroupList(LoadExpertGroupListPort.Param param) {
        // 3) Afterwards, I implemented it in the adapter, and I anticipate that the 'pageResult' will represent the results of the query.
// Note: I intentionally made a temporary field change. The 'pageResult' comprises a list with 'TupleConverters' objects, which is not readable.
// If I change the type of the returned method to 'Object[]' in the repository, the results will be displayed in 'pageResult,' but not with the correct class.

        var pageResult = repository.getExpertGroupSummaries(PageRequest.of(param.page(), param.size()));
        //List<GetExpertGroupListUseCase.ExpertGroupListItem> items = pageResult.getContent().stream()
            //.map(ExpertGroupMapper::mapToExpertGroupListItem)
            //.toList();

        return new PaginatedResponse<>(
            null,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
