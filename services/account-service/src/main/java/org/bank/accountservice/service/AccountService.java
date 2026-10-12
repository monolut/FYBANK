package org.bank.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.accountservice.client.BalanceClient;
import org.bank.common.AccountDto;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.*;
import org.bank.common.exception.AccountBalanceNotZeroException;
import org.bank.common.exception.AccountNotFoundByIban;
import org.bank.common.exception.AccountNotFoundByIdException;
import org.bank.common.exception.CurrencyNotFoundByAccountId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final BalanceClient balanceClient;
    private final AuthCommonService authCommonService;

    @Transactional
    public AccountDto createAccount(CurrencyCode currency) {

        Long userId = authCommonService.getUserId();

        log.info(
                "Creating account for userId={}, currency={}",
                userId,
                currency
        );

        log.debug(
                "Initial balance created for userId={}",
                userId
        );

        AccountEntity account = new AccountEntity(
                userId,
                IbanGenerator.generateIban("RO"),
                currency,
                AccountStatus.ACTIVE
        );

        accountRepository.save(account);

        balanceClient.createInitialBalance(
                new CreateBalanceRequest(account.getId(), currency)
        );

        log.info(
                "Account successfully created: accountId={}, userId={}, currency={}",
                account.getId(),
                userId,
                currency
        );

        return accountMapper.toDto(account);
    }

    @Transactional
    public void closeAccount(Long accountId) {

        log.info("Closing account, accountId={}", accountId);

        AccountEntity account = getAccountEntity(accountId);

        BalanceDto balance = balanceClient.getBalanceByAccountId(accountId);

        BigDecimal actualBalance = balance.getActual().getAmount();
        BigDecimal reservedBalance = balance.getReserved().getAmount();

        log.debug(
                "Account balances before closing: accountId={}, actual={}, reserved={}",
                accountId,
                actualBalance,
                reservedBalance
        );

        if (actualBalance.compareTo(BigDecimal.ZERO) != 0 ||
                reservedBalance.compareTo(BigDecimal.ZERO) != 0) {

            throw new AccountBalanceNotZeroException(accountId);

        } else {

            accountRepository.delete(account);

            balanceClient.deleteBalanceByAccountId(accountId);

            log.info(
                    "Account successfully closed, accountId={}",
                    accountId
            );
        }
    }

    @Transactional
    public void freezeAccount(Long accountId) {

        log.info("Freezing account, accountId={}", accountId);

        AccountEntity account = getAccountEntity(accountId);

        account.freeze();

        log.info(
                "Account successfully frozen, accountId={}",
                accountId
        );
    }

    @Transactional
    public void unFreezeAccount(Long accountId) {

        log.info("Unfreezing account, accountId={}", accountId);

        AccountEntity account = getAccountEntity(accountId);

        account.unfreeze();

        log.info(
                "Account successfully unfrozen, accountId={}",
                accountId
        );
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId) {

        log.debug("Fetching account, accountId={}", accountId);

        AccountDto account = accountMapper.toDto(getAccountEntity(accountId));

        BalanceDto balance = balanceClient.getBalanceByAccountId(accountId);

        account.setBalance(balance);

        log.debug("Account successfully fetched, accountId={}", accountId);

        return account;
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountByIban(String iban) {

        log.debug("Fetching account, iban={}", iban);

        AccountDto account = accountMapper.toDto(
                accountRepository.findByIban(iban)
                        .orElseThrow(() -> new AccountNotFoundByIban(iban))
        );

        BalanceDto balance = balanceClient.getBalanceByAccountId(account.getId());

        account.setBalance(balance);

        log.debug("Account successfully fetched, iban={}", iban);

        return account;
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts(Long userId) {

        log.debug(
                "Fetching accounts for userId={}",
                userId
        );

        List<AccountEntity> accounts = accountRepository.findAllByUserId(userId);

        return accounts.stream()
                        .map(account -> {
                                    BalanceDto balance =
                                            balanceClient.getBalanceByAccountId(account.getId());

                                    return new AccountDto(
                                            account.getId(),
                                            userId,
                                            account.getIban(),
                                            account.getStatus(),
                                            account.getCurrency(),
                                            balance,
                                            account.getCreatedAt()
                                    );
                                }).toList();
    }

    @Transactional(readOnly = true)
    public CurrencyCode getAccountCurrency(Long accountId) {
        return accountRepository.findCurrencyById(accountId)
                .orElseThrow(() -> new CurrencyNotFoundByAccountId(accountId));
    }

    @Transactional
    public void deleteAllAccounts(Long userId) {

        log.info(
                "Deleting all accounts for userId={}",
                userId
        );

        List<AccountEntity> accounts = accountRepository.findAllByUserId(userId);

        if (accounts.isEmpty()) {
            return;
        }

        List<Long> accountIds = accounts.stream()
                .map(AccountEntity::getId)
                .toList();

        balanceClient.deleteAllBalances(accountIds);

        log.debug(
                "All balances deleted for userId={}",
                userId
        );

        accountRepository.deleteAll(accounts);

        log.info(
                "All accounts and balances successfully deleted for userId={}",
                userId
        );
    }

    private AccountEntity getAccountEntity(Long accountId) {

        log.debug(
                "Fetching account entity, accountId={}",
                accountId
        );

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundByIdException(accountId));
    }
}