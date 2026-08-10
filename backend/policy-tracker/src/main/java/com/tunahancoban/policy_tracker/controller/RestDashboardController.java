package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.response.ChartResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.CustomerSummaryResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.DashboardSummaryResponse;
import com.tunahancoban.policy_tracker.model.entity.Log;
import com.tunahancoban.policy_tracker.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/dashboard")
@RequiredArgsConstructor
public class RestDashboardController {

    private final DashboardService dashboardService;

    @GetMapping(path = "/get-summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(){
        DashboardSummaryResponse dashboardSummaryResponse = dashboardService.getSummary();
        return ResponseEntity.ok(dashboardSummaryResponse);

    }

    @GetMapping(path= "/get-summary/{id}")
    public ResponseEntity<CustomerSummaryResponse> getSummaryById(@PathVariable(name = "id") String id){
        System.out.println(id);
        CustomerSummaryResponse customerSummaryResponse = dashboardService.getSummaryById(id);
        return ResponseEntity.ok( customerSummaryResponse);

    }

    @GetMapping(path="/get-recent-activities/{n}")
    public ResponseEntity<List<Log>> getRecentActivities(@PathVariable(name="n") int number) {
        List<Log> logList   = dashboardService.getRecentActivities(number);
        return ResponseEntity.ok(logList);
    }

    @GetMapping(path="/get-charts/{year}")
    public ResponseEntity<ChartResponse> getCharts(@PathVariable(name="year") int year){
        ChartResponse chartResponse = dashboardService.getCharts(year);
        return ResponseEntity.ok( chartResponse );
    }
    
}
