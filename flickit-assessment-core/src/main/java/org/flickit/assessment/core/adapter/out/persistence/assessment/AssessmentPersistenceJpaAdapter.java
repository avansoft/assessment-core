package org.flickit.assessment.core.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.port.out.assessment.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentKitSpaceJoinView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.ASSOCIATE;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    CreateAssessmentPort,
    LoadAssessmentListPort,
    UpdateAssessmentPort,
    GetAssessmentProgressPort,
    LoadAssessmentPort,
    DeleteAssessmentPort,
    CheckAssessmentSpaceMembershipPort,
    CountAssessmentsPort {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository resultRepository;
    private final AnswerJpaRepository answerRepository;
    private final QuestionJpaRepository questionRepository;
    private final AssessmentKitJpaRepository kitRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final KitCustomJpaRepository kitCustomRepository;

    @Override
    public UUID persist(CreateAssessmentPort.Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<AssessmentListItem> loadComparableAssessments(Long kitId, UUID userId, int page, int size) {
        var pageResult = repository.findComparableAssessments(kitId, userId, ASSOCIATE.getId(), PageRequest.of(page, size));

        Set<Long> kitVersionIds = pageResult.getContent().stream()
            .map(e -> e.getAssessmentKit().getKitVersionId())
            .collect(toSet());

        List<MaturityLevelJpaEntity> kitMaturityLevelEntities = maturityLevelRepository.findAllByKitVersionIdIn(kitVersionIds);
        Map<Long, List<MaturityLevelJpaEntity>> kitVersionIdToMaturityLevelEntities = kitMaturityLevelEntities.stream()
            .collect(Collectors.groupingBy(MaturityLevelJpaEntity::getKitVersionId));

        var assessmentMaturityLevelIds = pageResult.getContent().stream()
            .map(e -> new MaturityLevelJpaEntity.EntityId(e.getAssessmentResult().getMaturityLevelId(), e.getAssessmentKit().getKitVersionId()))
            .collect(toSet());

        var maturityLevelIdToMaturityLevel = kitMaturityLevelEntities.stream()
            .filter(e -> assessmentMaturityLevelIds.contains(new MaturityLevelJpaEntity.EntityId(e.getId(), e.getKitVersionId())))
            .collect(toMap(e -> new MaturityLevelJpaEntity.EntityId(e.getId(), e.getKitVersionId()), Function.identity()));

        List<AssessmentListItem> items = pageResult.getContent().stream()
            .map(e -> {
                AssessmentKitJpaEntity kitEntity = e.getAssessmentKit();
                List<MaturityLevelJpaEntity> kitLevelEntities = kitVersionIdToMaturityLevelEntities.get(kitEntity.getKitVersionId());
                AssessmentListItem.Kit kit = new AssessmentListItem.Kit(kitEntity.getId(), kitEntity.getTitle(), kitLevelEntities.size());
                SpaceJpaEntity spaceEntity = e.getSpace();
                AssessmentListItem.Space space = new AssessmentListItem.Space(spaceEntity.getId(), spaceEntity.getTitle());
                AssessmentListItem.MaturityLevel maturityLevel = null;
                if (Boolean.TRUE.equals(e.getAssessmentResult().getIsCalculateValid())) {
                    MaturityLevelJpaEntity maturityLevelEntity = maturityLevelIdToMaturityLevel
                        .get(new MaturityLevelJpaEntity.EntityId(e.getAssessmentResult().getMaturityLevelId(), e.getAssessmentKit().getKitVersionId()));
                    maturityLevel = new AssessmentListItem.MaturityLevel(maturityLevelEntity.getId(),
                        maturityLevelEntity.getTitle(),
                        maturityLevelEntity.getValue(),
                        maturityLevelEntity.getIndex());
                }

                return new AssessmentListItem(e.getAssessment().getId(),
                    e.getAssessment().getTitle(),
                    kit,
                    space,
                    e.getAssessment().getLastModificationTime(),
                    maturityLevel,
                    e.getAssessmentResult().getConfidenceValue(),
                    e.getAssessmentResult().getIsCalculateValid(),
                    e.getAssessmentResult().getIsConfidenceValid(),
                    null,
                    false);
            }).toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public PaginatedResponse<AssessmentListItem> loadSpaceAssessments(Long spaceId, UUID userId, int page, int size) {
        var pageResult = repository.findBySpaceId(spaceId, MANAGER.getId(), userId,
            PageRequest.of(page, size, Sort.Direction.DESC, AssessmentResultJpaEntity.Fields.lastModificationTime));

        Set<Long> kitVersionIds = pageResult.getContent().stream()
            .map(e -> e.getAssessmentResult().getKitVersionId())
            .collect(toSet());

        List<AssessmentKitJpaEntity> kitEntities = kitRepository.findByKitVersionIds(kitVersionIds);
        Map<Long, AssessmentKitJpaEntity> kitIdToKitEntity = kitEntities.stream()
            .collect(toMap(AssessmentKitJpaEntity::getId, Function.identity()));

        List<MaturityLevelJpaEntity> kitMaturityLevelEntities = maturityLevelRepository.findAllByKitVersionIdIn(kitVersionIds);
        Map<Long, List<MaturityLevelJpaEntity>> kitVersionIdToMaturityLevelEntities = kitMaturityLevelEntities.stream()
            .collect(Collectors.groupingBy(MaturityLevelJpaEntity::getKitVersionId));

        Map<MaturityLevelJpaEntity.EntityId, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevel =
            kitMaturityLevelEntities.stream()
                .collect(toMap(e -> new MaturityLevelJpaEntity.EntityId(e.getId(), e.getKitVersionId()), Function.identity()));

        List<AssessmentListItem> items = pageResult.getContent().stream()
            .map(e -> {
                AssessmentKitJpaEntity kitEntity = kitIdToKitEntity.get(e.getAssessment().getAssessmentKitId());
                List<MaturityLevelJpaEntity> kitLevelEntities = kitVersionIdToMaturityLevelEntities.get(e.getAssessmentResult().getKitVersionId());
                AssessmentListItem.Kit kit = new AssessmentListItem.Kit(kitEntity.getId(), kitEntity.getTitle(), kitLevelEntities.size());
                AssessmentListItem.Space space = null;
                AssessmentListItem.MaturityLevel maturityLevel = null;
                if (Boolean.TRUE.equals(e.getAssessmentResult().getIsCalculateValid())) {
                    MaturityLevelJpaEntity maturityLevelEntity = maturityLevelIdToMaturityLevel.get(
                        new MaturityLevelJpaEntity.EntityId(e.getAssessmentResult().getMaturityLevelId(), e.getAssessmentResult().getKitVersionId()));
                    maturityLevel = new AssessmentListItem.MaturityLevel(maturityLevelEntity.getId(),
                        maturityLevelEntity.getTitle(),
                        maturityLevelEntity.getValue(),
                        maturityLevelEntity.getIndex());
                }

                return new AssessmentListItem(e.getAssessment().getId(),
                    e.getAssessment().getTitle(),
                    kit,
                    space,
                    e.getAssessment().getLastModificationTime(),
                    maturityLevel,
                    e.getAssessmentResult().getConfidenceValue(),
                    e.getAssessmentResult().getIsCalculateValid(),
                    e.getAssessmentResult().getIsConfidenceValid(),
                    e.getManageable(),
                    e.getHasReport());
            }).toList();


        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public UpdateAssessmentPort.Result update(UpdateAssessmentPort.AllParam param) {
        if (!repository.existsByIdAndDeletedFalse(param.id()))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_ID_NOT_FOUND);

        repository.update(
            param.id(),
            param.title(),
            param.shortTitle(),
            param.code(),
            param.lastModificationTime(),
            param.lastModifiedBy());
        return new UpdateAssessmentPort.Result(param.id());
    }

    @Override
    public GetAssessmentProgressPort.Result getProgress(UUID assessmentId) {
        var assessmentResult = resultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_NOT_FOUND));

        int answersCount = answerRepository.getCountByAssessmentResultId(assessmentResult.getId());
        int questionsCount = questionRepository.countByKitVersionId(assessmentResult.getKitVersionId());
        return new GetAssessmentProgressPort.Result(assessmentId, answersCount, questionsCount);
    }

    @Override
    public Optional<Assessment> getAssessmentById(UUID assessmentId) {
        Optional<AssessmentKitSpaceJoinView> entity = repository.findByIdAndDeletedFalseWithKitAndSpace(assessmentId);
        if (entity.isEmpty())
            throw new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND);
        return entity.map(AssessmentMapper::mapToDomainModel);
    }

    @Override
    public void deleteById(UUID id, Long deletionTime) {
        if (!repository.existsByIdAndDeletedFalse(id))
            throw new ResourceNotFoundException(DELETE_ASSESSMENT_ID_NOT_FOUND);

        repository.delete(id, deletionTime);
    }

    @Override
    public void updateLastModificationTime(UUID id, LocalDateTime lastModificationTime) {
        repository.updateLastModificationTime(id, lastModificationTime);
    }

    @Override
    public void updateKitCustomId(UUID id, long kitCustomId) {
        AssessmentKitSpaceJoinView view = repository.findByIdAndDeletedFalseWithKitAndSpace(id)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        if (!kitCustomRepository.existsByIdAndKitId(kitCustomId, view.getKit().getId()))
            throw new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND);

        repository.updateKitCustomId(id, kitCustomId);
    }

    @Override
    public boolean isAssessmentSpaceMember(UUID assessmentId, UUID userId) {
        return repository.checkIsAssessmentSpaceMember(assessmentId, userId).isPresent();
    }

    @Override
    public int countSpaceAssessments(long spaceId) {
        return repository.countBySpaceIdAndDeletedFalse(spaceId);
    }
}
