package org.bank.accountservice.mapper;

import org.bank.accountservice.dto.TransactionDto;
import org.bank.accountservice.entity.TransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionDto toDto(TransactionEntity entity);
}
