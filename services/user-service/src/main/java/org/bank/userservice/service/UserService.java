package org.bank.userservice.service;

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
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        Long userId = authCommonService.getUserId();

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
        UserEntity saved = userRepository.save(newUser);
        return userMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts() {
        Long userId = authCommonService.getUserId();
        return accountFeign.getUserAccounts(userId).getBody();
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalanceByAccountId(Long accountId) {
        return balanceFeign.getBalanceByAccountId(accountId).getBody();
    }

    @Transactional
    public void deleteUser() {
        Long userId = authCommonService.getUserId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));
        eventPublisher.publishEvent(new UserDeletedEvent(userId));
    }
}
