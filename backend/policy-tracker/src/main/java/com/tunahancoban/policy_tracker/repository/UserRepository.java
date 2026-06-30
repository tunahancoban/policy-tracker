package com.tunahancoban.policy_tracker.repository;
import com.tunahancoban.policy_tracker.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, String id);
}
