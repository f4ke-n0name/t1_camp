package com.f4ken0name.github.error.handlers;

import com.f4ken0name.github.audit.publishers.AuditPublisher;
import com.f4ken0name.github.command.exceptions.*;
import com.f4ken0name.github.error.dto.ErrorResponse;
import com.f4ken0name.github.error.exceptions.AndroidNotAvailableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Autowired
    @Qualifier("consoleAuditPublisher")
    private final AuditPublisher auditPublisher;

    @ExceptionHandler(AndroidNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleAndroidNotAvailable(
            AndroidNotAvailableException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(CommandQueueOverflowException.class)
    public ResponseEntity<ErrorResponse> handleQueueOverflow(
            CommandQueueOverflowException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.TOO_MANY_REQUESTS, request);
    }

    @ExceptionHandler(CommandRejectedException.class)
    public ResponseEntity<ErrorResponse> handleRejected(
            CommandRejectedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(CommandValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            CommandValidationException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        auditPublisher.publish(String.format(
                "[ERROR] %s %s — %s (%s)",
                status.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        ));

        return new ResponseEntity<>(errorResponse, status);
    }
}
