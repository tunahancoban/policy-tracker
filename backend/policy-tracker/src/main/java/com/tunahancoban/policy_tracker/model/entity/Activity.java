package com.tunahancoban.policy_tracker.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "activities")
public class Activity {
    @Id
    private String id;

    private String title;
    private String description;

    private String customerId; //Which customer
    private String changeByField; //Which field changed

    private String username; //Which user did this action?

    private LocalDateTime date = LocalDateTime.now();
}
