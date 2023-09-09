package com.sonthai.schedulermanagement.security;

import com.sonthai.schedulermanagement.constant.SecurityConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BasicAuthSecurity {

    private final String[] WHITELIST_PATH = {"/api/user/register", "/login/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .requestMatcher(request -> {
                    String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
                    return auth == null || !auth.startsWith(SecurityConstant.BEARER);
                })
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeHttpRequests(registry -> {
                    registry.antMatchers(WHITELIST_PATH).permitAll();
                })
                .httpBasic().and()
                .formLogin().disable()
                .authorizeHttpRequests(registry -> {
                    registry.anyRequest().authenticated();
                }).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
