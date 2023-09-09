package com.sonthai.schedulermanagement.repository;

import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import com.sonthai.schedulermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = (:username) AND u.registrationId = (:registrationId)")
    Optional<User> findUserByUsernameAndRegistrationId(String username, RegistrationEnum registrationId);
}
