package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Page<Customer> getCustomerByParam(String customerId, String fullName,
                                      String identityNumber, String email, String phoneNumber,
                                      Boolean active, Pageable pageable);
    Customer getCustomerByCustomerId(String customerId);
    Customer createCustomer(CreateCustomerRequest request);
    Customer updateCustomer(String id, UpdateCustomerRequest updates);
    void deleteCustomer(String id);
    boolean existById(String customerId);
}
