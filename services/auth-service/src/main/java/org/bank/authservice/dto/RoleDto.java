package org.bank.authservice.dto;

import org.bank.authservice.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;

    @NotNull(message = "Role name cannot be null")
    private Role role;
}
