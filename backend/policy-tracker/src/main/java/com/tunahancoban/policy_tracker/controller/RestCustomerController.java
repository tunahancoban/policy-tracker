package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
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

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "firstName",
            "lastName",
            "email",
            "phoneNumber",
            "city",
            "customerId"
    );

    @GetMapping(path = "/with-params")
    public ResponseEntity<Page<Customer>> getCustomerWithParam(
            @RequestParam(name = "customerId", required = false) String customerId,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "identityNumber", required = false) String identityNumber,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "active", required = false) Boolean active,
            @PageableDefault(size = 5, sort = "customerId", direction = Sort.Direction.ASC)
            Pageable pageable) {

        validateSort(pageable);

        Page<Customer> customerList = customerService.getCustomerByParam(
                customerId, firstName, lastName, identityNumber, email, phoneNumber, active, pageable);

        return ResponseEntity.ok(customerList);
    }

    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException("Geçersiz sıralama alanı: " + order.getProperty());
            }
        });
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
