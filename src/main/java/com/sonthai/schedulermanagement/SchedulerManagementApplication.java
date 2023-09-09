package com.sonthai.schedulermanagement;

import com.sonthai.schedulermanagement.config.OAuth2Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OAuth2Properties.class})
public class SchedulerManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerManagementApplication.class, args);
    }

}
