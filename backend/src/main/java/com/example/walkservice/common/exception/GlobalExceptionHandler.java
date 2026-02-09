package com.example.walkservice.common.exception;

import com.example.walkservice.common.response.ApiError;
import com.example.walkservice.common.response.ApiResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ApiError error = new ApiError(ex.getCode(), ex.getMessage(), List.of());
        return ResponseEntity.status(httpStatusFor(ex.getCode())).body(ApiResponse.failure(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toDetail)
                .toList();

        ApiError error = new ApiError("COMMON_VALIDATION_FAILED", "Validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(error));
    }

    private String toDetail(FieldError error) {
        String field = error.getField();
        String message = error.getDefaultMessage();
        return field + ": " + message;
    }

    private HttpStatus httpStatusFor(String code) {
        if (code != null && code.endsWith("_FORBIDDEN")) {
            return HttpStatus.FORBIDDEN;
        }
        if (code != null && code.endsWith("_NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
