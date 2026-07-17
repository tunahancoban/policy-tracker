package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    boolean existsByCustomerId(String customerID);
    boolean existsByIdentityNumber(String identityNUmber);
    Customer findByCustomerId(String customerId);
}
