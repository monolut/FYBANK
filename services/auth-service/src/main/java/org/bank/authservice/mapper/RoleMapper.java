package org.bank.authservice.mapper;

import org.bank.authservice.dto.RoleDto;
import org.bank.authservice.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleEntity toEntity(RoleDto roleDto);

    RoleDto toDto(RoleEntity roleEntity);
}
