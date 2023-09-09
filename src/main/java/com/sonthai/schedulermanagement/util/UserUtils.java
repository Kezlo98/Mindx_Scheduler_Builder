package com.sonthai.schedulermanagement.util;

import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import com.sonthai.schedulermanagement.model.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Map;

public class UserUtils {

    public static UserDto getUserDtoFromAuthentication(Authentication authentication){
        if(authentication == null){
            return null;
        }
        UserDto userDto = new UserDto();
        if(authentication instanceof OAuth2AuthenticationToken){
            userDto.setUsername(getUserName(((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes(), ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()));
            userDto.setRegistrationId(RegistrationEnum.valueOf(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().toUpperCase()));
        } else {
            userDto.setUsername(authentication.getName());
            userDto.setRegistrationId(RegistrationEnum.BASIC);
        }

        return userDto;
    }

    public static String getUserName(Map<String, Object> attributes, String registrationId) {
        if (RegistrationEnum.value(registrationId) == RegistrationEnum.GITHUB) {
            return  (String) attributes.get("login");
        }
        return (String) attributes.get("email");
    }
}
