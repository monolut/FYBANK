package org.bank.accountservice.service;

import jakarta.persistence.OptimisticLockException;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.entity.BalanceEntity;
import org.bank.accountservice.enums.TransactionStatus;
import org.bank.accountservice.enums.TransactionType;
import org.bank.accountservice.exception.*;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Autowired
    public TransferService(
            AccountRepository accountRepository,
            TransactionService transactionService
    ) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    public void executeTransfer(Long senderId, Long recipientId, BigDecimal amount) {
        byte attempts = 0;
        while (attempts < 3) {
            try {
                doTransferTransactional(senderId, recipientId, amount);
                return;
            } catch (OptimisticLockException e) {
                attempts++;
                if (attempts == 3) throw e;
            }
        }
    }

    public void executeTransferWithIban(Long senderId, String iban, BigDecimal amount) {
        byte attempts = 0;
        while (attempts < 3) {
            try {
                doTransferWithIbanTransactional(senderId, iban, amount);
                return;
            } catch (OptimisticLockException e) {
                attempts++;
                if (attempts == 3) throw e;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void doTransferTransactional(Long senderId, Long recipientId, BigDecimal amount) {
        validateTransfer(senderId, recipientId, amount);

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));
        AccountEntity recipient = accountRepository.findByIdWithBalance(recipientId)
                .orElseThrow(() -> new AccountNotFoundByIdException(recipientId));

        try {
            transferBalances(sender.getBalance(), recipient.getBalance(), amount);
            transactionService.recordTransaction(senderId, recipientId, amount, sender.getCurrency(),
                    TransactionType.TRANSFER, TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transactionService.recordTransaction(senderId, recipientId, amount, sender.getCurrency(),
                    TransactionType.TRANSFER, TransactionStatus.FAILED);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void doTransferWithIbanTransactional(Long senderId, String iban, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new AmountNotPositiveException();

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));
        AccountEntity recipient = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundByIban(iban));

        if (sender.getId().equals(recipient.getId()))
            throw new SameAccountTransferException(senderId);

        try {
            transferBalances(sender.getBalance(), recipient.getBalance(), amount);
            transactionService.recordTransaction(senderId, recipient.getId(), amount, sender.getCurrency(),
                    TransactionType.TRANSFER, TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transactionService.recordTransaction(senderId, recipient.getId(), amount, sender.getCurrency(),
                    TransactionType.TRANSFER, TransactionStatus.FAILED);
            throw e;
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