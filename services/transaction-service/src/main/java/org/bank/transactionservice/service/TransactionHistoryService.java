package org.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.*;
import org.bank.common.exception.AccountNotFoundByIdException;
import org.bank.transactionservice.client.AccountClient;
import org.bank.transactionservice.entity.TransactionEntity;
import org.bank.transactionservice.mapper.TransactionMapper;
import org.bank.transactionservice.repository.TransactionRepository;
import org.bank.transactionservice.specification.TransactionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionHistoryService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuthCommonService authCommonService;
    private final AccountClient accountClient;

    @Transactional(readOnly = true)
    public PageResponse<TransactionDto> getHistory(
            Long accountId,
            TransactionType type,
            TransactionStatus status,
            Instant from,
            Instant to,
            Pageable pageable
    ) {

        log.debug(
                "Fetching transaction history: accountId={}, page={}, size={}",
                accountId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Long userId = authCommonService.getUserId();

        AccountDto accountDto = accountClient.getAccountById(accountId).getBody();

        if (accountDto == null) {
            throw new AccountNotFoundByIdException(
                    accountId
            );
        }

        if (!accountDto.getUserId().equals(userId)) {
            log.warn(
                    "User {} attempted to access transaction history of account {}",
                    userId,
                    accountId
            );

            throw new AccessDeniedException(
                    "You do not have access to this account"
            );
        }

        Specification<TransactionEntity> spec =
                TransactionSpecifications.hasSenderAccountId(accountId)
                        .or(TransactionSpecifications.hasRecipientAccountId(accountId));

        if(status != null) {
            spec = spec.and(TransactionSpecifications.hasStatus(status));
        }

        if (type != null) {
            spec = spec.and(
                    TransactionSpecifications.hasType(type)
            );
        }

        if (from != null) {
            spec = spec.and(
                    TransactionSpecifications.createdAfter(from)
            );
        }

        if (to != null) {
            spec = spec.and(
                    TransactionSpecifications.createdBefore(to)
            );
        }

        Page<TransactionDto> page = transactionRepository.findAll(spec, pageable).map(transactionMapper::toDto);

        PageResponse<TransactionDto> response = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
        log.debug(
                "Transaction history fetched: accountId={}, page={}, size={}, totalElements={}",
                accountId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                response.totalElements()
        );

        return response;
    }
}
