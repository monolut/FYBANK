package org.bank.transactionservice.specification;

import org.bank.transactionservice.entity.TransactionEntity;
import org.bank.common.TransactionStatus;
import org.bank.common.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class TransactionSpecifications {

    private TransactionSpecifications() {
    }

    public static Specification<TransactionEntity> hasSenderAccountId(Long accountId) {
        return (root, query, cb) ->
                cb.equal(root.get("senderAccountId"), accountId);
    }

    public static Specification<TransactionEntity> hasRecipientAccountId(Long accountId) {
        return (root, query, cb) ->
                cb.equal(root.get("recipientAccountId"), accountId);
    }

    public static Specification<TransactionEntity> hasStatus(TransactionStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<TransactionEntity> hasType(TransactionType type) {
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }

    public static Specification<TransactionEntity> createdAfter(Instant date) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<TransactionEntity> createdBefore(Instant date) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}