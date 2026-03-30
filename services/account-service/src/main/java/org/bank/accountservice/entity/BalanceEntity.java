package org.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bank.accountservice.enums.AccountStatus;
import org.bank.accountservice.exception.AccountBlockedException;
import org.bank.accountservice.exception.AmountNotPositiveException;
import org.bank.accountservice.exception.InsufficientFundsException;
import org.bank.accountservice.exception.InsufficientReservedFundsException;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "balances")
public class BalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "balance")
    private AccountEntity account;

    private BigDecimal actual;

    private BigDecimal available;

    private BigDecimal reserved;

    @Version
    private Long version;

    public BalanceEntity(BigDecimal actual, BigDecimal reserved) {
        this.actual = actual != null ? actual : BigDecimal.ZERO;
        this.reserved = reserved != null ? reserved : BigDecimal.ZERO;
        this.available = this.actual.subtract(this.reserved);
    }

    public void credit(BigDecimal amount) {
        checkStatus();
        validatePositive(amount);
        actual = actual.add(amount);
        recalcAvailable();
    }

    public void debit(BigDecimal amount) {
        checkActiveAndSufficient(amount, false);
        actual = actual.subtract(amount);
        recalcAvailable();
    }

    public void reserve(BigDecimal amount) {
        checkActiveAndSufficient(amount, false);
        reserved = reserved.add(amount);
        recalcAvailable();
    }

    public void releaseReserve(BigDecimal amount) {
        checkActiveAndSufficient(amount, true);
        reserved = reserved.subtract(amount);
        recalcAvailable();
    }

    public void commitReserve(BigDecimal amount) {
        checkActiveAndSufficient(amount, true);
        reserved = reserved.subtract(amount);
        actual = actual.subtract(amount);
        recalcAvailable();
    }

    private void recalcAvailable() {
        available = actual.subtract(reserved);
    }

    private void checkStatus() {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException(account.getId());
        }
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountNotPositiveException();
        }
    }

    private void checkActiveAndSufficient(BigDecimal amount, boolean reserved) {
        checkStatus();
        validatePositive(amount);
        if (
                reserved ? this.reserved.compareTo(amount) < 0 : this.available.compareTo(amount) < 0
        ) {
            throw reserved ? new InsufficientReservedFundsException(account.getId())
                    : new InsufficientFundsException(account.getId());
        }
    }
}
