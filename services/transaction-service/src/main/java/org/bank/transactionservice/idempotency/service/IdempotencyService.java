package org.bank.transactionservice.idempotency.service;

import lombok.RequiredArgsConstructor;
import org.bank.common.TransactionDto;
import org.bank.common.exception.TransactionNotFoundByIdException;
import org.bank.transactionservice.entity.TransactionEntity;
import org.bank.transactionservice.idempotency.entity.IdempotencyKeyEntity;
import org.bank.transactionservice.idempotency.repository.IdempotencyKeyRepository;
import org.bank.transactionservice.mapper.TransactionMapper;
import org.bank.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    public Optional<TransactionDto> findTransaction(
            String idempotencyKey,
            Long accountId
    ) {
        return idempotencyKeyRepository
                .findByIdempotencyKeyAndAccountId(idempotencyKey, accountId)
                .map(IdempotencyKeyEntity::getTransaction)
                .map(transactionMapper::toDto);
    }

    @Transactional
    public void save(
            String idempotencyKey,
            Long userId,
            Long transactionId
    ) {
        TransactionEntity transaction = transactionRepository.findById(transactionId)
                        .orElseThrow(() -> new TransactionNotFoundByIdException(transactionId));
        idempotencyKeyRepository.save(
                new IdempotencyKeyEntity(
                        idempotencyKey,
                        userId,
                        transaction
                )
        );
    }
}