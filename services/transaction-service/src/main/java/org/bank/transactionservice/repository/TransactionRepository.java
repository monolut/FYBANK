package org.bank.transactionservice.repository;

import org.bank.transactionservice.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findBySenderAccountIdOrRecipientAccountId(
            Long senderId,
            Long recipientId
    );

    Page<TransactionEntity> findBySenderAccountIdOrRecipientAccountId(
            Long senderId,
            Long recipientId,
            Pageable pageable
    );
}
