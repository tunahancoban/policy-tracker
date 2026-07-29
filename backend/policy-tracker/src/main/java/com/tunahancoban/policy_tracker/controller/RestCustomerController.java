package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import com.tunahancoban.policy_tracker.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "rest/api/customer")
@RequiredArgsConstructor
public class RestCustomerController {

    private final CustomerService customerService;

    @GetMapping(path = "/with-params")
    public ResponseEntity<RestResponse<Page<Customer>>> getCustomerWithParam(@RequestParam(name="customerId", required=false) String customerId,
                                                                             @RequestParam(name="firstName", required=false) String firstName,
                                                                             @RequestParam(name="lastName", required=false) String lastName,
                                                                             @RequestParam(name="identityNumber", required=false) String identityNumber,
                                                                             @RequestParam(name="email", required=false) String email,
                                                                             @RequestParam(name="phoneNumber", required=false) String phoneNumber,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerList = customerService.getCustomerByParam(customerId,firstName,lastName,identityNumber,email,phoneNumber,pageable);
        return ResponseEntity.ok(RestResponse.success("Müşteriler bulundu." , customerList));

    }
    @GetMapping(path ="/get-customer/{id}")
    public  ResponseEntity<RestResponse<Customer>> getCustomerById(@PathVariable(name="id") String id){
        Customer customer = customerService.getCustomerByCustomerId(id);
        return ResponseEntity.ok(RestResponse.success("Müşteri bulundu.", customer));
    }


    @PostMapping(path = "/create-customer")
    public ResponseEntity<RestResponse<Customer>> createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest){
            Customer customer = customerService.createCustomer(customerRequest);
            return ResponseEntity.ok(RestResponse.success("Başarılıyla oluşturuldu", customer));
    }

    @DeleteMapping(path="/delete-customer/{id}")
    public ResponseEntity<RestResponse<Void>> deleteCustomer(@PathVariable(name = "id") String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(RestResponse.success("Müşteri başarıyla silindi: " +id));
    }

    @PatchMapping(path = "/update-customer/{id}", consumes = "application/json")
    public ResponseEntity<RestResponse<Customer>> updateCustomer(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates) {
        Customer customer = customerService.updateCustomer(id,updates);
        return ResponseEntity.ok(RestResponse.success("Müşteri başarıyla güncellendi : "+id, customer));
    }


}
