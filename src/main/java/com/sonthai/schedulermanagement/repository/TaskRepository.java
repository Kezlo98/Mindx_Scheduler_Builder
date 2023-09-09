package com.sonthai.schedulermanagement.repository;

import com.sonthai.schedulermanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> getTaskByIdAndAssignee(Long taskId, String assignee);
    Optional<List<Task>> getAllByAssignee(String assignee);

    Optional<List<Task>> getAllByAssigneeAndTimeBetween(String assignee, LocalDateTime fromTime, LocalDateTime toTime);
    Optional<List<Task>> getAllByTimeBetween(LocalDateTime fromTime, LocalDateTime toTime);

    Optional<List<Task>> getTaskByIdIn(List<Long> id);
}
