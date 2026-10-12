package org.bank.transactionservice.mapper;

import org.bank.common.TransactionDto;
import org.bank.transactionservice.entity.TransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionDto toDto(TransactionEntity entity);
    TransactionEntity toEntity(TransactionDto dto);
}
