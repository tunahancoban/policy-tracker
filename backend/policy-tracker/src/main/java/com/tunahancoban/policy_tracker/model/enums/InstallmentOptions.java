package com.tunahancoban.policy_tracker.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InstallmentOptions {
    SINGLE(1),
    THREE(3),
    SIX(6);
    private final int value;
    InstallmentOptions(int value) { this.value = value; }
    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static InstallmentOptions fromValue(int value) {
        for (InstallmentOptions option : InstallmentOptions.values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        throw new IllegalArgumentException("Geçersiz taksit sayısı: " + value + ". İzin verilenler: 1, 3, 6");
    }
}
