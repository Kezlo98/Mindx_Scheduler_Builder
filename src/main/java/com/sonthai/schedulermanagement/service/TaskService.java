package com.sonthai.schedulermanagement.service;

import com.sonthai.schedulermanagement.model.dto.TaskDto;
import com.sonthai.schedulermanagement.model.dto.TimeRangeDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface TaskService {
    List<TaskDto> getAllTasks();

    List<TaskDto> getAllPersonTask();

    List<TaskDto> getAllTasksInRange(TimeRangeDto timeRangeDto);

    List<TaskDto> getAllPersonTaskInRange(TimeRangeDto timeRangeDto);

    TaskDto createTask(TaskDto taskDto);

    TaskDto getPersonTask(Long id);

    TaskDto updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);

    ResponseEntity<Resource> downloadTask(Long id) throws IOException;
}
