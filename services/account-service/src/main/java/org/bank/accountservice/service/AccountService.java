package org.bank.accountservice.service;

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
            BalanceRepository balanceRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.balanceService = balanceService;
        this.authCommonService =authCommonService;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public AccountDto createAccount(Currency currency) {
        Long userId = authCommonService.getUserId();
        BalanceEntity balance = balanceService.createInitialBalance();

        AccountEntity account = new AccountEntity();
        account.setUserId(userId);
        account.setBalance(balance);
        account.setStatus(AccountStatus.ACTIVE);
        account.setIban(IbanGenerator.generateIban("RO"));
        account.setCurrency(currency);

        accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    @Transactional
    public void closeAccount(Long accountId) {
        AccountEntity account = accountRepository.findByIdWithBalance(accountId)
                .orElseThrow(() -> new AccountNotFoundByIdException(accountId));

        if (account.getBalance().getActual().compareTo(BigDecimal.ZERO) != 0 ||
            account.getBalance().getReserved().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountBalanceNotZeroException(accountId);
        } else {
            accountRepository.delete(account);
        }
    }

    @Transactional
    public void freezeAccount(Long accountId) {
        AccountEntity account = getAccountEntity(accountId);

        account.setStatus(AccountStatus.BLOCKED);
    }

    @Transactional
    public void unFreezeAccount(Long accountId) {
        AccountEntity account = getAccountEntity(accountId);

        account.setStatus(AccountStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId) {
        return accountMapper.toDto(getAccountEntity(accountId));
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts(Long userId) {
        return accountMapper.toDtoList(accountRepository.findAccountEntitiesByUserId(userId));
    }

    @Transactional
    public void deleteAllAccounts(Long userId) {
        accountRepository.deleteAllByUserId(userId);
        balanceRepository.deleteAllByUserId(userId);
    }

    private AccountEntity getAccountEntity(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundByIdException(accountId));
    }
}
