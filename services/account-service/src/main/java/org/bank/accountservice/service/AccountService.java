package org.bank.accountservice.service;

import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(
            AccountRepository accountRepository,
            AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }
}
