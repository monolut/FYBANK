package org.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.AuthCommonService;
import org.bank.common.*;
import org.bank.common.exception.AccountNotFoundByIdException;
import org.bank.common.exception.TransactionNotFoundByIdException;
import org.bank.transactionservice.client.AccountClient;
import org.bank.transactionservice.entity.TransactionEntity;
import org.bank.transactionservice.mapper.TransactionMapper;
import org.bank.transactionservice.repository.TransactionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionReferenceGenerator transactionReferenceGenerator;
    private final AuthCommonService authCommonService;
    private final AccountClient accountClient;

    @Transactional(readOnly = true)
    public TransactionDto getTransaction(Long transactionId) {

        TransactionEntity transactionEntity =
                transactionRepository.findById(transactionId)
                        .orElseThrow(
                                () -> new TransactionNotFoundByIdException(transactionId)
                        );

        Long userId = authCommonService.getUserId();

        AccountDto recipientAccount =
                accountClient
                        .getAccountById(transactionEntity.getRecipientAccountId())
                        .getBody();

        if (recipientAccount == null) {
            throw new AccountNotFoundByIdException(
                    transactionEntity.getRecipientAccountId()
            );
        }

        AccountDto senderAccount =
                accountClient
                        .getAccountById(transactionEntity.getSenderAccountId())
                        .getBody();

        if (senderAccount == null) {
            throw new AccountNotFoundByIdException(
                    transactionEntity.getSenderAccountId()
            );
        }

        boolean isRecipient =
                recipientAccount.getUserId().equals(userId);

        boolean isSender =
                senderAccount.getUserId().equals(userId);

        if (!isRecipient && !isSender) {
            log.warn(
                    "User {} attempted to access transaction {}",
                    userId,
                    transactionId
            );

            throw new AccessDeniedException(
                    "You do not have access to this transaction"
            );
        }

        log.debug(
                "Transaction {} accessed by user {}",
                transactionId,
                userId
        );

        return transactionMapper.toDto(transactionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionDto recordTransaction(
            RecordTransactionRequest request
    ) {
        Long senderAccountId = request.senderAccountId();
        Long recipientAccountId = request.recipientAccountId();
        Money senderAmount = request.senderAmount();
        Money recipientAmount = request.recipientAmount();
        BigDecimal exchangeRate = request.exchangeRate();
        String description = request.description();
        TransactionType type = request.type();
        TransactionStatus status = request.status();

        log.info(
                "Recording transaction:" +
                        " senderId={}," +
                        " recipientId={}," +
                        " amount={} {}," +
                        " convertedAmount={} {}," +
                        " exchangeRate={}, type={}," +
                        " description={}" +
                        " status={}" ,
                senderAccountId,
                recipientAccountId,
                senderAmount.getAmount(),
                senderAmount.getCurrency(),
                recipientAmount.getAmount(),
                recipientAmount.getCurrency(),
                exchangeRate,
                description,
                type,
                status
        );

        TransactionEntity transaction = new TransactionEntity(
                senderAccountId,
                recipientAccountId,
                senderAmount,
                recipientAmount,
                exchangeRate,
                description,
                type,
                status,
                transactionReferenceGenerator.generate()
        );

        TransactionEntity saved = transactionRepository.save(transaction);

        log.info(
                "Transaction successfully recorded: transactionId={}, senderId={}, recipientId={}, amount={} {}, convertedAmount={} {}, exchangeRate={}, type={}, status={}",
                saved.getId(),
                senderAccountId,
                recipientAccountId,
                senderAmount.getAmount(),
                senderAmount.getCurrency(),
                recipientAmount.getAmount(),
                recipientAmount.getCurrency(),
                exchangeRate,
                type,
                status
        );

        return transactionMapper.toDto(saved);
    }
}