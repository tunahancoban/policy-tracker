package com.tunahancoban.policy_tracker.services;

import com.tunahancoban.policy_tracker.model.entity.Activity;
import com.tunahancoban.policy_tracker.model.DTO.DashboardSummaryDTO;
import com.tunahancoban.policy_tracker.model.DTO.RecentActivityDTO;
import com.tunahancoban.policy_tracker.repository.ActivityRepository;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final ActivityRepository activityRepository;


    public DashboardSummaryDTO getSummary(){
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(30); // 30 days later

        long totalCustomer = customerRepository.count();
        long activePolicyNumber = policyRepository.countByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, end);
        long expiringSoonPolicies = policyRepository.countByEndDateBetween(today, end);
        long expiredPolicies = policyRepository.countByEndDateLessThan(today);

        return new DashboardSummaryDTO(totalCustomer, activePolicyNumber, expiringSoonPolicies, expiredPolicies);
    }

    
    public List<RecentActivityDTO> getRecentActivities(int n){
        //Last n activities
        List<Activity> activities = activityRepository.findByOrderByDateDesc(PageRequest.of(0, n));
         return activities.stream()
                .map(activity -> new RecentActivityDTO(
                        activity.getTitle(),
                        activity.getDescription(),
                        activity.getDate()
                ))
                .collect(Collectors.toList());
    }
}
