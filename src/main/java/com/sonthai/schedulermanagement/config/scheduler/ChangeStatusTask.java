package com.sonthai.schedulermanagement.config.scheduler;

import com.sonthai.schedulermanagement.constant.TaskStatusEnum;
import com.sonthai.schedulermanagement.entity.Task;
import com.sonthai.schedulermanagement.repository.TaskRepository;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class ChangeStatusTask implements Runnable{

    private List<Long> taskId;
    private TaskRepository taskRepository;

    public ChangeStatusTask(List<Long> taskId, TaskRepository taskRepository) {
        this.taskId = taskId;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run() {
        Optional<List<Task>> tasksOpt = taskRepository.getTaskByIdIn(taskId);
        if(tasksOpt.isEmpty()){
            return;
        }

        List<Task> taskToUpdate = tasksOpt.get();

        for (Task task: taskToUpdate){
            task.setStatus(TaskStatusEnum.APPROVED);
        }

        taskRepository.saveAll(taskToUpdate);

    }
}
