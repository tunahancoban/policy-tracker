package com.tunahancoban.policy_tracker.services;

import com.tunahancoban.policy_tracker.model.DTO.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<Customer> getCustomerByParam(String customerId, String firstName, String lastName,
                                             String identityNumber, String email, String phoneNumber){

        Customer searchCriteria = new Customer();
        searchCriteria.setId(customerId);
        searchCriteria.setFirstName(firstName);
        searchCriteria.setLastName(lastName);
        searchCriteria.setEmail(email);
        searchCriteria.setPhoneNumber(phoneNumber);
        searchCriteria.setIdentityNumber(identityNumber);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Customer> example = Example.of(searchCriteria, matcher);

        return customerRepository.findAll(example);
    }

    public Customer createCustomer(CreateCustomerRequest request){
        if(customerRepository.existsByIdentityNumber(request.getIdentityNumber())){
            throw new RuntimeException("This identity number exist");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setIdentityNumber(request.getIdentityNumber());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCity(request.getCity());
        customer.setDistrict(request.getDistrict());
        customer.setFullAddress(request.getFullAddress());

        customer.setCustomerID(request.getEmail());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setActive(true);

        customerRepository.save(customer);

        return customer;
    }

    public boolean existById(String customerId){
        return customerRepository.existsByCustomerID(customerId);
    }
}
