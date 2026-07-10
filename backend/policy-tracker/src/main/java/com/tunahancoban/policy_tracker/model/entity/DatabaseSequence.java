package com.tunahancoban.policy_tracker.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "database_sequences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseSequence {

    @Id
    private String id;

    private long seq; 
}