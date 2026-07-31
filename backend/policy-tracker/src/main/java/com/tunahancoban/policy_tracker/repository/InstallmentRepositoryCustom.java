package com.tunahancoban.policy_tracker.repository;


import java.util.List;
import java.util.Map;

public interface InstallmentRepositoryCustom {
    List<Map<String, Object>> getExpectedMonthlyIncome(int year);
    List<Map<String, Object>> getPaidMonthlyIncome(int year);
}