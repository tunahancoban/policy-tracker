package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.PolicySearchRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.RenewPolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.service.PolicySearchService;
import com.tunahancoban.policy_tracker.service.interfaces.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path = "rest/api/policy")
@RequiredArgsConstructor
public class RestPolicyController {
    private final PolicyService policyService;
    private final PolicySearchService policySearchService;

    @GetMapping(path = "/with-params")
    public Page<Policy> getPolicyWithParams(PolicySearchRequest request) {
        return policySearchService.search(request);
    }
    
    @GetMapping(path = "/get-policy/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable String id){
        Policy policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    @PostMapping(path = "/create-policy")
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody CreatePolicyRequest policyRequest){

        Policy policy = policyService.createPolicy(policyRequest);
        return ResponseEntity.ok(policy);

    }

    @PostMapping(path = "/renew-policy")
    public ResponseEntity<Policy> renewPolicy(@Valid @RequestBody RenewPolicyRequest policyRequest){
        Policy policy = policyService.renewPolicy(policyRequest);
        return ResponseEntity.ok(policy);

    }

    //3. DELETE policy
    @DeleteMapping(path="/delete-policy/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    // 4. UPDATE policy
    @PatchMapping(path = "/update-policy/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable String id, @RequestBody UpdatePolicyRequest updatePolicyRequest) {
        Policy policy = policyService.updatePolicy(id, updatePolicyRequest);
        return ResponseEntity.ok(policy);
    }
}
