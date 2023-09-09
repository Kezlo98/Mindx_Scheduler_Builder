package com.sonthai.schedulermanagement.entity;

import com.sonthai.schedulermanagement.constant.TaskStatusEnum;
import lombok.Data;
import lombok.Generated;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class TaskFlow {

    @Id
    @Generated
    private Long id;

    private TaskStatusEnum stage;

    private TaskStatusEnum nextStage;

    private boolean isLastStage;
}
