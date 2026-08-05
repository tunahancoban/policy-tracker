package com.tunahancoban.policy_tracker.model.enums;

public enum PolicyType {
    TRAFIK("TRF"),
    KASKO("KSK"),
    DASK("DSK"),
    KONUT("KNT"),
    SAGLIK("SGL");

    private final String prefix;

    PolicyType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}