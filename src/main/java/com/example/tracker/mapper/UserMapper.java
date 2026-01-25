package com.example.tracker.mapper;

import com.example.tracker.dto.UserDto;
import com.example.tracker.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto dto);
}