package org.bank.accountservice.mapper;

import org.bank.common.AccountDto;
import org.bank.accountservice.entity.AccountEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountEntity toEntity(AccountDto accountDto);
    AccountDto toDto(AccountEntity accountEntity);

    List<AccountEntity> toEntityList(List<AccountDto> accountDtoList);
    List<AccountDto> toDtoList(List<AccountEntity> accountEntityList);
}
