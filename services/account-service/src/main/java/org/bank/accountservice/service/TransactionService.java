package org.bank.accountservice.service;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        log.info("TransactionService initialized");
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

        log.info(
                "Recording transaction: senderId={}, recipientId={}, amount={}, currency={}, type={}, status={}",
                senderId,
                recipientId,
                amount,
                currency,
                type,
                status
        );

        TransactionEntity transaction = new TransactionEntity(
                senderId,
                recipientId,
                amount,
                currency,
                type,
                status,
                transactionReferenceGenerator.generate()
        );

        TransactionEntity saved = transactionRepository.save(transaction);

        log.info(
                "Transaction successfully recorded: transactionId={}, senderId={}, recipientId={}, type={}, status={}",
                saved.getId(),
                senderId,
                recipientId,
                type,
                status
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getHistory(Long accountId) {

        log.debug(
                "Fetching transaction history for accountId={}",
                accountId
        );

        List<TransactionDto> transactions =
                transactionRepository
                        .findBySenderAccountIdOrRecipientAccountId(
                                accountId,
                                accountId
                        )
                        .stream()
                        .map(transactionMapper::toDto)
                        .toList();

        log.debug(
                "Transaction history fetched for accountId={}, transactionCount={}",
                accountId,
                transactions.size()
        );

        return transactions;
    }
}