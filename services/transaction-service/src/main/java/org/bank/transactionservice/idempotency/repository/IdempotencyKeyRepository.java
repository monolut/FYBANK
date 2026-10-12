package org.bank.transactionservice.idempotency.repository;

import org.bank.transactionservice.idempotency.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, Long> {

    Optional<IdempotencyKeyEntity> findByIdempotencyKeyAndAccountId(String idempotencyKey, Long accountId);
}
