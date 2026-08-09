package org.bank.accountservice.service;

import lombok.extern.slf4j.Slf4j;
import org.bank.accountservice.dto.TransactionDto;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.entity.BalanceEntity;
import org.bank.accountservice.entity.TransactionEntity;
import org.bank.accountservice.enums.TransactionStatus;
import org.bank.accountservice.enums.TransactionType;
import org.bank.accountservice.exception.*;
import org.bank.accountservice.mapper.TransactionMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.bank.authcommon.service.AuthCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransferTransactionalService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuthCommonService authCommonService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransferTransactionalService(
            AccountRepository accountRepository,
            TransactionService transactionService,
            AuthCommonService authCommonService,
            TransactionMapper transactionMapper
    ) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.authCommonService = authCommonService;
        this.transactionMapper = transactionMapper;

        log.info("TransferTransactionalService initialized");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionDto doTransferTransactional(
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

        validateTransfer(senderId, recipientId, amount);

        log.debug("Transfer parameters validated");

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));

        AccountEntity recipient = accountRepository.findByIdWithBalance(recipientId)
                .orElseThrow(() -> new AccountNotFoundByIdException(recipientId));

        log.debug(
                "Transfer accounts found: senderId={}, recipientId={}",
                senderId,
                recipientId
        );

        validateAccountOwnership(sender);

        log.debug(
                "Sender account ownership validated: senderId={}, userId={}",
                senderId,
                authCommonService.getUserId()
        );

        TransactionEntity transaction;

        try {

            log.debug(
                    "Transferring balance: senderId={}, recipientId={}, amount={}",
                    senderId,
                    recipientId,
                    amount
            );

            transferBalances(
                    sender.getBalance(),
                    recipient.getBalance(),
                    amount
            );

            log.debug(
                    "Balances successfully updated: senderId={}, recipientId={}",
                    senderId,
                    recipientId
            );

            transaction = transactionService.recordTransaction(
                    senderId,
                    recipientId,
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.SUCCESS
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

            transaction = transactionService.recordTransaction(
                    senderId,
                    recipientId,
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );

            log.warn(
                    "Failed transfer recorded: senderId={}, recipientId={}, transactionId={}",
                    senderId,
                    recipientId,
                    transaction.getId()
            );

            throw e;
        }

        return transactionMapper.toDto(transaction);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionDto doTransferWithIbanTransactional(
            Long senderId,
            String iban,
            BigDecimal amount
    ) {

        log.info(
                "Starting IBAN transfer: senderId={}, amount={}",
                senderId,
                amount
        );

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountNotPositiveException();
        }

        log.debug("IBAN transfer amount validated");

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));

        AccountEntity recipient = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundByIban(iban));

        log.debug(
                "IBAN transfer accounts found: senderId={}, recipientId={}",
                senderId,
                recipient.getId()
        );

        validateAccountOwnership(sender);

        log.debug(
                "Sender account ownership validated: senderId={}, userId={}",
                senderId,
                authCommonService.getUserId()
        );

        if (sender.getId().equals(recipient.getId())) {
            throw new SameAccountTransferException(senderId);
        }

        TransactionEntity transaction;

        try {

            log.debug(
                    "Transferring balance via IBAN: senderId={}, recipientId={}, amount={}",
                    senderId,
                    recipient.getId(),
                    amount
            );

            transferBalances(
                    sender.getBalance(),
                    recipient.getBalance(),
                    amount
            );

            transaction = transactionService.recordTransaction(
                    senderId,
                    recipient.getId(),
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.SUCCESS
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

            transaction = transactionService.recordTransaction(
                    senderId,
                    recipient.getId(),
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );

            log.warn(
                    "Failed IBAN transfer recorded: senderId={}, recipientId={}, transactionId={}",
                    senderId,
                    recipient.getId(),
                    transaction.getId()
            );

            throw e;
        }

        return transactionMapper.toDto(transaction);
    }

    private void validateAccountOwnership(AccountEntity account) {

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

        log.debug("Transfer parameters validated successfully");
    }

    private void transferBalances(
            BalanceEntity senderBalance,
            BalanceEntity recipientBalance,
            BigDecimal amount
    ) {

        log.debug(
                "Updating balances for transfer, amount={}",
                amount
        );

        senderBalance.debit(amount);
        recipientBalance.credit(amount);

        log.debug("Balances successfully updated");
    }
}