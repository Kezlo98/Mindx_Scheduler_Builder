package com.sonthai.schedulermanagement.service;

import com.sonthai.schedulermanagement.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto register(UserDto userDto);

    List<UserDto> getAllUserDetail();

    UserDto getUserDetail();
}
