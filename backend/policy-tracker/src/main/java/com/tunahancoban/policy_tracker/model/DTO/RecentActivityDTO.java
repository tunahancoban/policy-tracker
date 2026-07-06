package com.tunahancoban.policy_tracker.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivityDTO {
    private String title;
    private String description;
    private LocalDateTime date;
}
