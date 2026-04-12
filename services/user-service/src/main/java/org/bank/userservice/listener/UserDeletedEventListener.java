package org.bank.userservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.bank.userservice.entity.UserEntity;
import org.bank.userservice.event.UserDeletedEvent;
import org.bank.userservice.exception.UserNotFoundById;
import org.bank.userservice.feign.AccountFeign;
import org.bank.userservice.feign.AuthFeign;
import org.bank.userservice.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class UserDeletedEventListener {

    private final AuthFeign authFeign;
    private final AccountFeign accountFeign;
    private final UserRepository userRepository;

    public UserDeletedEventListener(
            AuthFeign authFeign,
            AccountFeign accountFeign,
            UserRepository userRepository
    ) {
        this.authFeign = authFeign;
        this.accountFeign = accountFeign;
        this.userRepository = userRepository;
    }

    @Async("applicationTaskExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void userDeletedHandle(UserDeletedEvent event) {

        Long userId = event.getUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundById(userId));

        log.info("UserDeletedEvent received for userId={}", userId);
        try {
            accountFeign.deleteAllAccounts(userId);
            authFeign.logoutAll(userId);
            authFeign.deleteUserData(userId);
            userRepository.delete(user);
            log.info("All sessions ends, userId={}", userId);
        } catch (Exception e) {

            log.error(
                    "Failed to process UserDeletedEvent, userId={}",
                    userId,
                    e
            );

            throw e;
        }
    }
}
