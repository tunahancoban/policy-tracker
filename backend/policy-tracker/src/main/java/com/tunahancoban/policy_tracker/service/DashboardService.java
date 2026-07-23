package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.response.ChartResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.CustomerSummaryResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.DashboardSummaryResponse;
import com.tunahancoban.policy_tracker.model.entity.Log;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import com.tunahancoban.policy_tracker.repository.LogRepository;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final LogRepository logRepository;

    public DashboardSummaryResponse getSummary(){
        //It returns the summary of policy and customer data.
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(30); // 30 days later

        long totalCustomer = customerRepository.count();
        long activePolicyNumber = policyRepository.countByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
        long expiringSoonPolicies = policyRepository.countByEndDateBetween(today, end);
        long expiredPolicies = policyRepository.countByEndDateLessThan(today);

        return new DashboardSummaryResponse(totalCustomer, activePolicyNumber, expiringSoonPolicies, expiredPolicies);
    }

    public CustomerSummaryResponse getSummaryById(String customerId){
        //It returns the summary of policy and customer data by id.
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(30); // 30 days later

        List<Map<String, Object>> premium = policyRepository.sumPremiumByCustomerId(customerId);
        long totalPremium = premium.isEmpty() ? 0L : ((Number) premium.get(0).get("totalPremium")).longValue();
        long activePolicyNumber = policyRepository.countByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(today, today, customerId);
        long expiringSoonPolicies = policyRepository.countByEndDateBetweenAndCustomerId(today, end, customerId);
        long expiredPolicies = policyRepository.countByEndDateLessThanAndCustomerId(today, customerId);

        return new CustomerSummaryResponse(totalPremium, activePolicyNumber, expiringSoonPolicies, expiredPolicies);
    }


    
    public List<Log> getRecentActivities(int n){
        //Last n activities
        PageRequest pageRequest = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "dateTime"));

        return logRepository.findAll(pageRequest).getContent();
    }

    public ChartResponse getCharts(){
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        Map<String, Long> typeLabelsMap = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> typeResults = policyRepository.countPoliciesGroupedByType();
            if (typeResults != null) {
                for (Map<String, Object> row : typeResults) {
                    if (row.get("_id") != null) {
                        String id = String.valueOf(row.get("_id"));
                        Object totalCount = row.get("totalCount");
                        long count = totalCount != null ? ((Number) totalCount).longValue() : 0L;
                        typeLabelsMap.put(id, count);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Type Labels Map doldurulurken hata oluştu: " + e.getMessage());
        }

        Map<String, Double> monthlyPremiumMap = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> premiumResults = policyRepository.findMonthlyPremiumEarnings(sixMonthsAgo);
            if (premiumResults != null) {
                for (Map<String, Object> row : premiumResults) {
                    if (row.get("_id") != null) {
                        String id = String.valueOf(row.get("_id"));
                        Object totalPremium = row.get("totalPremium");
                        double premium = totalPremium != null ? ((Number) totalPremium).doubleValue() : 0.0;
                        monthlyPremiumMap.put(id, premium);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Monthly Premium Map doldurulurken hata oluştu: " + e.getMessage());
        }

        return ChartResponse.builder()
                .typeLabels(typeLabelsMap)
                .monthlyPremium(monthlyPremiumMap)
                .build();
    }
}
