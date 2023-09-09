package com.sonthai.schedulermanagement.security.filter;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.sonthai.schedulermanagement.config.OAuth2Properties;
import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import com.sonthai.schedulermanagement.entity.User;
import com.sonthai.schedulermanagement.repository.UserRepository;
import com.sonthai.schedulermanagement.security.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class OAuth2AuthenticationFilter extends GenericFilterBean {

    private final String BASE_URI = "/login/oauth2/code";
    private final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(BASE_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}", "GET");
    private final String NONCE_PARAMETER_NAME = "nonce";

    private final OAuth2Properties oAuth2Properties;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    private final TokenManager tokenManager;
    private final DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    public OAuth2AuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
                                      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                                      TokenManager tokenManager,
                                      UserRepository userRepository,
                                      OAuth2Properties oAuth2Properties) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
        this.tokenManager = tokenManager;
        this.userRepository = userRepository;
        this.oAuth2Properties = oAuth2Properties;

        authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, BASE_URI);
        authenticationManager = new ProviderManager(
                new OidcAuthorizationCodeAuthenticationProvider(new DefaultAuthorizationCodeTokenResponseClient(), new OidcUserService()),
                new OAuth2LoginAuthenticationProvider(new DefaultAuthorizationCodeTokenResponseClient(), new DefaultOAuth2UserService())
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!isRequireAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            OAuth2AuthenticationToken authentication = authenticate(request, response);
            successfulAuthentication(response, authentication);
        } catch (Exception e) {
            log.error("Error when authenticate for OAuth2AuthenticationToken", e);
            unsuccessfulAuthentication(response, e);
        }

    }

    private boolean isRequireAuthentication(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    private OAuth2AuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response) {
        String registrationId = getRegistrationId(request);
        RegistrationEnum registrationEnum = RegistrationEnum.valueOf(registrationId.toUpperCase());

        OAuth2LoginAuthenticationToken authenticationResult = validateOAuth2LoginRequest(request, registrationId);

        String username = tokenManager.getUserName(authenticationResult.getPrincipal().getAttributes(), registrationId);
        Optional<User> userOpt = userRepository.findUserByUsernameAndRegistrationId(username, registrationEnum);
        User user;
        user = userOpt.orElseGet(() -> saveUser(User.builder()
                .username(username)
                .registrationId(registrationEnum)
                .name((String) authenticationResult.getPrincipal().getAttributes().get("name"))
                .roles(Collections.singleton("ROLE_USER"))
                .build()));


        Collection<GrantedAuthority> authorities = combineAuthorities(authenticationResult, user);

        OAuth2AuthenticationToken oAuth2Authentication = new OAuth2AuthenticationToken(
                authenticationResult.getPrincipal(),
                authorities,
                registrationId
        );

        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                authenticationResult.getClientRegistration(),
                oAuth2Authentication.getName(),
                authenticationResult.getAccessToken(),
                authenticationResult.getRefreshToken()
        );

        oAuth2AuthorizedClientRepository.saveAuthorizedClient(authorizedClient, oAuth2Authentication, request, response);

        return oAuth2Authentication;
    }

    private OAuth2LoginAuthenticationToken validateOAuth2LoginRequest(HttpServletRequest request, String registrationId) {
        String code = getCode(request);
        if (StringUtils.isBlank(code)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("Authentication Code is missing"));
        }
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("Client Registration not found"));
        }

        String redirectUri = oAuth2Properties.getRedirectUriByRegistrationId(registrationId);

        authorizationRequestResolver.setAuthorizationRequestCustomizer(builder -> {
            builder.redirectUri(redirectUri);
            builder.additionalParameters(additionalParameters -> additionalParameters.remove(NONCE_PARAMETER_NAME));
            builder.attributes(attributes -> attributes.remove(NONCE_PARAMETER_NAME));
        });

        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorizationRequestResolver.resolve(request, registrationId);

        OAuth2AuthorizationResponse oAuth2AuthorizationResponse = OAuth2AuthorizationResponse
                .success(code)
                .redirectUri(redirectUri)
                .state(oAuth2AuthorizationRequest.getState())
                .build();

        Authentication authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration, new OAuth2AuthorizationExchange(oAuth2AuthorizationRequest, oAuth2AuthorizationResponse));
        return (OAuth2LoginAuthenticationToken) authenticationManager.authenticate(authenticationRequest);
    }

    private void successfulAuthentication(HttpServletResponse response, OAuth2AuthenticationToken authentication) throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenManager.generate(authentication);
        tokenManager.set(token, authentication);

        String CONTENT_TYPE = "Content-Type";
        response.addHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().println(token);
    }

    private void unsuccessfulAuthentication(HttpServletResponse response, Exception e) throws IOException {
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    private Collection<GrantedAuthority> combineAuthorities(OAuth2LoginAuthenticationToken authentication, User user) {
        Set<GrantedAuthority> auth = new HashSet<>(authentication.getAuthorities());
        user.getRoles().forEach(role -> auth.add(new SimpleGrantedAuthority(role)));

        return auth;
    }

    private String getCode(HttpServletRequest request) {
        return request.getParameter("code");
    }

    private String getRegistrationId(HttpServletRequest request) {
        String registrationId = requestMatcher.matcher(request).getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME).toLowerCase();
        if (StringUtils.isBlank(registrationId)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("Client RegistrationId is missing"));
        }
        return registrationId;
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }
}
