package org.bank.accountservice.service;

import org.bank.accountservice.dto.TransactionDto;
import org.bank.accountservice.entity.TransactionEntity;
import org.bank.accountservice.enums.TransactionStatus;
import org.bank.accountservice.enums.TransactionType;
import org.bank.accountservice.mapper.TransactionMapper;
import org.bank.accountservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionReferenceGenerator transactionReferenceGenerator;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            TransactionReferenceGenerator transactionReferenceGenerator
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionReferenceGenerator = transactionReferenceGenerator;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionEntity recordTransaction(
            Long senderId,
            Long recipientId,
            BigDecimal amount,
            Currency currency,
            TransactionType type,
            TransactionStatus status
    ) {
        TransactionEntity transaction = new TransactionEntity(
                senderId,
                recipientId,
                amount,
                currency,
                type,
                status,
                transactionReferenceGenerator.generate()
        );

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getHistory(Long accountId) {
        return transactionRepository.findBySenderAccountIdOrRecipientAccountId(accountId, accountId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();

    }

}
