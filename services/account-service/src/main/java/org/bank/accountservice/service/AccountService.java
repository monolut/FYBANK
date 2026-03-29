package org.bank.accountservice.service;

import org.bank.accountservice.dto.AccountDto;
import org.bank.accountservice.entity.AccountEntity;
import org.bank.accountservice.entity.BalanceEntity;
import org.bank.accountservice.enums.AccountStatus;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.bank.authcommon.service.AuthCommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Currency;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final BalanceService balanceService;
    private final AuthCommonService authCommonService;

    public AccountService(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            BalanceService balanceService,
            AuthCommonService authCommonService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.balanceService = balanceService;
        this.authCommonService =authCommonService;
    }

    @Transactional
    public AccountDto createAccount(Currency currency) {
        Long userId = authCommonService.getUserId();
        BalanceEntity balance = balanceService.createInitialBalance(currency);

        AccountEntity account = new AccountEntity();
        account.setUserId(userId);
        account.setBalance(balance);
        account.setStatus(AccountStatus.ACTIVE);

        return accountMapper.toDto(account);
    }
}
