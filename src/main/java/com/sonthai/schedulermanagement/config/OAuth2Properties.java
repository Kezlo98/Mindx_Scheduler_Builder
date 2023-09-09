package com.sonthai.schedulermanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "oauth2")
@Data
public class OAuth2Properties {

    private Map<String, String> redirectUriMap;

    public String getRedirectUriByRegistrationId(String registrationId){
        return redirectUriMap.get(registrationId);
    }
}
