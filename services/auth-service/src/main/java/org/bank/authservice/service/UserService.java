package org.bank.authservice.service;

import org.bank.authcommon.service.AuthCommonService;
import org.bank.authservice.dto.UserDto;
import org.bank.authservice.entity.RoleEntity;
import org.bank.authservice.entity.UserEntity;
import org.bank.authservice.enums.Role;
import org.bank.authservice.event.PasswordChangedEvent;
import org.bank.authservice.exception.*;
import org.bank.authservice.mapper.UserMapper;
import org.bank.authservice.repository.RefreshTokenRepository;
import org.bank.authservice.repository.RoleRepository;
import org.bank.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthCommonService authCommonService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher applicationEventPublisher,
            AuthCommonService authCommonService,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.applicationEventPublisher = applicationEventPublisher;
        this.authCommonService = authCommonService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {

        log.debug("Fetching users with role USER");

        List<UserDto> users = userRepository.findByRole(Role.USER).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        log.debug("Fetched {} users with role USER", users.size());

        return users;
    }

    @Transactional(readOnly = true)
    public UserDto getDtoByEmail(String email) {

        log.debug("Fetching user DTO by email={}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserEntity getEntityByEmail(String email) {

        log.debug("Fetching user entity by email={}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));
    }

    @Transactional
    public UserDto createUser(String email, String password) {

        log.info("Creating new user with email={}", email);

        if(userRepository.findByEmail(email).isPresent()) {
            throw new UserEmailAlreadyExistsException(email);
        }

        RoleEntity role = roleRepository.findByRoleName(Role.USER)
                .orElseThrow(() -> {
                    log.error("Role USER not found in database");
                    return RoleNotFoundException.byRole(Role.USER);
                });

        UserEntity user = new UserEntity();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        UserEntity savedUser = userRepository.save(user);

        log.info("User successfully created, userId={}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto updateUserById(UserDto userDto) {
        Long id = authCommonService.getUserId();

        log.info("Updating user data for userId={}", id);

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundByIdException(id));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userEntity.setEmail(userDto.getEmail());
            log.debug("User email updated for userId={}", id);
        }

        userRepository.save(userEntity);

        log.info("User data successfully updated for userId={}", id);

        return userMapper.toDto(userEntity);
    }

    @Transactional
    public UserDto updateUserPassword(String oldPassword, String newPassword) {
        Long id = authCommonService.getUserId();

        log.info("Updating password for userId={}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundByIdException(id));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Password update failed: invalid old password, userId={}", id);
            throw new BadCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        applicationEventPublisher.publishEvent(
                new PasswordChangedEvent(user.getId())
        );

        log.info("Password successfully updated for userId={}", id);

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with userId={}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        refreshTokenRepository.deleteAllByUserId(userId);
        userRepository.delete(user);

        log.info("User successfully deleted, userId={}", userId);
    }
}
