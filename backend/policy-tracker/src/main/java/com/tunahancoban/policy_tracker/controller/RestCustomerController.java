package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.CustomerSearchRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.service.CustomerSearchService;
import com.tunahancoban.policy_tracker.service.interfaces.CustomerService;
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
@RequestMapping(path = "rest/api/customer")
@RequiredArgsConstructor
public class RestCustomerController {

    private final CustomerService customerService;
    private final CustomerSearchService searchService;

    @GetMapping(path = "/with-params")
    public ResponseEntity<Page<Customer>> getCustomerWithParam(CustomerSearchRequest request) {

        Page<Customer> customerList = searchService.search(request);
        return ResponseEntity.ok(customerList);
    }

    @GetMapping(path ="/get-customer/{id}")
    public  ResponseEntity<Customer> getCustomerById(@PathVariable String id){
        Customer customer = customerService.getCustomerByCustomerId(id);
        return ResponseEntity.ok(customer);
    }


    @PostMapping(path = "/create-customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest){
            Customer customer = customerService.createCustomer(customerRequest);
            return ResponseEntity.ok(customer);
    }

    @DeleteMapping(path="/delete-customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/update-customer/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        Customer customer = customerService.updateCustomer(id,updateCustomerRequest);
        return ResponseEntity.ok(customer);
    }


}
