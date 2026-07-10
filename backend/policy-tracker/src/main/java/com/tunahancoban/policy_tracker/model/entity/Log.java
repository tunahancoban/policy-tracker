package com.tunahancoban.policy_tracker.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "system_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Log {
    @Id
    private String id;
    private String type;
    private String detail;
    private String user;
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();
}
