package com.sonthai.schedulermanagement.repository;

import com.sonthai.schedulermanagement.constant.TaskStatusEnum;
import com.sonthai.schedulermanagement.entity.TaskFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskFlowRepository extends JpaRepository<TaskFlow, Long> {

    Optional<TaskFlow> getTaskFlowByStage(TaskStatusEnum stage);
}
