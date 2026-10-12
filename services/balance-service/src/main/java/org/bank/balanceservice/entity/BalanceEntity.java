package org.bank.balanceservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.AccountStatus;
import org.bank.common.Money;
import org.bank.common.exception.AccountBlockedException;
import org.bank.common.exception.AmountNotPositiveException;
import org.bank.common.exception.InsufficientFundsException;
import org.bank.common.exception.InsufficientReservedFundsException;

import java.math.BigDecimal;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "balances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_balance_account_currency",
                        columnNames = "account_id"
                )
        }
)
public class BalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "account_id",
            nullable = false
    )
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "actual_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "currency",
                            nullable = false
                    )
            )
    })
    private Money actual;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "available_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "currency",
                            nullable = false,
                            insertable = false,
                            updatable = false
                    )
            )
    })
    private Money available;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "reserved_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "currency",
                            nullable = false,
                            insertable = false,
                            updatable = false
                    )
            )
    })
    private Money reserved;

    @Version
    private Long version;

    public BalanceEntity(
            Long accountId,
            Money actual,
            Money reserved
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        if (actual == null) {
            throw new IllegalArgumentException("Actual balance cannot be null");
        }

        this.accountId = accountId;
        this.actual = actual;

        this.accountStatus = AccountStatus.ACTIVE;

        this.reserved = reserved != null
                ? reserved
                : Money.zero(actual.getCurrency());

        validateState();
        recalculateAvailable();
    }

    public void credit(Money money) {
        checkActive();
        validatePositive(money);

        actual = actual.add(money);

        recalculateAvailable();
        validateState();
    }

    public void debit(Money money) {
        log.info(
                "DEBIT: balance={} {}, amount={} {}",
                actual.getAmount(),
                actual.getCurrency(),
                money.getAmount(),
                money.getCurrency()
        );
        checkActive();
        validatePositive(money);
        checkAvailableFunds(money);

        actual = actual.subtract(money);

        recalculateAvailable();
        validateState();
    }

    public void reserve(Money money) {
        checkActive();
        validatePositive(money);
        checkAvailableFunds(money);

        reserved = reserved.add(money);

        recalculateAvailable();
        validateState();
    }

    public void releaseReserve(Money money) {
        checkActive();
        validatePositive(money);
        checkReservedFunds(money);

        reserved = reserved.subtract(money);

        recalculateAvailable();
        validateState();
    }

    public void commitReserve(Money money) {
        checkActive();
        validatePositive(money);
        checkReservedFunds(money);

        reserved = reserved.subtract(money);
        actual = actual.subtract(money);

        recalculateAvailable();
        validateState();
    }

    private void checkActive() {
        if (accountStatus != AccountStatus.ACTIVE) {
            throw new AccountBlockedException(accountId);
        }
    }

    private void validatePositive(Money money) {
        BigDecimal amount = money.getAmount();
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new AmountNotPositiveException();
        }
    }

    private void checkAvailableFunds(Money money) {
        log.info(
                "DEBIT AVAILABLE: balance={} {}, amount={} {}",
                actual.getAmount(),
                actual.getCurrency(),
                money.getAmount(),
                money.getCurrency()
        );
        BigDecimal amount = money.getAmount();
        if (available.getAmount().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    accountId
            );
        }
    }

    private void checkReservedFunds(Money money) {
        BigDecimal amount = money.getAmount();
        if (reserved.getAmount().compareTo(amount) < 0) {
            throw new InsufficientReservedFundsException(
                    accountId
            );
        }
    }

    private void recalculateAvailable() {
        available = actual.subtract(reserved);
    }

    private void validateState() {
        if (actual.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Actual balance cannot be negative"
            );
        }

        if (reserved.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Reserved balance cannot be negative"
            );
        }

        if (reserved.getAmount().compareTo(actual.getAmount()) > 0) {
            throw new IllegalStateException(
                    "Reserved balance cannot exceed actual balance"
            );
        }
    }
}