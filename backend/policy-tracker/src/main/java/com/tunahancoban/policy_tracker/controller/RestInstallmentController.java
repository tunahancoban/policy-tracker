package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import com.tunahancoban.policy_tracker.service.interfaces.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "rest/api/installment")
@RequiredArgsConstructor
public class RestInstallmentController {
    private final InstallmentService installmentService;

    @GetMapping(path="/with-params")
    public ResponseEntity<Page<Installment>> getInstallment(@RequestParam(name="customerId", required = false) String customerId,
                                                                          @RequestParam(name="policyId", required = false) String policyId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Installment> response = installmentService.getInstallment(customerId, policyId, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path="/update-installment/{id}")
    public ResponseEntity<Installment> updateInstallment(@PathVariable String id, @RequestParam(name = "status") PaymentStatus status){
        Installment installment = installmentService.updateInstallment(id, status);
        return ResponseEntity.ok(installment);
    }

    @DeleteMapping(path = "/delete-installment/{policyId}")
    public ResponseEntity<Void> deleteInstallment(@PathVariable String policyId){
        installmentService.deleteInstallment(policyId);
        return ResponseEntity.noContent().build();
    }
}
