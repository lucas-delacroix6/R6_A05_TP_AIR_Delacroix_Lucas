package com.example.tp_air.exceptions;

import com.example.tp_air.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ApiErrorDTO> handleNotFound(NotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiErrorDTO(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage()));
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiErrorDTO> handleForbidden(ForbiddenException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ApiErrorDTO(String.valueOf(HttpStatus.FORBIDDEN.value()), ex.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiErrorDTO> handleAccessDenied(AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ApiErrorDTO(String.valueOf(HttpStatus.FORBIDDEN.value()), "Access Denied"));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiErrorDTO> handleBusiness(BusinessException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiErrorDTO(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
                String errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                                .collect(Collectors.joining(", "));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiErrorDTO(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                                "Validation error: " + errors));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiErrorDTO(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                                                "Internal Server Error: " + ex.getMessage()));
        }
}
