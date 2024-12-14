package com.DTEC.Document_Tracking_and_E_Clearance.global_handler_exception;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.*;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException extends RuntimeException {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public GlobalHandlerException(DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> badRequestException(
            BadRequestException exception,
            WebRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> conflictException(
            ConflictException exception,
            WebRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> forbiddenException(
            ForbiddenException exception,
            WebRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ApiResponse<Void>> internalServerErrorException(
            InternalServerErrorException exception,
            WebRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> resourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> unauthorizedException(
            UnauthorizedException exception,
            WebRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException exception,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        exception.getMessage(),
                        null,
                        request.getDescription(false),
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }
}
