package org.bank.balanceservice.entity;

import org.bank.common.CurrencyCode;
import org.bank.common.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceEntityTest {

    private BalanceEntity balance;

    private static final Long ACCOUNT_ID = 1L;
    private static final CurrencyCode CURRENCY = CurrencyCode.EUR;

    @BeforeEach
    void setUp() {
        balance = new BalanceEntity(
                ACCOUNT_ID,
                money("1000.00"),
                money("0.00")
        );
    }

    @Test
    void credit_shouldIncreaseActualBalance() {
        balance.credit(money("200.00"));

        assertAmountEquals("1200.00", balance.getActual());
        assertAmountEquals("0.00", balance.getReserved());
    }

    @Test
    void debit_shouldDecreaseActualBalance() {
        balance.debit(money("200.00"));

        assertAmountEquals("800.00", balance.getActual());
        assertAmountEquals("0.00", balance.getReserved());
    }

    @Test
    void reserve_shouldIncreaseReservedBalance() {
        balance.reserve(money("300.00"));

        assertAmountEquals("1000.00", balance.getActual());
        assertAmountEquals("300.00", balance.getReserved());
    }

    @Test
    void releaseReserve_shouldDecreaseReservedBalance() {
        balance.reserve(money("300.00"));

        balance.releaseReserve(money("100.00"));

        assertAmountEquals("1000.00", balance.getActual());
        assertAmountEquals("200.00", balance.getReserved());
    }

    @Test
    void commitReserve_shouldDecreaseActualAndReserved() {
        balance.reserve(money("300.00"));

        balance.commitReserve(money("300.00"));

        assertAmountEquals("700.00", balance.getActual());
        assertAmountEquals("0.00", balance.getReserved());
    }

    @Test
    void creditTwice_shouldAccumulateAmounts() {
        balance.credit(money("100.00"));
        balance.credit(money("250.00"));

        assertAmountEquals("1350.00", balance.getActual());
    }

    @Test
    void debitTwice_shouldSubtractAmounts() {
        balance.debit(money("100.00"));
        balance.debit(money("250.00"));

        assertAmountEquals("650.00", balance.getActual());
    }

    @Test
    void reserveTwice_shouldAccumulateReservedAmount() {
        balance.reserve(money("100.00"));
        balance.reserve(money("200.00"));

        assertAmountEquals("1000.00", balance.getActual());
        assertAmountEquals("300.00", balance.getReserved());
    }

    @Test
    void reserveAndRelease_shouldRestoreOriginalState() {
        balance.reserve(money("500.00"));
        balance.releaseReserve(money("500.00"));

        assertAmountEquals("1000.00", balance.getActual());
        assertAmountEquals("0.00", balance.getReserved());
    }

    @Test
    void reserveAndCommit_shouldDecreaseActualOnlyByCommittedAmount() {
        balance.reserve(money("500.00"));

        balance.commitReserve(money("200.00"));

        assertAmountEquals("800.00", balance.getActual());
        assertAmountEquals("300.00", balance.getReserved());
    }

    @Test
    void credit_shouldNotChangeReservedBalance() {
        balance.reserve(money("300.00"));

        balance.credit(money("200.00"));

        assertAmountEquals("1200.00", balance.getActual());
        assertAmountEquals("300.00", balance.getReserved());
    }

    @Test
    void reserve_shouldNotChangeActualBalance() {
        balance.reserve(money("400.00"));

        assertAmountEquals("1000.00", balance.getActual());
    }

    @Test
    void releaseReserve_shouldNotChangeActualBalance() {
        balance.reserve(money("400.00"));

        balance.releaseReserve(money("200.00"));

        assertAmountEquals("1000.00", balance.getActual());
        assertAmountEquals("200.00", balance.getReserved());
    }

    @Test
    void debitAfterReserve_shouldUseOnlyAvailableFunds() {
        balance.reserve(money("800.00"));

        balance.debit(money("150.00"));

        assertAmountEquals("850.00", balance.getActual());
        assertAmountEquals("800.00", balance.getReserved());
    }

    private Money money(String amount) {
        return new Money(new BigDecimal(amount), CURRENCY);
    }

    private void assertAmountEquals(String expected, Money actual) {
        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual.getAmount())
        );
    }
}