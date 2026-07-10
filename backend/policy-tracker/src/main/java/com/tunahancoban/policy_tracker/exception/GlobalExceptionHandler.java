package com.tunahancoban.policy_tracker.exception;

import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<Void>> handleRuntimeException(RuntimeException exception){
        RestResponse<Void> response = RestResponse.error(exception.getMessage());
        System.out.println(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
