package org.bank.accountservice.mapper;

import org.bank.accountservice.dto.BalanceDto;
import org.bank.accountservice.entity.BalanceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    BalanceEntity toEntity(BalanceDto balanceDto);
    BalanceDto toDto(BalanceEntity balanceEntity);
}
