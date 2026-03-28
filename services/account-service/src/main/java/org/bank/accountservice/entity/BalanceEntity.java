package org.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bank.accountservice.exception.InsufficientFundsException;
import org.bank.accountservice.exception.InsufficientReservedFundsException;

import java.math.BigDecimal;
import java.util.Currency;

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

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    @Version
    private Long version;

    public BalanceEntity(BigDecimal actual, BigDecimal reserved, Currency currency) {
        this.actual = actual;
        this.reserved = reserved;
        this.available = actual.subtract(reserved);
        this.currency = currency;
    }

    public void credit(BigDecimal amount) {
        actual = actual.add(amount);
        recalcAvailable();
    }

    public void debit(BigDecimal amount) {
        if (available.compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getId());
        }
        actual = actual.subtract(amount);
        recalcAvailable();
    }

    public void reserve(BigDecimal amount) {
        if (available.compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getId());
        }

        reserved = reserved.add(amount);
        recalcAvailable();
    }

    public void releaseReserve(BigDecimal amount) {
        reserved = reserved.subtract(amount);
        recalcAvailable();
    }

    public void commitReserve(BigDecimal amount) {
        if (reserved.compareTo(amount) < 0) {
            throw new InsufficientReservedFundsException(account.getId());
        }

        reserved = reserved.subtract(amount);
        actual = actual.subtract(amount);
        recalcAvailable();
    }

    private void recalcAvailable() {
        available = actual.subtract(reserved);
    }
}
