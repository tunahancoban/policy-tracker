package com.tunahancoban.policy_tracker.model.DTO.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {

    private JsonNullable<String> firstName = JsonNullable.undefined();
    private JsonNullable<String> lastName = JsonNullable.undefined();
    private JsonNullable<String> identityNumber = JsonNullable.undefined();
    private JsonNullable<String> email = JsonNullable.undefined();
    private JsonNullable<String> phoneNumber = JsonNullable.undefined();
    private JsonNullable<String> city = JsonNullable.undefined();
    private JsonNullable<String> district = JsonNullable.undefined();
    private JsonNullable<String> fullAddress = JsonNullable.undefined();
    private JsonNullable<Boolean> isActive = JsonNullable.undefined();
}