package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.response.ChartResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.CustomerSummaryResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.DashboardSummaryResponse;
import com.tunahancoban.policy_tracker.model.entity.Log;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import com.tunahancoban.policy_tracker.repository.LogRepository;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final LogRepository logRepository;
    private final InstallmentRepository installmentRepository;

    public DashboardSummaryResponse getSummary(){
        //It returns the summary of policy and customer data.
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime end = today.plusDays(30); // 30 days later

        LocalDate today2 = LocalDate.now();

        long totalCustomer = customerRepository.count();
        long activePolicyNumber = policyRepository.countByStartDateLessThanEqualAndEndDateGreaterThanEqual(today2, today2);
        long expiringSoonPolicies = 0;//policyRepository.countByEndDateBetween(today, end);
        long expiredPolicies = policyRepository.countByEndDateLessThan(today2);

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

    public ChartResponse getCharts(int year){
        year = 2026;
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


        Map<String, Double> expectedMonthlyIncomeMap = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> expectedResults = installmentRepository.getExpectedMonthlyIncome(year);
            if (expectedResults != null) {
                for (Map<String, Object> row : expectedResults) {
                    Object yearObj = row.get("year");
                    Object monthObj = row.get("month");
                    if (yearObj != null && monthObj != null) {
                        String key = yearObj + "-" + String.format("%02d", ((Number) monthObj).intValue());
                        Object totalExpected = row.get("totalExpected");
                        double amount = totalExpected != null ? ((Number) totalExpected).doubleValue() : 0.0;
                        expectedMonthlyIncomeMap.put(key, amount);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Expected Monthly Income Map doldurulurken hata oluştu: " + e.getMessage());
        }


        Instant todayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant();

        Instant day7  = todayStart.atZone(ZoneOffset.UTC).plusDays(7).toInstant();
        Instant day15 = todayStart.atZone(ZoneOffset.UTC).plusDays(15).toInstant();
        Instant day30 = todayStart.atZone(ZoneOffset.UTC).plusDays(30).toInstant();

        long critical=0;
        long warning=0;
        long normal=0;

        try {
            critical = policyRepository.countByEndDateBetween(todayStart, day7);
            warning  = policyRepository.countByEndDateBetween(day7, day15);
            normal   = policyRepository.countByEndDateBetween(day15, day30);
             System.out.println(todayStart);
             System.out.println(warning);
             System.out.println(normal);

        } catch (Exception e) {
            System.err.println("Remaining Days Distribution hesaplanırken hata oluştu: " + e.getMessage());

        }

        return ChartResponse.builder()
                .typeLabels(typeLabelsMap)
                .monthlyPremium(expectedMonthlyIncomeMap)
                .numberOfWarningPolicies(warning)
                .numberOfNormalPolicies(normal)
                .numberOfCriticalPolicies(critical)
                .build();
    }
}
