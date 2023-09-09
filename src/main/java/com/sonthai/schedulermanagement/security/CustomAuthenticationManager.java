package com.sonthai.schedulermanagement.security;

import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import com.sonthai.schedulermanagement.entity.User;
import com.sonthai.schedulermanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null){
            throw new BadCredentialsException("Invalid authentication!");
        }

        return authenticateBasicAuth(authentication);
    }

    private UsernamePasswordAuthenticationToken authenticateBasicAuth(Authentication authentication) {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        Optional<User> userOpt = userRepository.findUserByUsernameAndRegistrationId(username, RegistrationEnum.BASIC);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            if(passwordEncoder.matches(pwd,user.getPassword())){
                return new  UsernamePasswordAuthenticationToken(username,pwd,getGrantedAuthorities(user.getRoles()));
            }else {
                throw new BadCredentialsException("Invalid Password!");
            }
        } else {
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<String> authorities) {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
