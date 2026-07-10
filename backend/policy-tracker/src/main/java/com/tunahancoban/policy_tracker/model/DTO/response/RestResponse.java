package com.tunahancoban.policy_tracker.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse <T>{

    private boolean success;
    private String message;
    private T data;

    public static <T> RestResponse<T> success(String message, T data) {
        return new RestResponse<>(true, message, data);
    }

    public static <T> RestResponse<T> success(String message) {
        return new RestResponse<>(true, message, null);
    }

    public static <T> RestResponse<T> error(String message) {
        return new RestResponse<>(false, message, null);
    }

}
