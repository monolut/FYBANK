package org.bank.accountservice.service;

import org.bank.accountservice.client.BalanceClient;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.*;
import org.bank.common.exception.AccountBalanceNotZeroException;
import org.bank.common.exception.AccountNotFoundByIbanException;
import org.bank.common.exception.AccountNotFoundByIdException;
import org.bank.common.exception.CurrencyNotFoundByAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private BalanceClient balanceClient;

    @Mock
    private AuthCommonService authCommonService;

    @InjectMocks
    private AccountService accountService;

    private AccountEntity account;
    private BalanceDto balance;
    private Money actual;
    private Money reserved;
    private AccountDto accountDto;

    private static final Long ACCOUNT_ID = 10L;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        account = new AccountEntity(
                USER_ID,
                "RO_TEST_IBAN",
                CurrencyCode.EUR,
                AccountStatus.ACTIVE
        );

        ReflectionTestUtils.setField(account, "id", ACCOUNT_ID);

        actual = mock(Money.class);
        reserved = mock(Money.class);
        balance = mock(BalanceDto.class);

        accountDto = AccountDto.builder()
                .id(ACCOUNT_ID)
                .userId(USER_ID)
                .iban("RO_TEST_IBAN")
                .currency(CurrencyCode.EUR)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void createAccount_whenValidCurrency_createsAccountAndInitialBalance() {
        when(authCommonService.getUserId()).thenReturn(USER_ID);
        when(accountMapper.toDto(any(AccountEntity.class)))
                .thenReturn(accountDto);

        ArgumentCaptor<AccountEntity> accountCaptor =
                ArgumentCaptor.forClass(AccountEntity.class);

        AccountDto result = accountService.createAccount(
                CurrencyCode.EUR
        );

        verify(accountRepository).save(accountCaptor.capture());

        AccountEntity savedAccount = accountCaptor.getValue();

        assertEquals(USER_ID, savedAccount.getUserId());
        assertEquals(CurrencyCode.EUR, savedAccount.getCurrency());
        assertEquals(AccountStatus.ACTIVE, savedAccount.getStatus());
        assertNotNull(savedAccount.getIban());
        assertTrue(savedAccount.getIban().startsWith("RO"));
        assertNotNull(savedAccount.getCreatedAt());

        verify(balanceClient).createInitialBalance(
                any(CreateBalanceRequest.class)
        );

        verify(accountMapper).toDto(savedAccount);

        assertSame(accountDto, result);
    }

    @Test
    void closeAccount_whenBothBalancesAreZero_deletesAccountAndBalance() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        when(balance.getActual()).thenReturn(actual);
        when(balance.getReserved()).thenReturn(reserved);

        when(actual.getAmount()).thenReturn(BigDecimal.ZERO);
        when(reserved.getAmount()).thenReturn(BigDecimal.ZERO);

        accountService.closeAccount(ACCOUNT_ID);

        verify(accountRepository).delete(account);
        verify(balanceClient).deleteBalanceByAccountId(ACCOUNT_ID);
    }

    @Test
    void closeAccount_whenActualBalanceIsPositive_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        when(balance.getActual()).thenReturn(actual);
        when(balance.getReserved()).thenReturn(reserved);

        when(actual.getAmount())
                .thenReturn(new BigDecimal("100.00"));
        when(reserved.getAmount())
                .thenReturn(BigDecimal.ZERO);

        assertThrows(
                AccountBalanceNotZeroException.class,
                () -> accountService.closeAccount(ACCOUNT_ID)
        );

        verify(accountRepository, never()).delete(any(AccountEntity.class));

        verify(balanceClient, never())
                .deleteBalanceByAccountId(anyLong());
    }

    @Test
    void closeAccount_whenActualBalanceIsNegative_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        when(balance.getActual()).thenReturn(actual);
        when(balance.getReserved()).thenReturn(reserved);

        when(actual.getAmount())
                .thenReturn(new BigDecimal("-10.00"));

        when(reserved.getAmount())
                .thenReturn(BigDecimal.ZERO);

        assertThrows(
                AccountBalanceNotZeroException.class,
                () -> accountService.closeAccount(ACCOUNT_ID)
        );

        verify(accountRepository, never()).delete(any(AccountEntity.class));

        verify(balanceClient, never())
                .deleteBalanceByAccountId(anyLong());
    }

    @Test
    void closeAccount_whenReservedBalanceIsPositive_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        when(balance.getActual()).thenReturn(actual);
        when(balance.getReserved()).thenReturn(reserved);

        when(actual.getAmount())
                .thenReturn(BigDecimal.ZERO);

        when(reserved.getAmount())
                .thenReturn(new BigDecimal("50.00"));

        assertThrows(
                AccountBalanceNotZeroException.class,
                () -> accountService.closeAccount(ACCOUNT_ID)
        );

        verify(accountRepository, never()).delete(any(AccountEntity.class));

        verify(balanceClient, never())
                .deleteBalanceByAccountId(anyLong());
    }

    @Test
    void closeAccount_whenAccountDoesNotExist_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundByIdException.class,
                () -> accountService.closeAccount(ACCOUNT_ID)
        );

        verifyNoInteractions(balanceClient);
        verify(accountRepository, never()).delete(any(AccountEntity.class));
    }

    @Test
    void freezeAccount_whenAccountIsActive_freezesAccount() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        accountService.freezeAccount(ACCOUNT_ID);

        assertEquals(AccountStatus.FROZEN, account.getStatus());

        verify(accountRepository).findById(ACCOUNT_ID);
        verifyNoInteractions(balanceClient);
    }

    @Test
    void freezeAccount_whenAccountDoesNotExist_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundByIdException.class,
                () -> accountService.freezeAccount(ACCOUNT_ID)
        );
    }

    @Test
    void freezeAccount_whenAccountIsClosed_throwsException() {
        account.close();

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        assertThrows(
                IllegalStateException.class,
                () -> accountService.freezeAccount(ACCOUNT_ID)
        );

        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    void unFreezeAccount_whenAccountIsFrozen_activatesAccount() {
        account.freeze();

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        accountService.unFreezeAccount(ACCOUNT_ID);

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void unFreezeAccount_whenAccountIsActive_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        assertThrows(
                IllegalStateException.class,
                () -> accountService.unFreezeAccount(ACCOUNT_ID)
        );

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void unFreezeAccount_whenAccountDoesNotExist_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundByIdException.class,
                () -> accountService.unFreezeAccount(ACCOUNT_ID)
        );
    }

    @Test
    void getAccountById_whenAccountExists_returnsAccountWithBalance() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        when(accountMapper.toDto(account))
                .thenReturn(accountDto);

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        AccountDto result =
                accountService.getAccountById(ACCOUNT_ID);

        assertSame(accountDto, result);
        assertSame(balance, result.getBalance());

        verify(accountMapper).toDto(account);

        verify(balanceClient)
                .getBalanceByAccountId(ACCOUNT_ID);
    }

    @Test
    void getAccountById_whenAccountDoesNotExist_throwsException() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundByIdException.class,
                () -> accountService.getAccountById(ACCOUNT_ID)
        );

        verifyNoInteractions(balanceClient);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getAccountByIban_whenAccountExists_returnsAccountWithBalance() {
        String iban = account.getIban();

        when(accountRepository.findByIban(iban))
                .thenReturn(Optional.of(account));

        when(accountMapper.toDto(account))
                .thenReturn(accountDto);

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        AccountDto result =
                accountService.getAccountByIban(iban);

        assertSame(accountDto, result);
        assertSame(balance, result.getBalance());

        verify(accountRepository).findByIban(iban);

        verify(balanceClient)
                .getBalanceByAccountId(ACCOUNT_ID);
    }

    @Test
    void getAccountByIban_whenAccountDoesNotExist_throwsException() {
        String iban = "RO_NON_EXISTENT_IBAN";

        when(accountRepository.findByIban(iban))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundByIbanException.class,
                () -> accountService.getAccountByIban(iban)
        );

        verifyNoInteractions(balanceClient);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getAccounts_whenAccountsExist_returnsPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 20);

        AccountEntity secondAccount = new AccountEntity(
                USER_ID,
                "RO_SECOND_IBAN",
                CurrencyCode.EUR,
                AccountStatus.ACTIVE
        );

        ReflectionTestUtils.setField(
                secondAccount,
                "id",
                11L
        );

        List<AccountEntity> accounts =
                List.of(account, secondAccount);

        Page<AccountEntity> accountPage = new PageImpl<>(
                accounts,
                pageable,
                accounts.size()
        );

        when(accountRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<AccountEntity>>any(),
                eq(pageable)
        )).thenReturn(accountPage);

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        when(balanceClient.getBalanceByAccountId(11L))
                .thenReturn(balance);

        PageResponse<AccountDto> result =
                accountService.getAccounts(
                        USER_ID,
                        null,
                        null,
                        pageable
                );

        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(20, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.first());
        assertTrue(result.last());

        verify(balanceClient).getBalanceByAccountId(ACCOUNT_ID);
        verify(balanceClient).getBalanceByAccountId(11L);
    }

    @Test
    void getAccounts_whenCurrencyAndStatusProvided_returnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<AccountEntity> accountPage = new PageImpl<>(
                List.of(account),
                pageable,
                1
        );

        when(accountRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<AccountEntity>>any(),
                eq(pageable)
        )).thenReturn(accountPage);

        when(balanceClient.getBalanceByAccountId(ACCOUNT_ID))
                .thenReturn(balance);

        PageResponse<AccountDto> result =
                accountService.getAccounts(
                        USER_ID,
                        CurrencyCode.EUR,
                        AccountStatus.ACTIVE,
                        pageable
                );

        assertEquals(1, result.content().size());

        AccountDto returnedAccount = result.content().getFirst();

        assertEquals(USER_ID, returnedAccount.getUserId());
        assertEquals(CurrencyCode.EUR, returnedAccount.getCurrency());
        assertEquals(AccountStatus.ACTIVE, returnedAccount.getStatus());

        verify(accountRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<AccountEntity>>any(),
                eq(pageable)
        );
    }

    @Test
    void getAccounts_whenNoAccountsExist_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<AccountEntity> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(accountRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<AccountEntity>>any(),
                eq(pageable)
        )).thenReturn(emptyPage);

        PageResponse<AccountDto> result =
                accountService.getAccounts(
                        USER_ID,
                        null,
                        null,
                        pageable
                );

        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());

        verifyNoInteractions(balanceClient);
    }

    @Test
    void getAccountCurrency_whenAccountExists_returnsCurrency() {
        when(accountRepository.findCurrencyById(ACCOUNT_ID))
                .thenReturn(Optional.of(CurrencyCode.EUR));

        CurrencyCode result =
                accountService.getAccountCurrency(ACCOUNT_ID);

        assertEquals(CurrencyCode.EUR, result);

        verify(accountRepository)
                .findCurrencyById(ACCOUNT_ID);
    }

    @Test
    void getAccountCurrency_whenAccountDoesNotExist_throwsException() {
        when(accountRepository.findCurrencyById(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                CurrencyNotFoundByAccountId.class,
                () -> accountService.getAccountCurrency(ACCOUNT_ID)
        );
    }

    @Test
    void deleteAllAccounts_whenUserHasNoAccounts_doesNothing() {
        when(accountRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of());

        accountService.deleteAllAccounts(USER_ID);

        verify(accountRepository).findAllByUserId(USER_ID);

        verify(accountRepository, never()).deleteAll(
                anyList()
        );

        verifyNoInteractions(balanceClient);
    }

    @Test
    void deleteAllAccounts_whenUserHasAccounts_deletesBalancesAndAccounts() {
        AccountEntity secondAccount = new AccountEntity(
                USER_ID,
                "RO_SECOND_IBAN",
                CurrencyCode.USD,
                AccountStatus.ACTIVE
        );

        ReflectionTestUtils.setField(
                secondAccount,
                "id",
                11L
        );

        when(accountRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(account, secondAccount));

        accountService.deleteAllAccounts(USER_ID);

        verify(balanceClient).deleteAllBalances(
                List.of(ACCOUNT_ID, 11L)
        );

        verify(accountRepository).deleteAll(
                List.of(account, secondAccount)
        );
    }
}