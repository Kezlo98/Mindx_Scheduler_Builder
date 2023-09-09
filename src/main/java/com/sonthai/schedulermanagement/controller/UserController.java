package com.sonthai.schedulermanagement.controller;

import com.sonthai.schedulermanagement.model.GeneralResponse;
import com.sonthai.schedulermanagement.model.dto.UserDto;
import com.sonthai.schedulermanagement.service.UserService;
import com.sonthai.schedulermanagement.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping("/register")
    public GeneralResponse<?> register(@Valid @RequestBody UserDto userDto){
        return ResponseUtils.buildSuccessResponse(userService.register(userDto));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping()
    public GeneralResponse<?> getUserDetail(){
        return ResponseUtils.buildSuccessResponse(userService.getUserDetail());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public GeneralResponse<?> getAllUser(){
        return ResponseUtils.buildSuccessResponse(userService.getAllUserDetail());
    }
}
