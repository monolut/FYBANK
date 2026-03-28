package org.bank.accountservice.mapper;

import org.bank.accountservice.dto.AccountDto;
import org.bank.accountservice.entity.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BalanceMapper.class})
public interface AccountMapper {
    AccountEntity toEntity(AccountDto accountDto);
    AccountDto toDto(AccountEntity accountEntity);
}
