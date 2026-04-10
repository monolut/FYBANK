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
    }

    public TransactionDto executeTransfer(Long senderId, Long recipientId, BigDecimal amount) {
        byte attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                TransactionDto transaction = transferTransactionalService.doTransferTransactional(senderId, recipientId, amount);
                return transaction;
            } catch (OptimisticLockException e) {
                attempts++;
                log.warn(
                        "Retry attempt {} for transfer: senderId={}, recipientId={}, amount={}",
                        attempts,
                        senderId,
                        recipientId,
                        amount
                );
                if (attempts == 3) throw e;
            }
        }
        throw new IllegalStateException("Transfer failed after retries");
    }

    public TransactionDto executeTransferWithIban(Long senderId, String iban, BigDecimal amount) {
        byte attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                TransactionDto transaction = transferTransactionalService.doTransferWithIbanTransactional(senderId, iban, amount);
                return transaction;
            } catch (OptimisticLockException e) {
                attempts++;
                log.warn(
                        "Retry attempt {} for transfer: senderId={}, iban={}, amount={}",
                        attempts,
                        senderId,
                        iban,
                        amount
                );
                if (attempts == 3) throw e;
            }
        }
        throw new IllegalStateException("Transfer failed after retries");
    }
}