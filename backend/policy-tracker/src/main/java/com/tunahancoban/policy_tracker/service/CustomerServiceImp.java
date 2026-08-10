package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.mapper.CustomerMapper;
import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import com.tunahancoban.policy_tracker.service.interfaces.CustomerService;
import com.tunahancoban.policy_tracker.service.interfaces.IdGeneratorService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImp implements CustomerService {
    private final CustomerRepository customerRepository;
    private final IdGeneratorService idGeneratorService;
    private final CustomerMapper customerMapper;

    @Override
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

    @Override
    public Customer getCustomerByCustomerId(String customerId) {
        log.debug("Fetching customer - customerId: {}", customerId);
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found - customerId: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + customerId);
                });
        return customer;
    }

    @LogActivity( type = "MUSTERI", detail = "'Yeni müşteri eklendi. ID: ' + #result.customerId" )
    @Override
    public Customer createCustomer(CreateCustomerRequest request) {
        log.info("Create customer request received - identityNumber: {}, email: {}",
                request.getIdentityNumber(), request.getEmail());

        if (customerRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            log.warn("Customer creation failed - identity number already registered: {}", request.getIdentityNumber());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A customer with this identity number already exists: " + request.getIdentityNumber());
        }


        Customer customer = customerMapper.toEntity(request);

        customer.setCustomerId(idGeneratorService.generateCustomerId());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        customerRepository.save(customer);
        log.info("Customer successfully created - customerId: {}", customer.getCustomerId());

        return customer;
    }

    @LogActivity(type = "MUSTERI", detail = "'Müşteri güncellendi. ID: ' + #result.customerId")
    @Override
    public Customer updateCustomer(String id, UpdateCustomerRequest updates) {
        log.info("Update customer request received - customerId: {}", id);

        Customer customer = customerRepository.findByCustomerId(id)
                .orElseThrow(() -> {
                    log.warn("Customer update failed - customer not found: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id);
                });


        customerMapper.updateEntityFromRequest(updates, customer);
        customer.setUpdatedAt(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(customer);

        customerRepository.save(savedCustomer);
        log.info("Customer successfully updated - customerId: {}", id);

        return savedCustomer;
    }

    @LogActivity(type = "MUSTERI", detail = "'Müşteri silindi. ID: ' + #id")
    @Override
    public void deleteCustomer(String id) {
        log.info("Delete customer request received - customerId: {}", id);

        Customer customer = customerRepository.findByCustomerId(id)
                .orElseThrow(() -> {
                    log.warn("Customer deletion failed - customer not found: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id);
                });

        customerRepository.deleteById(customer.getId());
        log.info("Customer successfully deleted - customerId: {}", id);
    }

    @Override
    public boolean existById(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }
}