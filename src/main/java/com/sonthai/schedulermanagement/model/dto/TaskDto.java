package com.sonthai.schedulermanagement.model.dto;

import com.sonthai.schedulermanagement.constant.TaskStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class TaskDto {

    private long id;

    @NotNull
    private String name;

    private String content;

    @NotNull
    private String assignee;

    @NotNull
    private TaskStatusEnum status;

    private boolean isImportant = false;

    private Set<String> links = new HashSet<>();

    @NotNull
    private LocalDateTime time;
}
