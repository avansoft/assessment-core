package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddEvidenceUseCaseParamTest {

    @Test
    void testAddEvidenceParam_DescriptionIsBlank_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("    ", 1L, assessmentId, 1L));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_NOT_BLANK);
    }

    @Test
    void testAddEvidenceParam_DescriptionIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("ab", 1L, assessmentId, 1L));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_SIZE_MIN);
    }

    @Test
    void testAddEvidenceParam_DescriptionSizeIsEqualToMin_Success() {
        assertDoesNotThrow(
            () -> new AddEvidenceUseCase.Param("abc", 1L, UUID.randomUUID(), 1L));
    }

    @Test
    void testAddEvidenceParam_DescriptionSizeIsGreaterThanMax_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var desc = randomAlphabetic(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param(desc, 1L, assessmentId, 1L));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_SIZE_MAX);
    }

    @Test
    void testAddEvidenceParam_DescriptionSizeIsEqualToMax_Success() {
        assertDoesNotThrow(
            () -> new AddEvidenceUseCase.Param(randomAlphabetic(1000), 1L, UUID.randomUUID(), 1L));
    }

    @Test
    void testAddEvidenceParam_CreatedByIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("desc", null, assessmentId, 1L));
        assertThat(throwable).hasMessage("createdById: " + ADD_EVIDENCE_CREATED_BY_ID_NOT_NULL);
    }

    @Test
    void testAddEvidenceParam_AssessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("desc", 1L, null, 1L));
        assertThat(throwable).hasMessage("assessmentId: " + ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testAddEvidenceParam_QuestionIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("desc", 1L, assessmentId, null)
        );
        assertThat(throwable).hasMessage("questionId: " + ADD_EVIDENCE_QUESTION_ID_NOT_NULL);
    }
}
