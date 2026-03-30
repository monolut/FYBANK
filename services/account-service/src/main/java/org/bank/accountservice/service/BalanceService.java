package org.bank.accountservice.service;

import org.bank.accountservice.dto.BalanceDto;
import org.bank.accountservice.entity.BalanceEntity;
import org.bank.accountservice.exception.AccountNotFoundByIdException;
import org.bank.accountservice.mapper.BalanceMapper;
import org.bank.accountservice.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public BalanceService(
            BalanceRepository balanceRepository,
            BalanceMapper balanceMapper
    ) {
        this.balanceRepository = balanceRepository;
        this.balanceMapper = balanceMapper;
    }

    public BalanceEntity createInitialBalance() {
        return new BalanceEntity(
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalanceByAccountId(Long accountId) {
        BalanceEntity balance = balanceRepository.findBalanceByAccountId(accountId)
                .orElseThrow(() ->  new AccountNotFoundByIdException(accountId));

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public void credit(Long accountId, BigDecimal amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.credit(amount);
    }

    @Transactional
    public void debit(Long accountId, BigDecimal amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.debit(amount);
    }

    @Transactional
    public void reserve(Long accountId, BigDecimal amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.reserve(amount);
    }

    @Transactional
    public void releaseReserve(Long accountId, BigDecimal amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.releaseReserve(amount);
    }

    @Transactional
    public void commitReserve(Long accountId, BigDecimal amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.commitReserve(amount);
    }

    private BalanceEntity getBalanceEntity(Long accountId) {
        return balanceRepository.findBalanceByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundByIdException(accountId));
    }
}
