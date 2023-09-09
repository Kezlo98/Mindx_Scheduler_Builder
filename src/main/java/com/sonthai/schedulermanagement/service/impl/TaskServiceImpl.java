package com.sonthai.schedulermanagement.service.impl;

import com.sonthai.schedulermanagement.config.scheduler.ChangeStatusTask;
import com.sonthai.schedulermanagement.constant.TaskStatusEnum;
import com.sonthai.schedulermanagement.entity.Task;
import com.sonthai.schedulermanagement.entity.TaskFlow;
import com.sonthai.schedulermanagement.exception.DomainCode;
import com.sonthai.schedulermanagement.exception.DomainException;
import com.sonthai.schedulermanagement.mapper.TaskMapper;
import com.sonthai.schedulermanagement.model.dto.TaskDto;
import com.sonthai.schedulermanagement.model.dto.TimeRangeDto;
import com.sonthai.schedulermanagement.model.dto.UserDto;
import com.sonthai.schedulermanagement.repository.TaskFlowRepository;
import com.sonthai.schedulermanagement.repository.TaskRepository;
import com.sonthai.schedulermanagement.service.TaskService;
import com.sonthai.schedulermanagement.util.PDFUtil;
import com.sonthai.schedulermanagement.util.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private TaskMapper taskMapper;
    private TaskFlowRepository taskFlowRepository;
    private TaskScheduler taskScheduler;

    @Override
    public List<TaskDto> getAllTasks(){
        return taskMapper.toDto(taskRepository.findAll());
    }

    @Override
    public List<TaskDto> getAllPersonTask(){
        UserDto userDto = getUserDto();

        Optional<List<Task>> tasksOpt = taskRepository.getAllByAssignee(userDto.getUsername());
        if(tasksOpt.isEmpty()){
            throw new DomainException(DomainCode.TASK_IS_NOT_EXISTED_WITH_ASSIGNEE, new Object[]{userDto.getUsername()});
        }

        return taskMapper.toDto(tasksOpt.get());
    }

    @Override
    public List<TaskDto> getAllTasksInRange(TimeRangeDto timeRangeDto){
        validateRangeTime(timeRangeDto);

        return taskMapper.toDto(taskRepository.getAllByTimeBetween(timeRangeDto.getFromTime(), timeRangeDto.getToTime()).get());
    }

    @Override
    public List<TaskDto> getAllPersonTaskInRange(TimeRangeDto timeRangeDto){
        UserDto userDto = getUserDto();
        validateRangeTime(timeRangeDto);

        return taskMapper.toDto(taskRepository.getAllByAssigneeAndTimeBetween(userDto.getUsername(), timeRangeDto.getFromTime(), timeRangeDto.getToTime()).get());
    }

    @Override
    public TaskDto createTask(TaskDto taskDto){
        UserDto userDto = getUserDto();
        taskDto.setAssignee(userDto.getUsername());
        taskDto.setStatus(TaskStatusEnum.WAITING);
        Task task = taskMapper.toEntity(taskDto);
        Task savedTask = taskRepository.save(task);
        scheduleTask(Collections.singletonList(task.getId()), task.getTime());
        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskDto getPersonTask(Long id){
        return taskMapper.toDto(getTaskById(id));
    }

    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto){
        Task task = getTaskById(id);
        task = taskMapper.update(task,taskDto);
        Task savedTask = taskRepository.save(task);
        scheduleTask(Collections.singletonList(savedTask.getId()), savedTask.getTime());
        return taskMapper.toDto(savedTask);
    }

    @Override
    public void deleteTask(Long id){
        getTaskById(id);
        taskRepository.deleteById(id);
    }

    @Override
    public ResponseEntity<Resource> downloadTask(Long id) throws IOException {
        UserDto userDto = getUserDto();
        if(userDto == null){
            throw  new BadCredentialsException("Cannot find user with authentication");
        }

        String pdfFileName = PDFUtil.buildPDFName(userDto, id);
        File file = PDFUtil.getPDF(pdfFileName);
        if(!file.isFile()){
            TaskDto resume = getPersonTask(id);
            PDFUtil.generateResumePDF(userDto, Collections.singletonList(resume));
            file = PDFUtil.getPDF(pdfFileName);
        }

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path), pdfFileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "attachment; filename=\"" + pdfFileName + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private void scheduleTask(List<Long> ids, LocalDateTime time){
        taskScheduler.schedule(new ChangeStatusTask(ids, taskRepository), Timestamp.valueOf(time));
    }

    private UserDto getUserDto(){
        return UserUtils.getUserDtoFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    }

    private Task getTaskById(Long id){
        UserDto userDto = getUserDto();
        Optional<Task> taskOpt = taskRepository.getTaskByIdAndAssignee(id, userDto.getUsername());
        if(taskOpt.isEmpty()){
            throw new DomainException(DomainCode.TASK_IS_NOT_EXISTED, new Object[]{id, userDto.getUsername()});
        }
        return taskOpt.get();
    }

    private void validateRangeTime(TimeRangeDto rangeDto){
        if(rangeDto.getFromTime() == null && rangeDto.getToTime() == null){
            throw new DomainException(DomainCode.INVALID_REQUEST, new Object[]{"Invalid dateTime"});
        }

        if(rangeDto.getFromTime() != null && rangeDto.getToTime() != null){
            if(rangeDto.getFromTime().isAfter(rangeDto.getToTime())){
                throw new DomainException(DomainCode.INVALID_REQUEST, new Object[]{"Invalid dateTime"});
            }
        }
    }
}
