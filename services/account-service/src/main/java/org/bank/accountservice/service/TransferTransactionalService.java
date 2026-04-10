package org.bank.accountservice.service;

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
            AuthCommonService authCommonService, TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.authCommonService = authCommonService;
        this.transactionMapper = transactionMapper;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionDto doTransferTransactional(Long senderId, Long recipientId, BigDecimal amount) {
        validateTransfer(senderId, recipientId, amount);

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));
        AccountEntity recipient = accountRepository.findByIdWithBalance(recipientId)
                .orElseThrow(() -> new AccountNotFoundByIdException(recipientId));

        validateAccountOwnership(sender);

        TransactionEntity transaction;

        try {
            transferBalances(sender.getBalance(), recipient.getBalance(), amount);
            transaction = transactionService.recordTransaction(
                    senderId,
                    recipientId,
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.SUCCESS
            );
        } catch (Exception e) {
            transaction = transactionService.recordTransaction(
                    senderId,
                    recipientId,
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );
            throw e;
        }
        return transactionMapper.toDto(transaction);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionDto doTransferWithIbanTransactional(Long senderId, String iban, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new AmountNotPositiveException();

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));
        AccountEntity recipient = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundByIban(iban));

        validateAccountOwnership(sender);

        if (sender.getId().equals(recipient.getId()))
            throw new SameAccountTransferException(senderId);

        TransactionEntity transaction;
        try {
            transferBalances(sender.getBalance(), recipient.getBalance(), amount);
            transaction = transactionService.recordTransaction(
                    senderId,
                    recipient.getId(),
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.SUCCESS
            );
        } catch (Exception e) {
            transaction = transactionService.recordTransaction(
                    senderId,
                    recipient.getId(),
                    amount,
                    sender.getCurrency(),
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );
            throw e;
        }
        return transactionMapper.toDto(transaction);
    }

    private void validateAccountOwnership(AccountEntity account) {
        if (!account.getUserId().equals(authCommonService.getUserId())) {
            throw new ForbiddenException("Account does not belong to user");
        }
    }

    private void validateTransfer(Long senderId, Long recipientId, BigDecimal amount) {
        if (senderId.equals(recipientId))
            throw new SameAccountTransferException(senderId);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new AmountNotPositiveException();
    }

    private void transferBalances(BalanceEntity senderBalance, BalanceEntity recipientBalance, BigDecimal amount) {
        senderBalance.debit(amount);
        recipientBalance.credit(amount);
    }
}
