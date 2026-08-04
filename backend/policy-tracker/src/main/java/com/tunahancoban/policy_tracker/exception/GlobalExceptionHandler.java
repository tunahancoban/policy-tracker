package com.tunahancoban.policy_tracker.exception;

import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------------------------------
    // 404 - Kaynak bulunamadı (örn. findById().orElseThrow ile fırlatılan)
    // ----------------------------------------------------------------
    /*@ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleEntityNotFoundException(EntityNotFoundException exception) {
        System.out.println("Not Found: " + exception.getMessage());
        RestResponse<Void> response = RestResponse.error(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }*/

    // ----------------------------------------------------------------
    // 400 - @Valid ile işaretli @RequestBody DTO'larındaki validasyon hataları
    // (ConstraintViolationException genelde @RequestParam/@PathVariable
    //  seviyesindeki validasyonu yakalar; body validasyonu bu farklı exception'la gelir)
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String cleanErrorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        if (cleanErrorMessage.isBlank()) {
            cleanErrorMessage = "Validation error occurred";
        }

        System.out.println("Validation Error: " + cleanErrorMessage);
        RestResponse<Void> response = RestResponse.error(cleanErrorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 400 - @RequestParam / @PathVariable seviyesindeki validasyon hataları
    // ----------------------------------------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestResponse<Void>> handleConstraintViolationException(ConstraintViolationException exception) {
        String cleanErrorMessage = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Validation error occurred");

        System.out.println("Validation Error: " + cleanErrorMessage);
        RestResponse<Void> response = RestResponse.error(cleanErrorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 400 - Geçersiz argüman (örn. sort whitelist kontrolünde attığımız)
    // ----------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        System.out.println("Illegal Argument: " + exception.getMessage());
        RestResponse<Void> response = RestResponse.error(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 400 - Zorunlu query parametresi eksik
    // (örn. @RequestParam(required = true) olan bir alan hiç gönderilmezse)
    // ----------------------------------------------------------------
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestResponse<Void>> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        String message = "Eksik parametre: " + exception.getParameterName();
        System.out.println(message);
        RestResponse<Void> response = RestResponse.error(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 400 - Parametre tipi uyuşmazlığı (örn. sayısal alana metin gönderilmesi)
    // ----------------------------------------------------------------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("'%s' parametresi için geçersiz değer: %s",
                exception.getName(), exception.getValue());
        System.out.println(message);
        RestResponse<Void> response = RestResponse.error(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 400 - Bozuk/okunamayan JSON body
    // ----------------------------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        System.out.println("Malformed JSON request: " + exception.getMessage());
        RestResponse<Void> response = RestResponse.error("Geçersiz istek gövdesi (JSON formatı hatalı)");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 409 - Veritabanı bütünlük ihlali (örn. unique constraint, foreign key)
    // ----------------------------------------------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        System.out.println("Data Integrity Violation: " + exception.getMessage());
        RestResponse<Void> response = RestResponse.error("Bu işlem veri bütünlüğünü ihlal ediyor (örn. kayıt zaten mevcut veya ilişkili veri var)");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // ----------------------------------------------------------------
    // 403 - Yetkisiz erişim (Spring Security kullanıyorsanız)
    // ----------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        System.out.println("Access Denied: " + exception.getMessage());
        RestResponse<Void> response = RestResponse.error("Bu işlem için yetkiniz yok");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // ----------------------------------------------------------------
    // 400 - Genel RuntimeException (mevcut yakalanmamış özel exception'lar için güvenlik ağı)
    // ----------------------------------------------------------------
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<Void>> handleRuntimeException(RuntimeException exception) {
        System.out.println(exception.getMessage());
        RestResponse<Void> response = RestResponse.error(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------------------
    // 500 - Yakalanmamış her türlü diğer hata (son çare)
    // ----------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Void>> handleGenericException(Exception exception) {
        System.out.println("Unexpected error: " + exception.getMessage());
        exception.printStackTrace();
        RestResponse<Void> response = RestResponse.error("Beklenmeyen bir hata oluştu");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestResponse<Void>> handleResponseStatusException(ResponseStatusException exception) {
        System.out.println(exception.getReason());
        RestResponse<Void> response = RestResponse.error(exception.getReason());
        return new ResponseEntity<>(response, (HttpStatus) exception.getStatusCode());
    }
}