package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.model.DTO.request.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final IdGeneratorService idGeneratorService;

    public List<Customer> getCustomerByParam(String customerId, String firstName, String lastName,
                                             String identityNumber, String email, String phoneNumber){
        //It creates a searchCriteria
        Customer searchCriteria = Customer.builder()
                .customerId(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .identityNumber(identityNumber)
                .email(email)
                .phoneNumber(phoneNumber).build();

        //And it is searching according to criteria. It ignores null values and it is not case-sensitive. If it contains the word it also finds it.
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withIgnorePaths("createdAt", "updatedAt", "active")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Customer> example = Example.of(searchCriteria, matcher);

        return customerRepository.findAll(example);
    }

    @LogActivity(
            type = "MUSTERI",
            detail = "Yeni müşteri eklendi."
    )
    public Customer createCustomer(CreateCustomerRequest request){
        //Identity number is unique so it checks this customer is exist or not
        if(customerRepository.existsByIdentityNumber(request.getIdentityNumber())){
            throw new RuntimeException("This identity number exist");
        }

        //If it is not saves customer
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
        return customer;
    }

    @LogActivity(type = "MUSTERI"
            ,detail = " Müşteri güncellendi.")
    public boolean updateCustomer(String id, Map<String, Object> updates){

        if(!existById(id)){
            throw new RuntimeException("This customer does not exist");
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
            }

        }
        );
        customerBuilder.updatedAt(LocalDateTime.now());
        Customer saveCustomer = customerBuilder.build();
        customerRepository.save(saveCustomer);
        return true;
    }

    @LogActivity(type = "MUSTERI"
            ,detail = " Müşteri silindi.")
    public void deleteCustomer(String id){
        if(!existById(id)){
            throw new RuntimeException("This customer does not exist");
        }
        Customer customer = customerRepository.findByCustomerId(id);

        customerRepository.deleteById(customer.getId());
    }

    //Checks id does exist or not
    public boolean existById(String customerId){
        return customerRepository.existsByCustomerId(customerId);
    }

}
