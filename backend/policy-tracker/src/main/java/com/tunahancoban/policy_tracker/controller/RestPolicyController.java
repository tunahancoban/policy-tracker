package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.RestResponse;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.services.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "rest/api/policy")
@RequiredArgsConstructor
public class RestPolicyController {
    private final PolicyService policyService;

    @GetMapping(path = "/with-params")
    public RestResponse<List<Policy>> getPolicyWithParams(@RequestParam(name = "policyId", required = false) String policyId,
                                              @RequestParam(name = "customerId", required = false) String customerId,
                                              @RequestParam(name = "type", required = false) PolicyType type){
        try {
            List<Policy> policyList = policyService.getPolicyWithParams(customerId,policyId,type);
            return RestResponse.success("Poliçeler bulundu ", policyList);
        }catch (Exception e) {
            return RestResponse.error(e.getMessage());
        }
    }

    @PostMapping(path = "/create-policy")
    public RestResponse<Policy> createPolicy(@Valid @RequestBody CreatePolicyRequest policyRequest){
        try {
            Policy policy = policyService.createPolicy(policyRequest);
            return RestResponse.success("Poliçe başarıyla oluşturuldu ", policy);
        }catch (Exception e) {
            return RestResponse.error(e.getMessage());
        }
    }

    @DeleteMapping(path="/delete-policy/{id}")
    public RestResponse<Void> deleteUser(@PathVariable(name = "id") String id) {
        return null;
    }

    // 4. UPDATE User
    @PatchMapping(path = "/update-policy/{id}", consumes = "application/json")
    public RestResponse<Void> updateUser(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates) {
        return null;
    }

}
