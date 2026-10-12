package org.bank.transferservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.*;
import org.bank.common.exception.AmountNotPositiveException;
import org.bank.common.exception.CurrencyMismatchException;
import org.bank.common.exception.ForbiddenException;
import org.bank.common.exception.SameAccountTransferException;
import org.bank.transferservice.client.*;
import org.bank.transferservice.dto.TransferResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferTransactionalService {

    private final AccountClient accountClient;
    private final IdempotencyClient idempotencyClient;
    private final AuthCommonService authCommonService;
    private final TransactionClient transactionClient;
    private final CurrencyClient currencyClient;
    private final BalanceClient balanceClient;

    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionDto doTransferTransactional(
            String idempotencyKey,
            Long senderId,
            Long recipientId,
            String description,
            Money money
    ) {
        Optional<TransactionDto> existing = idempotencyClient.findTransaction(idempotencyKey, senderId);

        if(existing.isPresent()) {
            log.info(
                    "Idempotent request detected: accountId={}, key={}, transactionId={}",
                    senderId,
                    idempotencyKey,
                    existing.get().getId()
            );

            return existing.get();
        }

        BigDecimal amount = money.getAmount();

        log.info(
                "Starting transfer: senderId={}, recipientId={}, amount={}",
                senderId,
                recipientId,
                amount
        );

        validateTransfer(senderId, recipientId, amount);

        log.debug("Transfer parameters validated");

        AccountDto sender = accountClient.getAccountById(senderId);

        AccountDto recipient = accountClient.getAccountById(recipientId);

        log.debug(
                "Transfer accounts found: senderId={}, recipientId={}",
                senderId,
                recipientId
        );

        validateAccountOwnership(sender);

        TransactionDto transaction;
        TransferResult transferResult = null;

        try {

            log.debug(
                    "Transferring balance: senderId={}, recipientId={}, amount={}",
                    senderId,
                    recipientId,
                    amount
            );

            transferResult = transferBalances(
                    sender.getBalance(),
                    recipient.getBalance(),
                    money
            );

            log.debug(
                    "Balances successfully updated: senderId={}, recipientId={}",
                    senderId,
                    recipientId
            );

            transaction = transactionClient.recordTransaction(
                    new RecordTransactionRequest(
                            senderId,
                            recipientId,
                            transferResult.senderAmount(),
                            transferResult.recipientAmount(),
                            transferResult.exchangeRate(),
                            description,
                            TransactionType.TRANSFER,
                            TransactionStatus.COMPLETED
                    )
            );

            idempotencyClient.saveTransaction(
                    new SaveTransactionRequest(
                            idempotencyKey,
                            senderId,
                            transaction.getId()
                    )
            );

            log.info(
                    "Transfer completed successfully: senderId={}, recipientId={}, amount={}, transactionId={}",
                    senderId,
                    recipientId,
                    amount,
                    transaction.getId()
            );

        } catch (Exception e) {

            log.error(
                    "Transfer failed: senderId={}, recipientId={}, amount={}, reason={}",
                    senderId,
                    recipientId,
                    amount,
                    e.getMessage(),
                    e
            );

            Money recipientAmount = money;
            BigDecimal exchangeRate = BigDecimal.ONE;

            if (transferResult != null) {
                recipientAmount = transferResult.recipientAmount();
                exchangeRate = transferResult.exchangeRate();
            }

            transaction = transactionClient.recordTransaction(
                    new RecordTransactionRequest(
                            senderId,
                            recipientId,
                            money,
                            recipientAmount,
                            exchangeRate,
                            description,
                            TransactionType.TRANSFER,
                            TransactionStatus.FAILED
                    )
            );

            idempotencyClient.saveTransaction(
                    new SaveTransactionRequest(
                            idempotencyKey,
                            senderId,
                            transaction.getId()
                    )
            );

            log.warn(
                    "Failed transfer recorded: senderId={}, recipientId={}, transactionId={}, exchangeRate={}",
                    senderId,
                    recipientId,
                    transaction.getId(),
                    exchangeRate
            );

            throw e;
        }

        return transaction;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionDto doTransferWithIbanTransactional(
            String idempotencyKey,
            Long senderId,
            String iban,
            String description,
            Money money
    ) {

        Optional<TransactionDto> existing = idempotencyClient.findTransaction(idempotencyKey, senderId);

        if(existing.isPresent()) {
            log.info(
                    "Idempotent request detected: accountId={}, key={}, transactionId={}",
                    senderId,
                    idempotencyKey,
                    existing.get().getId()
            );

            return existing.get();
        }

        BigDecimal amount = money.getAmount();

        log.info(
                "Starting IBAN transfer: senderId={}, amount={}",
                senderId,
                amount
        );

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountNotPositiveException();
        }

        log.debug("IBAN transfer amount validated");

        AccountDto sender = accountClient.getAccountById(senderId);
        AccountDto recipient = accountClient.getAccountByIban(iban);

        log.debug(
                "IBAN transfer accounts found: senderId={}, recipientId={}",
                senderId,
                recipient.getId()
        );

        validateAccountOwnership(sender);

        if (sender.getId().equals(recipient.getId())) {
            throw new SameAccountTransferException(senderId);
        }

        TransactionDto transaction;
        TransferResult transferResult = null;

        try {

            log.debug(
                    "Transferring balance via IBAN: senderId={}, recipientId={}, amount={}",
                    senderId,
                    recipient.getId(),
                    amount
            );

            transferResult = transferBalances(
                    sender.getBalance(),
                    recipient.getBalance(),
                    money
            );

            transaction = transactionClient.recordTransaction(
                    new RecordTransactionRequest(
                            senderId,
                            recipient.getId(),
                            transferResult.senderAmount(),
                            transferResult.recipientAmount(),
                            transferResult.exchangeRate(),
                            description,
                            TransactionType.TRANSFER,
                            TransactionStatus.COMPLETED
                    )
            );

            idempotencyClient.saveTransaction(
                    new SaveTransactionRequest(
                            idempotencyKey,
                            senderId,
                            transaction.getId()
                    )
            );

            log.info(
                    "IBAN transfer completed successfully: senderId={}, recipientId={}, amount={}, transactionId={}",
                    senderId,
                    recipient.getId(),
                    amount,
                    transaction.getId()
            );

        } catch (Exception e) {

            log.error(
                    "IBAN transfer failed: senderId={}, recipientId={}, amount={}, reason={}",
                    senderId,
                    recipient.getId(),
                    amount,
                    e.getMessage(),
                    e
            );

            Money recipientAmount = money;
            BigDecimal exchangeRate = BigDecimal.ONE;

            if (transferResult != null) {
                recipientAmount = transferResult.recipientAmount();
                exchangeRate = transferResult.exchangeRate();
            }

            transaction = transactionClient.recordTransaction(
                    new RecordTransactionRequest(
                            senderId,
                            recipient.getId(),
                            money,
                            recipientAmount,
                            exchangeRate,
                            description,
                            TransactionType.TRANSFER,
                            TransactionStatus.FAILED
                    )
            );

            idempotencyClient.saveTransaction(
                    new SaveTransactionRequest(
                            idempotencyKey,
                            senderId,
                            transaction.getId()
                    )
            );

            log.warn(
                    "Failed IBAN transfer recorded: senderId={}, recipientId={}, transactionId={}, exchangeRate={}",
                    senderId,
                    recipient.getId(),
                    transaction.getId(),
                    exchangeRate
            );

            throw e;
        }

        return transaction;
    }

    private void validateAccountOwnership(AccountDto account) {

        Long userId = authCommonService.getUserId();

        log.debug(
                "Validating account ownership: accountId={}, userId={}",
                account.getId(),
                userId
        );

        if (!account.getUserId().equals(userId)) {
            log.warn(
                    "Account ownership validation failed: accountId={}, userId={}",
                    account.getId(),
                    userId
            );

            throw new ForbiddenException("Account does not belong to user");
        }

        log.debug(
                "Account ownership validated successfully: accountId={}, userId={}",
                account.getId(),
                userId
        );
    }

    private void validateTransfer(
            Long senderId,
            Long recipientId,
            BigDecimal amount
    ) {

        log.debug(
                "Validating transfer parameters: senderId={}, recipientId={}, amount={}",
                senderId,
                recipientId,
                amount
        );

        if (senderId.equals(recipientId)) {
            throw new SameAccountTransferException(senderId);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountNotPositiveException();
        }

        log.debug(
                "Sender account ownership validated: senderId={}, userId={}",
                senderId,
                authCommonService.getUserId()
        );
    }

    private TransferResult transferBalances(
            BalanceDto senderBalance,
            BalanceDto recipientBalance,
            Money senderAmount
    ) {

        CurrencyCode senderCurrency =
                senderBalance.getActual().getCurrency();

        CurrencyCode recipientCurrency =
                recipientBalance.getActual().getCurrency();

        if (!senderAmount.getCurrency().equals(senderCurrency)) {
            throw new CurrencyMismatchException(
                    senderAmount.getCurrency(),
                    senderCurrency
            );
        }

        ConversionResult conversionResult =
                currencyClient.convert(
                        new ConversionRequest(
                                senderAmount,
                                recipientCurrency
                        )
                );

        Money recipientAmount = conversionResult.money();

        BigDecimal exchangeRate = conversionResult.exchangeRate();

        log.info(
                "Currency conversion: {} {} -> {} {}, rate={}",
                senderAmount.getAmount(),
                senderCurrency,
                recipientAmount.getAmount(),
                recipientCurrency,
                exchangeRate
        );

        balanceClient.debit(
                senderBalance.getAccountId(),
                new AmountRequest(
                        senderAmount.getAmount(),
                        senderAmount.getCurrency()
                )
        );

        balanceClient.credit(
                recipientBalance.getAccountId(),
                new AmountRequest(
                        recipientAmount.getAmount(),
                        recipientAmount.getCurrency()
                )
        );

        log.debug(
                "Balances successfully updated: sender={} {}, recipient={} {}, rate={}",
                senderAmount.getAmount(),
                senderCurrency,
                recipientAmount.getAmount(),
                recipientCurrency,
                exchangeRate
        );

        return new TransferResult(
                senderAmount,
                recipientAmount,
                exchangeRate
        );
    }
}