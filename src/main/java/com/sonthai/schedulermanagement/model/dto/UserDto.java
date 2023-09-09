package com.sonthai.schedulermanagement.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {

    @NotNull
    @NotBlank
    private String username;
    private String password;
    private String email;
    private RegistrationEnum registrationId = RegistrationEnum.BASIC;
    private Set<String> roles = Set.of("ROLE_USER");
}
