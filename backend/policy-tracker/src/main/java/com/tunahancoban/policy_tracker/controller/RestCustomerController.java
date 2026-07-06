package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.DTO.RestResponse;
import com.tunahancoban.policy_tracker.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public RestResponse<List<Customer>> getCustomerWithParam(@RequestParam(name="customerId", required=false) String customerId,
                                                   @RequestParam(name="firstName", required=false) String firstName,
                                                   @RequestParam(name="lastName", required=false) String lastName,
                                                   @RequestParam(name="identityNumber", required=false) String identityNumber,
                                                   @RequestParam(name="email", required=false) String email,
                                                   @RequestParam(name="phoneNumber", required=false) String phoneNumber){
        try {
            List<Customer> customerList = customerService.getCustomerByParam(customerId,firstName,lastName,identityNumber,email,phoneNumber);
            return RestResponse.success("Customers are found" , customerList);
        }catch (Exception e) {
            return RestResponse.error(e.getMessage());
        }
    }


    @PostMapping(path = "/create-customer")
    public RestResponse<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest){

        try{
            Customer customer = customerService.createCustomer(customerRequest);
            return RestResponse.success("Başarılıyla oluşturuldu", customer);
        }catch (Exception e) {
            return RestResponse.error(e.getMessage());
        }

    }

    @DeleteMapping(path="/delete-customer/{id}")
    public RestResponse<Void> deleteCustomer(@PathVariable(name = "id") String id) {
        return null;
    }

    @PatchMapping(path = "/update-customer/{id}", consumes = "application/json")
    public RestResponse<Void> updateCustomer(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates) {
        return null;
    }


}
