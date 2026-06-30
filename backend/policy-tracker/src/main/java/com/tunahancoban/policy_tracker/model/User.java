package com.tunahancoban.policy_tracker.model;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    private String id; //Unique
    private String firstName;
    private String lastName;
    private String email; //Unique
    private String password;
    private String role;
}
