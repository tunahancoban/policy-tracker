package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.policy.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.PolicySearchRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.RenewPolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.service.PolicySearchService;
import com.tunahancoban.policy_tracker.service.interfaces.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "rest/api/policies")
@RequiredArgsConstructor
public class RestPolicyController {
    private final PolicyService policyService;
    private final PolicySearchService policySearchService;

    @GetMapping
    public Page<Policy> getPolicyWithParams(PolicySearchRequest request) {
        return policySearchService.search(request);
    }
    
    @GetMapping(path = "/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable String id){
        Policy policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    @PostMapping
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody CreatePolicyRequest policyRequest){

        Policy policy = policyService.createPolicy(policyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(policy);

    }

    @PostMapping(path = "/renew")
    public ResponseEntity<Policy> renewPolicy(@Valid @RequestBody RenewPolicyRequest policyRequest){
        Policy policy = policyService.renewPolicy(policyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(policy);
    }

    //3. DELETE policy
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    // 4. UPDATE policy
    @PatchMapping(path = "/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable String id, @RequestBody UpdatePolicyRequest updatePolicyRequest) {
        Policy policy = policyService.updatePolicy(id, updatePolicyRequest);
        return ResponseEntity.ok(policy);
    }
}
