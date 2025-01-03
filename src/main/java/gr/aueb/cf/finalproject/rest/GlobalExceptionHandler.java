package gr.aueb.cf.finalproject.rest;

import gr.aueb.cf.finalproject.core.exceptions.AppGenericException;
import gr.aueb.cf.finalproject.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.finalproject.core.exceptions.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppGenericException.class)
    public ResponseEntity<Map<String, Object>> handleAppGenericException(AppGenericException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ex.getCode());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ex.getCode());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("details", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
