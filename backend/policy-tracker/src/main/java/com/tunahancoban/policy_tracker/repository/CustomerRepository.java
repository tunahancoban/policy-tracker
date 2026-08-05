package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    boolean existsByCustomerId(String customerID);
    boolean existsByIdentityNumber(String identityNUmber);
    Optional<Customer> findByCustomerId(String customerId);
    Page<Customer> findAll(Pageable pageable);

}
