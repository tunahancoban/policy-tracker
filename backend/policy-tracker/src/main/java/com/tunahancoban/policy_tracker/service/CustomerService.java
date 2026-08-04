package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final IdGeneratorService idGeneratorService;

    public Page<Customer> getCustomerByParam(String customerId, String firstName, String lastName,
                                             String identityNumber, String email, String phoneNumber,
                                             Boolean active, Pageable pageable) {
        log.debug("Customer search initiated - customerId: {}, firstName: {}, lastName: {}, active: {}, page: {}",
                customerId, firstName, lastName, active, pageable);

        Customer searchCriteria = Customer.builder()
                .customerId(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .identityNumber(identityNumber)
                .email(email)
                .phoneNumber(phoneNumber)
                .active(active).build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withIgnorePaths("createdAt", "updatedAt")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Customer> example = Example.of(searchCriteria, matcher);

        Page<Customer> result = customerRepository.findAll(example, pageable);
        log.debug("Customer search completed - total records found: {}", result.getTotalElements());

        return result;
    }

    public Customer getCustomerByCustomerId(String customerId) {
        log.debug("Fetching customer - customerId: {}", customerId);

        Customer customer = customerRepository.findByCustomerId(customerId);
        if (customer == null) {
            log.warn("Customer not found - customerId: {}", customerId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + customerId);
        }
        return customer;
    }

    @LogActivity( type = "MUSTERI", detail = "Yeni müşteri eklendi.")
    public Customer createCustomer(CreateCustomerRequest request) {
        log.info("Create customer request received - identityNumber: {}, email: {}",
                request.getIdentityNumber(), request.getEmail());

        if (customerRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            log.warn("Customer creation failed - identity number already registered: {}", request.getIdentityNumber());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A customer with this identity number already exists: " + request.getIdentityNumber());
        }

        Customer customer = Customer.builder()
                .customerId(idGeneratorService.generateCustomerId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .identityNumber(request.getIdentityNumber())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .city(request.getCity())
                .district(request.getDistrict())
                .fullAddress(request.getFullAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true).build();

        customerRepository.save(customer);
        log.info("Customer successfully created - customerId: {}", customer.getCustomerId());

        return customer;
    }

    @LogActivity(type = "MUSTERI", detail = " Müşteri güncellendi.")
    public Customer updateCustomer(String id, Map<String, Object> updates) {
        log.info("Update customer request received - customerId: {}, updated fields: {}", id, updates.keySet());

        if (!existById(id)) {
            log.warn("Customer update failed - customer not found: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id);
        }
        Customer customer = customerRepository.findByCustomerId(id);
        Customer.CustomerBuilder customerBuilder = customer.toBuilder();

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    customerBuilder.firstName((String) value);
                    break;
                case "lastName":
                    customerBuilder.lastName((String) value);
                    break;
                case "identityNumber":
                    customerBuilder.identityNumber((String) value);
                    break;
                case "email":
                    customerBuilder.email((String) value);
                    break;
                case "phoneNumber":
                    customerBuilder.phoneNumber((String) value);
                    break;
                case "city":
                    customerBuilder.city((String) value);
                    break;
                case "district":
                    customerBuilder.district((String) value);
                    break;
                case "fullAddress":
                    customerBuilder.fullAddress((String) value);
                    break;
                case "active":
                    customerBuilder.active((boolean) value);
                    break;
                default:
                    log.warn("Unknown update field ignored - field: {}", key);
                    break;
            }
        });

        customerBuilder.updatedAt(LocalDateTime.now());
        Customer saveCustomer = customerBuilder.build();
        customerRepository.save(saveCustomer);
        log.info("Customer successfully updated - customerId: {}", id);

        return saveCustomer;
    }

    @LogActivity(type = "MUSTERI", detail = " Müşteri silindi.")
    public void deleteCustomer(String id) {
        log.info("Delete customer request received - customerId: {}", id);

        if (!existById(id)) {
            log.warn("Customer deletion failed - customer not found: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id);
        }
        Customer customer = customerRepository.findByCustomerId(id);
        customerRepository.deleteById(customer.getId());
        log.info("Customer successfully deleted - customerId: {}", id);
    }

    public boolean existById(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }
}