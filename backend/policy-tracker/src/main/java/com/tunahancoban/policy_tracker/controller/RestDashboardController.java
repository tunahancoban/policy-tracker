package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.DashboardSummaryDTO;
import com.tunahancoban.policy_tracker.model.DTO.RecentActivityDTO;
import com.tunahancoban.policy_tracker.model.DTO.RestResponse;
import com.tunahancoban.policy_tracker.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:9000")
@RestController
@RequestMapping("/rest/api/dashboard")
@RequiredArgsConstructor
public class RestDashboardController {

    private final DashboardService dashboardService;

    @GetMapping(path = "/get-summary")
    public RestResponse<DashboardSummaryDTO> getSummary(){
        try {
            DashboardSummaryDTO dashboardSummaryDTO = dashboardService.getSummary();
            return RestResponse.success("Summary returned successfully", dashboardSummaryDTO);
        }catch (Exception e){
            return RestResponse.error(e.getMessage());
        }
    }

    @GetMapping(path="/get-recent-activities/{n}")
    public RestResponse<List<RecentActivityDTO>> getRecentActivities(@PathVariable(name="n") int number) {
        try {
            List<RecentActivityDTO> recentActivityDTO = dashboardService.getRecentActivities(number);
            return RestResponse.success("Recent activities returned successfully", recentActivityDTO);
        } catch (Exception e) {
            return RestResponse.error(e.getMessage());
        }
    }
    
}
