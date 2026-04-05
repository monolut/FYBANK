package org.bank.userservice.mapper;

import org.bank.userservice.dto.UserDto;
import org.bank.userservice.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity userEntity);
}
