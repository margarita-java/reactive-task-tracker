package com.example.tracker.mapper;

import com.example.tracker.dto.TaskDto;
import com.example.tracker.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskDto dto);

    TaskDto toDto(Task task);
}