package org.bank.accountservice.service;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.bank.accountservice.dto.TransactionDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransferService {

    private final TransferTransactionalService transferTransactionalService;
    private static final int MAX_RETRIES = 3;

    public TransferService(
            TransferTransactionalService transferTransactionalService
    ) {
        this.transferTransactionalService = transferTransactionalService;

        log.info("TransferService initialized");
    }

    public TransactionDto executeTransfer(
            Long senderId,
            Long recipientId,
            BigDecimal amount
    ) {

        log.info(
                "Starting transfer: senderId={}, recipientId={}, amount={}",
                senderId,
                recipientId,
                amount
        );

        byte attempts = 0;

        while (attempts < MAX_RETRIES) {
            try {

                TransactionDto transaction =
                        transferTransactionalService.doTransferTransactional(
                                senderId,
                                recipientId,
                                amount
                        );

                log.info(
                        "Transfer completed successfully: senderId={}, recipientId={}, amount={}",
                        senderId,
                        recipientId,
                        amount
                );

                return transaction;

            } catch (OptimisticLockException e) {

                attempts++;

                log.warn(
                        "Optimistic lock conflict during transfer, retry attempt {}/{}: senderId={}, recipientId={}, amount={}",
                        attempts,
                        MAX_RETRIES,
                        senderId,
                        recipientId,
                        amount
                );

                if (attempts == MAX_RETRIES) {
                    log.error(
                            "Transfer failed after {} attempts: senderId={}, recipientId={}, amount={}",
                            MAX_RETRIES,
                            senderId,
                            recipientId,
                            amount,
                            e
                    );
                    throw e;
                }
            }
        }

        log.error(
                "Transfer failed unexpectedly after retries: senderId={}, recipientId={}, amount={}",
                senderId,
                recipientId,
                amount
        );

        throw new IllegalStateException("Transfer failed after retries");
    }

    public TransactionDto executeTransferWithIban(
            Long senderId,
            String iban,
            BigDecimal amount
    ) {

        log.info(
                "Starting IBAN transfer: senderId={}, iban={}, amount={}",
                senderId,
                iban,
                amount
        );

        byte attempts = 0;

        while (attempts < MAX_RETRIES) {
            try {

                TransactionDto transaction =
                        transferTransactionalService.doTransferWithIbanTransactional(
                                senderId,
                                iban,
                                amount
                        );

                log.info(
                        "IBAN transfer completed successfully: senderId={}, iban={}, amount={}",
                        senderId,
                        iban,
                        amount
                );

                return transaction;

            } catch (OptimisticLockException e) {

                attempts++;

                log.warn(
                        "Optimistic lock conflict during IBAN transfer, retry attempt {}/{}: senderId={}, iban={}, amount={}",
                        attempts,
                        MAX_RETRIES,
                        senderId,
                        iban,
                        amount
                );

                if (attempts == MAX_RETRIES) {
                    log.error(
                            "IBAN transfer failed after {} attempts: senderId={}, iban={}, amount={}",
                            MAX_RETRIES,
                            senderId,
                            iban,
                            amount,
                            e
                    );
                    throw e;
                }
            }
        }

        log.error(
                "IBAN transfer failed unexpectedly after retries: senderId={}, iban={}, amount={}",
                senderId,
                iban,
                amount
        );

        throw new IllegalStateException("Transfer failed after retries");
    }
}