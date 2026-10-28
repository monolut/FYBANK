package org.bank.balanceservice.service;

import org.bank.balanceservice.entity.BalanceEntity;
import org.bank.balanceservice.mapper.BalanceMapper;
import org.bank.balanceservice.repository.BalanceRepository;
import org.bank.common.BalanceDto;
import org.bank.common.CurrencyCode;
import org.bank.common.Money;
import org.bank.common.exception.AccountBalanceNotZeroException;
import org.bank.common.exception.BalanceNotFoundByAccountIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    private BalanceService balanceService;

    private static final Long ACCOUNT_ID = 1L;
    private static final CurrencyCode CURRENCY = CurrencyCode.EUR;

    @BeforeEach
    void setUp() {
        balanceService = new BalanceService(balanceRepository, balanceMapper);
    }

    @Test
    void createInitialBalance_shouldSaveBalanceWith1000() {
        balanceService.createInitialBalance(ACCOUNT_ID, CURRENCY);

        ArgumentCaptor<BalanceEntity> captor =
                ArgumentCaptor.forClass(BalanceEntity.class);

        verify(balanceRepository).save(captor.capture());

        BalanceEntity saved = captor.getValue();

        assertEquals(ACCOUNT_ID, saved.getAccountId());
        assertAmountEquals("1000.00", saved.getActual());
        assertEquals(CURRENCY, saved.getActual().getCurrency());
    }

    @Test
    void deleteBalance_shouldDeleteWhenAmountsAreZero() {
        BalanceEntity balance = mock(BalanceEntity.class);

        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.of(balance));
        when(balance.getActual()).thenReturn(money("0.00"));
        when(balance.getReserved()).thenReturn(money("0.00"));

        balanceService.deleteBalanceByAccountId(ACCOUNT_ID);

        verify(balanceRepository).delete(balance);
    }

    @Test
    void deleteBalance_shouldThrowWhenNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.deleteBalanceByAccountId(ACCOUNT_ID)
        );

        verify(balanceRepository, never()).delete(any(BalanceEntity.class));
    }

    @Test
    void deleteBalance_shouldThrowWhenActualIsNotZero() {
        BalanceEntity balance = mock(BalanceEntity.class);

        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.of(balance));
        when(balance.getActual()).thenReturn(money("100.00"));

        assertThrows(
                AccountBalanceNotZeroException.class,
                () -> balanceService.deleteBalanceByAccountId(ACCOUNT_ID)
        );

        verify(balanceRepository, never()).delete(any(BalanceEntity.class));
    }

    @Test
    void deleteBalance_shouldThrowWhenReservedIsNotZero() {
        BalanceEntity balance = mock(BalanceEntity.class);

        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.of(balance));
        when(balance.getActual()).thenReturn(money("0.00"));
        when(balance.getReserved()).thenReturn(money("50.00"));

        assertThrows(
                AccountBalanceNotZeroException.class,
                () -> balanceService.deleteBalanceByAccountId(ACCOUNT_ID)
        );

        verify(balanceRepository, never()).delete(any(BalanceEntity.class));
    }

    @Test
    void deleteAllBalances_shouldCallRepository() {
        List<Long> ids = List.of(1L, 2L, 3L);

        balanceService.deleteAllBalances(ids);

        verify(balanceRepository).deleteByAccountIds(ids);
    }

    @Test
    void getBalance_shouldReturnMappedDto() {
        BalanceEntity balance = mock(BalanceEntity.class);
        BalanceDto dto = mock(BalanceDto.class);

        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(dto);

        BalanceDto result = balanceService.getBalanceByAccountId(ACCOUNT_ID);

        assertSame(dto, result);
        verify(balanceMapper).toDto(balance);
    }

    @Test
    void getBalance_shouldThrowWhenNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.getBalanceByAccountId(ACCOUNT_ID)
        );

        verifyNoInteractions(balanceMapper);
    }

    @Test
    void credit_shouldDelegateToEntity() {
        BalanceEntity balance = mockExistingBalance();
        Money amount = money("100.00");

        balanceService.credit(ACCOUNT_ID, amount);

        verify(balance).credit(amount);
    }

    @Test
    void debit_shouldDelegateToEntity() {
        BalanceEntity balance = mockExistingBalance();
        Money amount = money("100.00");

        balanceService.debit(ACCOUNT_ID, amount);

        verify(balance).debit(amount);
    }

    @Test
    void reserve_shouldDelegateToEntity() {
        BalanceEntity balance = mockExistingBalance();
        Money amount = money("100.00");

        balanceService.reserve(ACCOUNT_ID, amount);

        verify(balance).reserve(amount);
    }

    @Test
    void releaseReserve_shouldDelegateToEntity() {
        BalanceEntity balance = mockExistingBalance();
        Money amount = money("100.00");

        balanceService.releaseReserve(ACCOUNT_ID, amount);

        verify(balance).releaseReserve(amount);
    }

    @Test
    void commitReserve_shouldDelegateToEntity() {
        BalanceEntity balance = mockExistingBalance();
        Money amount = money("100.00");

        balanceService.commitReserve(ACCOUNT_ID, amount);

        verify(balance).commitReserve(amount);
    }

    @Test
    void credit_shouldThrowWhenBalanceNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.credit(ACCOUNT_ID, money("100.00"))
        );
    }

    @Test
    void debit_shouldThrowWhenBalanceNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.debit(ACCOUNT_ID, money("100.00"))
        );
    }

    @Test
    void reserve_shouldThrowWhenBalanceNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.reserve(ACCOUNT_ID, money("100.00"))
        );
    }

    @Test
    void releaseReserve_shouldThrowWhenBalanceNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.releaseReserve(ACCOUNT_ID, money("100.00"))
        );
    }

    @Test
    void commitReserve_shouldThrowWhenBalanceNotFound() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                BalanceNotFoundByAccountIdException.class,
                () -> balanceService.commitReserve(ACCOUNT_ID, money("100.00"))
        );
    }

    private BalanceEntity mockExistingBalance() {
        BalanceEntity balance = mock(BalanceEntity.class);

        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.of(balance));

        return balance;
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