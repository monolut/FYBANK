package org.bank.accountservice.specifications;

import org.bank.accountservice.entity.AccountEntity;
import org.bank.common.AccountStatus;
import org.bank.common.CurrencyCode;
import org.springframework.data.jpa.domain.Specification;

public final class AccountSpecifications {

    private AccountSpecifications() {}

    public static Specification<AccountEntity> hasUserId(
            Long userId
    ) {
        return(root, query, cb) ->
                cb.equal(root.get("userId"), userId);
    }

    public static Specification<AccountEntity> hasCurrency(
            CurrencyCode currencyCode
    ) {
        return (root, query, cb) ->
                cb.equal(root.get("currency"), currencyCode);
    }

    public static Specification<AccountEntity> hasStatus(
            AccountStatus status
    ) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}
