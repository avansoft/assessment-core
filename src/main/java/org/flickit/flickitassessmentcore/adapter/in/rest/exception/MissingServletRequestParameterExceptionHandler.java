package org.flickit.flickitassessmentcore.adapter.in.rest.exception;

import org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorResponseDto;
import org.flickit.flickitassessmentcore.common.MessageBundle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.flickit.flickitassessmentcore.adapter.in.rest.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_LIST_SPACE_IDS_NOT_NULL;

@RestControllerAdvice
public class MissingServletRequestParameterExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {
        if (ex.getParameterName().equals("spaceId")) {
            String errorMessage = GET_ASSESSMENT_LIST_SPACE_IDS_NOT_NULL;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDto(INVALID_INPUT, MessageBundle.message(errorMessage)));
        }
        return super.handleMissingServletRequestParameter(ex, headers, status, request);
    }

}
