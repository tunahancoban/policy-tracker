package com.tunahancoban.policy_tracker.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------------------------------
    // 400 - @Valid ile işaretli @RequestBody DTO validasyon hataları
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String cleanErrorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        if (cleanErrorMessage.isBlank()) {
            cleanErrorMessage = "Validation error occurred";
        }

        System.out.println("Validation Error: " + cleanErrorMessage);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, cleanErrorMessage);
        problemDetail.setTitle("Validasyon Hatası");
        return problemDetail;
    }

    // ----------------------------------------------------------------
    // 400 - @RequestParam / @PathVariable seviyesindeki validasyon hataları
    // ----------------------------------------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
        String cleanErrorMessage = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Validation error occurred");

        System.out.println("Validation Error: " + cleanErrorMessage);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, cleanErrorMessage);
    }

    // ----------------------------------------------------------------
    // 400 - Geçersiz argüman
    // ----------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception) {
        System.out.println("Illegal Argument: " + exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // ----------------------------------------------------------------
    // 400 - Zorunlu query parametresi eksik
    // ----------------------------------------------------------------
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        String message = "Eksik parametre: " + exception.getParameterName();
        System.out.println(message);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }

    // ----------------------------------------------------------------
    // 400 - Parametre tipi uyuşmazlığı
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("'%s' parametresi için geçersiz değer: %s",
                exception.getName(), exception.getValue());
        System.out.println(message);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }

    // ----------------------------------------------------------------
    // 400 - Bozuk/okunamayan JSON body
    // ----------------------------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        System.out.println("Malformed JSON request: " + exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Geçersiz istek gövdesi (JSON formatı hatalı)");
    }

    // ----------------------------------------------------------------
    // 409 - Veritabanı bütünlük ihlali
    // ----------------------------------------------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        System.out.println("Data Integrity Violation: " + exception.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Bu işlem veri bütünlüğünü ihlal ediyor (örn. kayıt zaten mevcut veya ilişkili veri var)"
        );
    }

    // ----------------------------------------------------------------
    // 403 - Yetkisiz erişim
    // ----------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        System.out.println("Access Denied: " + exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz yok");
    }

    // ----------------------------------------------------------------
    // ResponseStatusException Yönetimi
    // ----------------------------------------------------------------
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException exception) {
        System.out.println(exception.getReason());
        return ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getReason());
    }

    // ----------------------------------------------------------------
    // 400 - Genel RuntimeException
    // ----------------------------------------------------------------
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException exception) {
        System.out.println(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // ----------------------------------------------------------------
    // 500 - Yakalanmamış her türlü diğer hata (son çare)
    // ----------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        System.out.println("Unexpected error: " + exception.getMessage());
        exception.printStackTrace();
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu");
    }
}