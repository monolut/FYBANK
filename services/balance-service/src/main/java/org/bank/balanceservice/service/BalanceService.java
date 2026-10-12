package org.bank.balanceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.bank.common.BalanceDto;
import org.bank.balanceservice.entity.BalanceEntity;
import org.bank.balanceservice.mapper.BalanceMapper;
import org.bank.balanceservice.repository.BalanceRepository;
import org.bank.common.CurrencyCode;
import org.bank.common.Money;
import org.bank.common.exception.AccountBalanceNotZeroException;
import org.bank.common.exception.BalanceNotFoundByAccountIdException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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

    @Transactional
    public void createInitialBalance(
            Long accountId,
            CurrencyCode currency
    ) {
        BalanceEntity balance = new BalanceEntity(
                accountId,
                new Money(new BigDecimal("1000.00"), currency),
                null
        );

        balanceRepository.save(balance);
    }

    @Transactional
    public void deleteBalanceByAccountId(Long accountId) {
        BalanceEntity balance = balanceRepository.findBalanceByAccountId(accountId)
                .orElseThrow(() -> new BalanceNotFoundByAccountIdException(accountId));

        if (balance.getActual().getAmount().compareTo(BigDecimal.ZERO) != 0 ||
                balance.getReserved().getAmount().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountBalanceNotZeroException(accountId);
        }

        balanceRepository.delete(balance);

        log.info(
                "Balance successfully deleted, accountId={}",
                accountId
        );
    }

    @Transactional
    public void deleteAllBalances(List<Long> accountIds) {
        balanceRepository.deleteByAccountIds(accountIds);

        log.info("Balances successfully deleted");
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalanceByAccountId(Long accountId) {
        BalanceEntity balance = balanceRepository.findBalanceByAccountId(accountId)
                .orElseThrow(() -> new BalanceNotFoundByAccountIdException(accountId));

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public void credit(Long accountId, Money amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.credit(amount);
    }

    @Transactional
    public void debit(Long accountId, Money amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.debit(amount);
    }

    @Transactional
    public void reserve(Long accountId, Money amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.reserve(amount);
    }

    @Transactional
    public void releaseReserve(Long accountId, Money amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.releaseReserve(amount);
    }

    @Transactional
    public void commitReserve(Long accountId, Money amount) {
        BalanceEntity balance = getBalanceEntity(accountId);
        balance.commitReserve(amount);
    }

    private BalanceEntity getBalanceEntity(Long accountId) {
        return balanceRepository.findBalanceByAccountId(accountId)
                .orElseThrow(() -> new BalanceNotFoundByAccountIdException(accountId));
    }
}
