package com.tunahancoban.policy_tracker.repository;
import com.tunahancoban.policy_tracker.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, String id);

    Optional<User> findByEmail(String email);
}
