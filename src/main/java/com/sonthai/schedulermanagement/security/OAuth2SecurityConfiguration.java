package com.sonthai.schedulermanagement.security;

import com.sonthai.schedulermanagement.constant.SecurityConstant;
import com.sonthai.schedulermanagement.security.filter.OAuth2AuthenticationFilter;
import com.sonthai.schedulermanagement.security.filter.OAuth2AuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Order(2)
public class OAuth2SecurityConfiguration {

    private final OAuth2AuthenticationFilter auth2AuthenticationFilter;
    private final OAuth2AuthorizationFilter auth2AuthorizationFilter;

    private final String[] WHITELIST_PATH = {"/api/user/register", "/login/**"};

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .requestMatcher(request -> {
                    String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
                    return auth != null && auth.startsWith(SecurityConstant.BEARER);
                })
                .csrf().disable()

                .addFilterBefore(auth2AuthenticationFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(auth2AuthorizationFilter, BasicAuthenticationFilter.class)

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeHttpRequests(registry -> {
                    registry.antMatchers(WHITELIST_PATH).permitAll();
                    registry.anyRequest().authenticated();
                })

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new OAuth2AuthenticationEntryPoint());
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new OAuth2AccessDeniedHandler());
                })
                .build();

    }
}
