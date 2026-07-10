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
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final IdGeneratorService idGeneratorService;

    public List<Customer> getCustomerByParam(String customerId, String firstName, String lastName,
                                             String identityNumber, String email, String phoneNumber){
        //It creates a searchCriteria
        Customer searchCriteria = Customer.builder()
                .customerID(customerId)
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
                .customerID(idGeneratorService.generateCustomerId())
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
        Customer customer = customerRepository.findByCustomerID(id);
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    if(((String) value).isEmpty()){throw new RuntimeException("Invalid  first name: " + value);}
                    customer.setFirstName((String) value);
                    break;

                case "lastName":
                    if(((String) value).isEmpty()){ throw new RuntimeException("Invalid  last name: " + value);}
                    customer.setLastName((String) value);
                    break;

                case "identityNumber":
                    if(customerRepository.existsByIdentityNumber((String) value) || ((String) value).length()!=11){
                        throw new RuntimeException("Invalid identity number: " + value);}
                    customer.setIdentityNumber((String) value);
                    break;

                case "email":
                    if (value != null && !emailPattern.matcher((String)value).matches()) {
                        throw new RuntimeException("Invalid email format: " + value);
                    }
                    customer.setEmail((String) value);
                    break;

                case "phoneNumber":
                    customer.setPhoneNumber((String) value);
                    break;

                case "city":
                    customer.setCity((String) value);
                    break;

                case "district":
                    customer.setDistrict((String) value);
                    break;

                case "fullAddress":
                    customer.setFullAddress((String) value);
                    break;

                case "active":
                    customer.setActive((Boolean) value);
                    break;
            }

        }
        );
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        return true;
    }

    @LogActivity(type = "MUSTERI"
            ,detail = " Müşteri silindi.")
    public void deleteCustomer(String id){
        if(!existById(id)){
            throw new RuntimeException("This customer does not exist");
        }
        Customer customer = customerRepository.findByCustomerID(id);

        customerRepository.deleteById(customer.getId());
    }

    //Checks id does exist or not
    public boolean existById(String customerId){
        return customerRepository.existsByCustomerID(customerId);
    }

}
