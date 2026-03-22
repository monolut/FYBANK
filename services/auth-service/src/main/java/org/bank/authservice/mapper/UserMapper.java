package org.bank.authservice.mapper;

import org.bank.authservice.dto.UserDto;
import org.bank.authservice.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserEntity toEntity(UserDto userDto);
    UserDto toDto(UserEntity userEntity);
}
