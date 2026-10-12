package org.bank.balanceservice.mapper;

import org.bank.common.BalanceDto;
import org.bank.balanceservice.entity.BalanceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    BalanceDto toDto(BalanceEntity balanceEntity);
}
