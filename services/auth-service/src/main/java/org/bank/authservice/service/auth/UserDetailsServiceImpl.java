package org.bank.authservice.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.bank.authservice.entity.UserEntity;
import org.bank.authservice.exception.UserNotFoundByEmailException;
import org.bank.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

        log.info("UserDetailsServiceImpl initialized");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        log.debug("Loading user details for username={}", username);

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found by email={}", username);
                    return new UserNotFoundByEmailException(username);
                });

        log.debug(
                "User found: userId={}, email={}, role={}",
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleName()
        );

        UserDetails userDetails = User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getRoleName().toString())
                .build();

        log.debug(
                "UserDetails successfully created for userId={}",
                user.getId()
        );

        return userDetails;
    }
}