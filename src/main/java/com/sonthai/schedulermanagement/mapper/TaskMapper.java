package com.sonthai.schedulermanagement.mapper;

import com.sonthai.schedulermanagement.entity.Task;
import com.sonthai.schedulermanagement.model.dto.TaskDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(Task task);

    List<TaskDto> toDto(List<Task> taskList);

    Task toEntity(TaskDto taskDto);

    @Mapping(target = "id",ignore = true)
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task update(@MappingTarget Task task, TaskDto taskDto);
}
