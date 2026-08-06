package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.DTO.response.ChartResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.CustomerSummaryResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.DashboardSummaryResponse;
import com.tunahancoban.policy_tracker.model.entity.Log;

import java.util.List;

public interface DashboardServiceImp {
    DashboardSummaryResponse getSummary();
    CustomerSummaryResponse getSummaryById(String customerId);
    List<Log> getRecentActivities(int n);
    ChartResponse getCharts(int year);

}
