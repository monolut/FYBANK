package org.bank.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.userservice.dto.AccountDto;
import org.bank.userservice.dto.BalanceDto;
import org.bank.userservice.dto.CreateUserRequest;
import org.bank.userservice.dto.UserDto;
import org.bank.userservice.entity.UserEntity;
import org.bank.userservice.event.UserDeletedEvent;
import org.bank.userservice.exception.UserAlreadyExistException;
import org.bank.userservice.exception.UserNotFoundById;
import org.bank.userservice.feign.AccountFeign;
import org.bank.userservice.feign.BalanceFeign;
import org.bank.userservice.mapper.UserMapper;
import org.bank.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthCommonService authCommonService;
    private final AccountFeign accountFeign;
    private final BalanceFeign balanceFeign;

    @Autowired
    public UserService(
            ApplicationEventPublisher eventPublisher,
            UserRepository userRepository,
            UserMapper userMapper,
            AuthCommonService authCommonService,
            AccountFeign accountFeign,
            BalanceFeign balanceFeign
    ) {
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authCommonService = authCommonService;
        this.accountFeign = accountFeign;
        this.balanceFeign = balanceFeign;

        log.info("UserService initialized");
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {

        Long userId = authCommonService.getUserId();

        log.info("Creating user with userId={}", userId);

        if (userRepository.existsById(userId)) {
            throw new UserAlreadyExistException(userId);
        }

        UserEntity newUser = new UserEntity(
                userId,
                request.getFirstName(),
                request.getLastName(),
                authCommonService.getUserEmail(),
                request.getPhoneNumber()
        );

        log.debug("Saving new user with userId={}", userId);

        UserEntity saved = userRepository.save(newUser);

        log.info("User successfully created, userId={}", userId);

        return userMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts() {

        Long userId = authCommonService.getUserId();

        log.info("Fetching accounts for userId={}", userId);

        List<AccountDto> accounts =
                accountFeign.getUserAccounts(userId).getBody();

        log.info(
                "Accounts successfully fetched for userId={}, accountCount={}",
                userId,
                accounts != null ? accounts.size() : 0
        );

        return accounts;
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalanceByAccountId(Long accountId) {

        log.info("Fetching balance for accountId={}", accountId);

        BalanceDto balance =
                balanceFeign.getBalanceByAccountId(accountId).getBody();

        log.info(
                "Balance successfully fetched for accountId={}",
                accountId
        );

        return balance;
    }

    @Transactional
    public void deleteUser() {

        Long userId = authCommonService.getUserId();

        log.info("Deleting user with userId={}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        log.debug("User found, publishing UserDeletedEvent for userId={}", userId);

        eventPublisher.publishEvent(
                new UserDeletedEvent(userId)
        );

        log.info("User deletion event published for userId={}", userId);
    }
}