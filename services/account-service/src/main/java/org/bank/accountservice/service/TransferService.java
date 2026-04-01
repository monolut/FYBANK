package org.bank.accountservice.service;

import jakarta.persistence.OptimisticLockException;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.entity.BalanceEntity;
import org.bank.accountservice.exception.*;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    @Autowired
    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void executeTransfer(Long senderId, Long recipientId, BigDecimal amount) {
        byte attempts = 0;
        while (attempts < 3) {
            try {
                doTransfer(senderId, recipientId, amount);
            } catch (OptimisticLockException e) {
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
            }
        }
    }

    @Transactional
    public void doTransfer(Long senderId, Long recipientId, BigDecimal amount) {
        if (senderId.equals(recipientId)) {
            throw new SameAccountTransferException(senderId);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountNotPositiveException();
        }

        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));

        AccountEntity recipient = accountRepository.findByIdWithBalance(recipientId)
                .orElseThrow(() -> new AccountNotFoundByIdException(recipientId));

        transfer(sender.getBalance(), recipient.getBalance(), amount);
    }

    @Transactional
    public void transferWithIban(Long senderId, String iban, BigDecimal amount) {
        AccountEntity sender = accountRepository.findByIdWithBalance(senderId)
                .orElseThrow(() -> new AccountNotFoundByIdException(senderId));

        AccountEntity recipient = accountRepository.findByIban(iban)
                        .orElseThrow(() -> new AccountNotFoundByIban(iban));

        transfer(sender.getBalance(), recipient.getBalance(), amount);
    }

    private void transfer(BalanceEntity sender, BalanceEntity recipient, BigDecimal amount) {
        sender.debit(amount);
        recipient.credit(amount);
    }
}
