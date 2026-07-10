package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.response.ChartResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.DashboardSummaryResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import com.tunahancoban.policy_tracker.model.entity.Log;
import com.tunahancoban.policy_tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:9000")
@RestController
@RequestMapping("/rest/api/dashboard")
@RequiredArgsConstructor
public class RestDashboardController {

    private final DashboardService dashboardService;

    @GetMapping(path = "/get-summary")
    public ResponseEntity<RestResponse<DashboardSummaryResponse>> getSummary(){

        DashboardSummaryResponse dashboardSummaryResponse = dashboardService.getSummary();
        return ResponseEntity.ok(RestResponse.success("Summary returned successfully", dashboardSummaryResponse));

    }

    @GetMapping(path="/get-recent-activities/{n}")
    public ResponseEntity<RestResponse<List<Log>>> getRecentActivities(@PathVariable(name="n") int number) {
        List<Log> logList   = dashboardService.getRecentActivities(number);
        return ResponseEntity.ok(RestResponse.success("Logs are returned successfully", logList));
    }

    @GetMapping(path="/get-charts")
    public ResponseEntity<RestResponse<ChartResponse>> getCharts(){
        ChartResponse chartResponse = dashboardService.getCharts();
        return ResponseEntity.ok(RestResponse.success("Charts infos returned successfully", chartResponse ));
    }
    
}
