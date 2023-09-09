package com.sonthai.schedulermanagement.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeRangeDto {
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
}
