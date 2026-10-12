package org.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.AccountDto;
import org.bank.common.TransactionDto;
import org.bank.common.exception.AccountNotFoundByIdException;
import org.bank.transactionservice.client.AccountClient;
import org.bank.transactionservice.dto.PageResponse;
import org.bank.transactionservice.mapper.TransactionMapper;
import org.bank.transactionservice.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Page<TransactionDto> page =
                transactionRepository
                        .findBySenderAccountIdOrRecipientAccountId(
                                accountId,
                                accountId,
                                pageable
                        )
                        .map(transactionMapper::toDto);

        log.debug(
                "Transaction history fetched: accountId={}, page={}, size={}, totalElements={}",
                accountId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                page.getTotalElements()
        );

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
