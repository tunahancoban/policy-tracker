package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.customer.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.customer.CustomerSearchRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.customer.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.service.CustomerSearchService;
import com.tunahancoban.policy_tracker.service.interfaces.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "rest/api/customers")
@RequiredArgsConstructor
public class RestCustomerController {

    private final CustomerService customerService;
    private final CustomerSearchService searchService;

    @GetMapping
    public ResponseEntity<Page<Customer>> getCustomerWithParam(@ModelAttribute CustomerSearchRequest request) {

        Page<Customer> customerList = searchService.search(request);
        return ResponseEntity.ok(customerList);
    }

    @GetMapping(path ="/{id}")
    public  ResponseEntity<Customer> getCustomerById(@PathVariable String id){
        Customer customer = customerService.getCustomerByCustomerId(id);
        return ResponseEntity.ok(customer);
    }


    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest){
        Customer customer = customerService.createCustomer(customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        Customer customer = customerService.updateCustomer(id,updateCustomerRequest);
        return ResponseEntity.ok(customer);
    }


}
