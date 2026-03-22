package org.bank.authservice.mapper;

import org.bank.authservice.dto.auth.RefreshTokenDto;
import org.bank.authservice.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper{

    RefreshTokenEntity toEntity(RefreshTokenDto refreshTokenDto);

    RefreshTokenDto toDto(RefreshTokenEntity refreshTokenEntity);
}
