package com.tunahancoban.policy_tracker.config;

import com.tunahancoban.policy_tracker.model.exceptions.BusinessValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
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

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // 1) Normal alan hataları (@NotNull, @Size, @Positive vb.)
        for (FieldError fe : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Geçersiz değer");
        }

        // 2) Global hatalar (@AssertTrue metotları)
        //    codes[0] örneği: "AssertTrue.createHealthPolicyRequest.endDate"
        //    veya codes dizisinin son elemanı metot adıdır: "isEndDate" -> "endDate"
        for (org.springframework.validation.ObjectError ge : exception.getBindingResult().getGlobalErrors()) {
            String fieldName = resolveAssertTrueFieldName(ge);
            fieldErrors.putIfAbsent(fieldName, ge.getDefaultMessage() != null ? ge.getDefaultMessage() : "Kural ihlali");
        }

        log.warn("Validation Error: {}", fieldErrors);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Doğrulama hatası");
        problemDetail.setTitle("Validasyon Hatası");
        problemDetail.setProperty("errors", fieldErrors);
        return problemDetail;
    }

    /**
     * @AssertTrue metot adından frontend ile eşleşen alan adını çıkarır.
     * Örnek: codes = ["AssertTrue.createHealthPolicyRequest.endDate", "AssertTrue.endDate", "endDate"]
     *   -> Son parça: "endDate"
     * Eğer codes yoksa veya parse edilemezse, objectName döner (fallback).
     */
    private String resolveAssertTrueFieldName(org.springframework.validation.ObjectError ge) {
        if (ge.getCodes() != null && ge.getCodes().length > 0) {
            // codes dizisinin son elemanı genellikle saf metot adıdır
            // Örn: ["AssertTrue.createPolicyRequest.endDate", "AssertTrue.endDate", "endDate"]
            // Son eleman "endDate" direkt kullanılabilir.
            // Ancak bazı durumlarda son eleman "isEndDate" olabilir (constraint adı)
            // İlk codes elemanından parçalayalım: codes[0].split(".") -> son parça
            String firstCode = ge.getCodes()[0];
            String[] parts = firstCode.split("\\.");
            String rawName = parts[parts.length - 1];

            // "is" prefix'ini kaldır ve ilk harfi küçült: "isEndDate" -> "endDate"
            return stripIsPrefix(rawName);
        }
        return ge.getObjectName();
    }

    /**
     * @AssertTrue metot adından "is" prefix'ini kaldırır.
     * "isEndDate"       -> "endDate"
     * "isBirthDate"     -> "birthDate"
     * "endDate"         -> "endDate" (prefix yoksa dokunmaz)
     */
    private String stripIsPrefix(String methodName) {
        if (methodName.length() > 2
                && methodName.startsWith("is")
                && Character.isUpperCase(methodName.charAt(2))) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return methodName;
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
        Map<String, String> errorsMap = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String fieldName = extractFieldName(violation);
            errorsMap.put(fieldName, violation.getMessage());
        }

        log.warn("Constraint Violation Error: {}", errorsMap);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Doğrulama hatası");
        problemDetail.setTitle("Validasyon Hatası");
        problemDetail.setProperty("errors", errorsMap);
        return problemDetail;
    }

    /**
     * ConstraintViolation propertyPath üzerinden alan adını çıkarır.
     *
     * Örnekler:
     *   - Normal alan: propertyPath = "birthDate"  -> "birthDate"
     *   - @AssertTrue metot: propertyPath = "maternityCoverage" (bean-property adı)
     *     veya bazen "isMaternityCoverage" -> stripIsPrefix -> "maternityCoverage"
     *
     * propertyPath boşsa, constraint annotation adını fallback olarak kullanır.
     */
    private String extractFieldName(ConstraintViolation<?> violation) {
        Path propertyPath = violation.getPropertyPath();
        String lastNode = null;

        for (Path.Node node : propertyPath) {
            lastNode = node.getName();
        }

        if (lastNode != null && !lastNode.isBlank()) {
            return stripIsPrefix(lastNode);
        }

        // Fallback: constraint annotation adı
        return violation.getConstraintDescriptor()
                .getAnnotation().annotationType().getSimpleName();
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