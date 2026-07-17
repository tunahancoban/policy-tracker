package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "rest/api/policy")
@RequiredArgsConstructor
public class RestPolicyController {
    private final PolicyService policyService;

    @GetMapping(path = "/with-params")
    public ResponseEntity<RestResponse<List<Policy>>> getPolicyWithParams(@RequestParam(name = "policyId", required = false) String policyId,
                                                                          @RequestParam(name = "customerId", required = false) String customerId,
                                                                          @RequestParam(name = "type", required = false) PolicyType type){
        List<Policy> policyList = policyService.getPolicyWithParams(customerId,policyId,type);
        return ResponseEntity.ok(RestResponse.success("Poliçeler bulundu ", policyList));
    }

    @PostMapping(path = "/create-policy")
    public ResponseEntity<RestResponse<Policy>> createPolicy(@Valid @RequestBody CreatePolicyRequest policyRequest){

        Policy policy = policyService.createPolicy(policyRequest);
        return ResponseEntity.ok(RestResponse.success("Poliçe başarıyla oluşturuldu ", policy));

    }

    //3. DELETE policy
    @DeleteMapping(path="/delete-policy/{id}")
    public ResponseEntity<RestResponse<Void>> deleteUser(@PathVariable(name = "id") String id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok(RestResponse.success("Poliçe başarıyla silindi: "+ id));
    }

    // 4. UPDATE policy
    @PatchMapping(path = "/update-policy/{id}", consumes = "application/json")
    public ResponseEntity<RestResponse<Void>> updateUser(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates) {
        policyService.updatePolicy(id, updates);
        return ResponseEntity.ok(RestResponse.success("Poliçe başarıyla güncellendi: "+ id));
    }

}
