package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.CountKitStatsView;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaRepository;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.kittagrelation.KitTagRelationMapper;
import org.flickit.assessment.kit.adapter.out.persistence.kitversion.KitVersionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.users.user.UserMapper;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.*;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitExpertGroupPort,
    LoadKitUsersPort,
    DeleteKitUserAccessPort,
    LoadKitMinimalInfoPort,
    CreateAssessmentKitPort,
    UpdateKitLastMajorModificationTimePort,
    CountKitStatsPort,
    UpdateKitInfoPort,
    LoadAssessmentKitPort {

    private final AssessmentKitJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final ExpertGroupJpaRepository expertGroupRepository;
    private final KitVersionJpaRepository kitVersionRepository;
    private final KitTagRelationJpaRepository kitTagRelationRepository;

    @Override
    public ExpertGroup loadKitExpertGroup(Long kitId) {
        ExpertGroupJpaEntity entity = expertGroupRepository.findByKitId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));
        return new ExpertGroup(entity.getId(), entity.getTitle(), entity.getOwnerId());
    }

    @Override
    public PaginatedResponse<GetKitUserListUseCase.UserListItem> loadKitUsers(LoadKitUsersPort.Param param) {
        Page<UserJpaEntity> pageResult = repository.findAllKitUsers(
            param.kitId(),
            PageRequest.of(param.page(), param.size()));

        List<GetKitUserListUseCase.UserListItem> items = pageResult.getContent().stream()
            .map(UserMapper::mapToUserListItem)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public void delete(DeleteKitUserAccessPort.Param param) {
        AssessmentKitJpaEntity assessmentKit = repository.findById(param.kitId())
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_KIT_ID_NOT_FOUND));
        UserJpaEntity user = userRepository.findById(param.userId())
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_USER_NOT_FOUND));

        assessmentKit.getAccessGrantedUsers().remove(user);
        repository.save(assessmentKit);
    }

    @Override
    public GetKitMinimalInfoUseCase.Result loadKitMinimalInfo(Long kitId) {
        AssessmentKitJpaEntity kitEntity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_MINIMAL_INFO_KIT_ID_NOT_FOUND));

        ExpertGroupJpaEntity expertGroupEntity = expertGroupRepository.findById(kitEntity.getExpertGroupId())
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        return new GetKitMinimalInfoUseCase.Result(
                kitEntity.getId(),
                kitEntity.getTitle(),
                new GetKitMinimalInfoUseCase.MinimalExpertGroup(
                        expertGroupEntity.getId(),
                        expertGroupEntity.getTitle()
                )
        );
    }

    @Override
    public CreateAssessmentKitPort.Result persist(CreateAssessmentKitPort.Param param) {
        AssessmentKitJpaEntity kitEntity = AssessmentKitMapper.toJpaEntity(param, null);
        Long kitId = repository.save(kitEntity).getId();
        KitVersionJpaEntity kitVersionEntity = KitVersionMapper.toJpaEntity(kitEntity, KitVersionStatus.ACTIVE);
        Long savedKitVersionId = kitVersionRepository.save(kitVersionEntity).getId();
        repository.updateKitVersionId(kitId, savedKitVersionId);
        return new CreateAssessmentKitPort.Result(kitId, savedKitVersionId);
    }

    @Override
    public void updateLastMajorModificationTime(Long kitId, LocalDateTime lastMajorModificationTime) {
        repository.updateLastMajorModificationTime(kitId, lastMajorModificationTime);
    }

    @Override
    public CountKitStatsPort.Result countKitStats(long kitId) {
        CountKitStatsView kitStats = repository.countKitStats(kitId);
        return new CountKitStatsPort.Result(kitStats.getQuestionnaireCount(),
            kitStats.getAttributeCount(),
            kitStats.getQuestionCount(),
            kitStats.getMaturityLevelCount(),
            kitStats.getLikeCount(),
            kitStats.getAssessmentCount());
    }

    @Override
    public void update(UpdateKitInfoPort.Param param) {
        var kitEntity = repository.findById(param.kitId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_KIT_INFO_KIT_ID_NOT_FOUND));

        if (param.tags() != null)
            updateKitTags(param.kitId(), param.tags());

        var toBeUpdatedEntity = AssessmentKitMapper.toJpaEntity(kitEntity, param);
        repository.save(toBeUpdatedEntity);
    }

    private void updateKitTags(Long kitId, Set<Long> tags) {
        Set<Long> savedTags = kitTagRelationRepository.findAllByKitId(kitId).stream()
            .map(KitTagRelationJpaEntity::getTagId)
            .collect(Collectors.toSet());

        var removedTags = savedTags.stream()
            .filter(t -> !tags.contains(t))
            .collect(Collectors.toSet());

        var addedTags = tags.stream()
            .filter(t -> !savedTags.contains(t))
            .collect(Collectors.toSet());

        kitTagRelationRepository.saveAll(addedTags.stream()
            .map(tagId -> KitTagRelationMapper.toJpaEntity(tagId, kitId))
            .collect(Collectors.toSet()));

        kitTagRelationRepository.deleteByKitIdAndTagIdIn(kitId, removedTags);
    }

    @Override
    public AssessmentKit load(long kitId) {
        AssessmentKitJpaEntity kitEntity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        return AssessmentKitMapper.mapToDomainModel(kitEntity);
    }
}
