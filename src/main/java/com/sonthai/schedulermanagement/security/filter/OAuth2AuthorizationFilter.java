package com.sonthai.schedulermanagement.security.filter;

import com.sonthai.schedulermanagement.constant.SecurityConstant;
import com.sonthai.schedulermanagement.security.TokenManager;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@NoArgsConstructor
public class OAuth2AuthorizationFilter extends GenericFilterBean {

    private TokenManager tokenManager;

    @Autowired
    public OAuth2AuthorizationFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = extractToken((HttpServletRequest)request);
        if(StringUtils.hasText(token)){
            Authentication authentication = tokenManager.get(token);
            if(authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request,response);
    }

    private String extractToken(HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith(SecurityConstant.BEARER)){
            return token.substring(SecurityConstant.BEARER.length());
        }

        return token;
    }
}
