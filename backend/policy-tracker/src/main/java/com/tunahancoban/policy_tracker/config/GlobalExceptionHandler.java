package com.tunahancoban.policy_tracker.config;

import com.tunahancoban.policy_tracker.model.exceptions.BusinessValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------------------------------
    // 400 - @Valid ile işaretli @RequestBody DTO validasyon hataları
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Geçersiz değer",
                        (existing, replacement) -> existing // aynı field'da 2 hata varsa ilkini tut
                ));

        log.warn("Validation Error: {}", fieldErrors);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Doğrulama hatası");
        problemDetail.setTitle("Validasyon Hatası");
        problemDetail.setProperty("errors", fieldErrors);
        return problemDetail;
    }

    // ----------------------------------------------------------------
    // 409 / 400 - Alan Bazlı İş Kuralı ve Çakışma Hataları
    // ----------------------------------------------------------------
    @ExceptionHandler(BusinessValidationException.class)
    public ProblemDetail handleBusinessValidationException(BusinessValidationException exception) {
        log.warn("Business Validation Error on field '{}': {}", exception.getFieldName(), exception.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                exception.getStatus(),
                exception.getMessage()
        );
        problemDetail.setTitle("İş Kuralı Doğrulama Hatası");

        problemDetail.setProperty("errors", Map.of(exception.getFieldName(), exception.getMessage()));

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

        log.warn("Constraint Violation Error: {}", cleanErrorMessage);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, cleanErrorMessage);
    }

    // ----------------------------------------------------------------
    // 400 - Geçersiz argüman
    // ----------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Illegal Argument: {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // ----------------------------------------------------------------
    // 400 - Zorunlu query parametresi eksik
    // ----------------------------------------------------------------
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        String message = "Eksik parametre: " + exception.getParameterName();
        log.warn(message);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }

    // ----------------------------------------------------------------
    // 400 - Parametre tipi uyuşmazlığı
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("'%s' parametresi için geçersiz değer: %s",
                exception.getName(), exception.getValue());
        log.warn(message);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }

    // ----------------------------------------------------------------
    // 400 - Bozuk/okunamayan JSON body
    // ----------------------------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.warn("Malformed JSON request: {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Geçersiz istek gövdesi (JSON formatı hatalı)");
    }

    // ----------------------------------------------------------------
    // 409 - Veritabanı bütünlük ihlali
    // ----------------------------------------------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        log.error("Data Integrity Violation: {}", exception.getMessage(), exception);
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
        log.warn("Access Denied: {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz yok");
    }

    // ----------------------------------------------------------------
    // ResponseStatusException Yönetimi
    // ----------------------------------------------------------------
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException exception) {
        log.warn("Response Status Exception [{}]: {}", exception.getStatusCode(), exception.getReason());
        return ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getReason());
    }

    // ----------------------------------------------------------------
    // 400 - Genel RuntimeException
    // ----------------------------------------------------------------
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException exception) {
        log.error("Unhandled RuntimeException: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // ----------------------------------------------------------------
    // 500 - Yakalanmamış her türlü diğer hata (son çare)
    // ----------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        log.error("Unexpected error occurred: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu");
    }
}