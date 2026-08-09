package org.bank.accountservice.service;

import lombok.extern.slf4j.Slf4j;
import org.bank.accountservice.dto.AccountDto;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.entity.BalanceEntity;
import org.bank.accountservice.enums.AccountStatus;
import org.bank.accountservice.exception.AccountBalanceNotZeroException;
import org.bank.accountservice.exception.AccountNotFoundByIdException;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.bank.accountservice.repository.BalanceRepository;
import org.bank.authcommon.service.AuthCommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final BalanceService balanceService;
    private final AuthCommonService authCommonService;
    private final BalanceRepository balanceRepository;

    public AccountService(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            BalanceService balanceService,
            AuthCommonService authCommonService,
            BalanceRepository balanceRepository
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.balanceService = balanceService;
        this.authCommonService = authCommonService;
        this.balanceRepository = balanceRepository;

        log.info("AccountService initialized");
    }

    @Transactional
    public AccountDto createAccount(Currency currency) {

        Long userId = authCommonService.getUserId();

        log.info(
                "Creating account for userId={}, currency={}",
                userId,
                currency
        );

        BalanceEntity balance = balanceService.createInitialBalance();

        log.debug(
                "Initial balance created for userId={}",
                userId
        );

        AccountEntity account = new AccountEntity();
        account.setUserId(userId);
        account.setBalance(balance);
        account.setStatus(AccountStatus.ACTIVE);
        account.setIban(IbanGenerator.generateIban("RO"));
        account.setCurrency(currency);

        accountRepository.save(account);

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

        AccountEntity account = accountRepository.findByIdWithBalance(accountId)
                .orElseThrow(() -> new AccountNotFoundByIdException(accountId));

        BigDecimal actualBalance = account.getBalance().getActual();
        BigDecimal reservedBalance = account.getBalance().getReserved();

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

        account.setStatus(AccountStatus.BLOCKED);

        log.info(
                "Account successfully frozen, accountId={}",
                accountId
        );
    }

    @Transactional
    public void unFreezeAccount(Long accountId) {

        log.info("Unfreezing account, accountId={}", accountId);

        AccountEntity account = getAccountEntity(accountId);

        account.setStatus(AccountStatus.ACTIVE);

        log.info(
                "Account successfully unfrozen, accountId={}",
                accountId
        );
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId) {

        log.debug("Fetching account, accountId={}", accountId);

        AccountDto account = accountMapper.toDto(
                getAccountEntity(accountId)
        );

        log.debug(
                "Account successfully fetched, accountId={}",
                accountId
        );

        return account;
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts(Long userId) {

        log.debug(
                "Fetching accounts for userId={}",
                userId
        );

        List<AccountDto> accounts = accountMapper.toDtoList(
                accountRepository.findAccountEntitiesByUserId(userId)
        );

        log.debug(
                "Accounts fetched for userId={}, accountCount={}",
                userId,
                accounts.size()
        );

        return accounts;
    }

    @Transactional
    public void deleteAllAccounts(Long userId) {

        log.info(
                "Deleting all accounts for userId={}",
                userId
        );

        accountRepository.deleteAllByUserId(userId);

        log.debug(
                "All accounts deleted for userId={}",
                userId
        );

        balanceRepository.deleteAllByUserId(userId);

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