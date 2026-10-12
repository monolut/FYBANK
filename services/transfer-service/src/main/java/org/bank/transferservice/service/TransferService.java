package org.bank.transferservice.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.CurrencyCode;
import org.bank.common.Money;
import org.bank.common.TransactionDto;
import org.bank.common.exception.CurrencyNotFoundByAccountId;
import org.bank.transferservice.client.AccountClient;
import org.bank.transferservice.dto.IbanTransferRequest;
import org.bank.transferservice.dto.InternalTransferRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferTransactionalService transferTransactionalService;
    private final AccountClient accountClient;
    private static final int MAX_RETRIES = 3;

    public TransactionDto executeTransfer(
            String idempotencyKey,
            InternalTransferRequest request
    ) {
        Long senderId = request.getSenderId();
        Long recipientId = request.getRecipientId();
        BigDecimal amount = request.getAmount();
        String description = request.getDescription();
        CurrencyCode currencyCode = accountClient.getAccountCurrency(senderId);

        Money money = new Money(
                amount,
                currencyCode
        );

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
                                idempotencyKey,
                                senderId,
                                recipientId,
                                description,
                                money
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
            String idempotencyKey,
            IbanTransferRequest request
    ) {
        Long senderId = request.getSenderId();
        String iban = request.getIban();
        BigDecimal amount = request.getAmount();
        String description = request.getDescription();
        CurrencyCode currencyCode = accountClient.getAccountCurrency(senderId);

        Money money = new Money(
                amount,
                currencyCode
        );

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
                                idempotencyKey,
                                senderId,
                                iban,
                                description,
                                money
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