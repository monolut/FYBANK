package org.bank.authservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.bank.authservice.event.PasswordChangedEvent;
import org.bank.authservice.service.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PasswordChangedEventListener {

    private final AuthService authService;

    @Autowired
    public PasswordChangedEventListener(AuthService authService) {
        this.authService = authService;
    }

    @Async("applicationTaskExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void PasswordChangedHandler(PasswordChangedEvent event) {

        Long userId = event.getUserId();

        log.info("PasswordChangedEvent received, userId={}", userId);
        try {
            authService.logoutAll(userId);

            log.info("All sessions invalidated after password change, userId={}", userId);
        } catch (Exception e) {

            log.error(
                    "Failed to process PasswordChangedEvent, userId={}",
                    userId,
                    e
            );

            throw e;
        }
    }
}
