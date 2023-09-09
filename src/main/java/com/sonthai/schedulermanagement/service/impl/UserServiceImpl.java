package com.sonthai.schedulermanagement.service.impl;

import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import com.sonthai.schedulermanagement.entity.User;
import com.sonthai.schedulermanagement.exception.DomainCode;
import com.sonthai.schedulermanagement.exception.DomainException;
import com.sonthai.schedulermanagement.mapper.UserMapper;
import com.sonthai.schedulermanagement.model.dto.UserDto;
import com.sonthai.schedulermanagement.repository.UserRepository;
import com.sonthai.schedulermanagement.service.UserService;
import com.sonthai.schedulermanagement.util.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserDto userDto){
        Optional<User> userOpt = userRepository.findUserByUsernameAndRegistrationId(userDto.getUsername(), RegistrationEnum.BASIC);
        if(userOpt.isPresent()){
            throw new DomainException(DomainCode.USERNAME_IS_EXISTED,new Object[]{userDto.getUsername()});
        }
        try{
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userDto.setRegistrationId(RegistrationEnum.BASIC);
            User user = userRepository.save(userMapper.toEntity(userDto));

            return userMapper.toDto(user);
        } catch (Exception ex){
            throw new DomainException(DomainCode.GENERAL_ERROR);
        }
    }

    @Override
    public List<UserDto> getAllUserDetail() {
        List<User> user = userRepository.findAll();
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserDetail(){
        UserDto userDto = UserUtils.getUserDtoFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        Optional <User> userOpt = userRepository.findUserByUsernameAndRegistrationId(userDto.getUsername(), userDto.getRegistrationId());
        if(userOpt.isEmpty()){
            throw new DomainException(DomainCode.USERNAME_IS_NOT_EXISTED,new Object[]{userDto.getUsername(), userDto.getRegistrationId()});
        }

        return userMapper.toDto(userOpt.get());
    }

}
