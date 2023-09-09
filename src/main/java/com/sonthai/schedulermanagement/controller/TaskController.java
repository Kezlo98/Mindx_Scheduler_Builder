package com.sonthai.schedulermanagement.controller;

import com.sonthai.schedulermanagement.model.GeneralResponse;
import com.sonthai.schedulermanagement.model.dto.TaskDto;
import com.sonthai.schedulermanagement.model.dto.TimeRangeDto;
import com.sonthai.schedulermanagement.service.TaskService;
import com.sonthai.schedulermanagement.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {
    
    private TaskService taskService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/test/user")
    public String testLogin(){
        return "User Ok";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/test/admin")
    public String testADMINLogin(){
        return "Admin Ok";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/time/admin")
    public GeneralResponse<?> getAllTaskInRange(@RequestBody TimeRangeDto timeRangeDto){
        return ResponseUtils.buildSuccessResponse(taskService.getAllTasksInRange(timeRangeDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public GeneralResponse<?> getAllTask(){
        return ResponseUtils.buildSuccessResponse(taskService.getAllTasks());
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping()
    public GeneralResponse<?> getPersonTasks(){
        return ResponseUtils.buildSuccessResponse(taskService.getAllPersonTask());
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/time")
    public GeneralResponse<?> getPersonTasksInRange(@RequestBody TimeRangeDto timeRangeDto){
        return ResponseUtils.buildSuccessResponse(taskService.getAllPersonTaskInRange(timeRangeDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{id}")
    public GeneralResponse<?> getPersonTask(@PathVariable Long id){
        return ResponseUtils.buildSuccessResponse(taskService.getPersonTask(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping
    public GeneralResponse<?> createTask(@RequestBody TaskDto taskDto){
        return ResponseUtils.buildSuccessResponse(taskService.createTask(taskDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PutMapping("/{id}")
    public GeneralResponse<?> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto){
        return ResponseUtils.buildSuccessResponse(taskService.updateTask(id, taskDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @DeleteMapping ("/{id}")
    public GeneralResponse<?> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseUtils.buildSuccessResponse(null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadTask(@PathVariable Long id) throws IOException {
        return taskService.downloadTask(id);
    }
}
