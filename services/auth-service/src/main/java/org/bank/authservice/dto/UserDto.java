package org.bank.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotNull(message = "Register date cannot be null")
    private LocalDateTime registerDate;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should have email type")
    @Size(max = 50, message = "Email length should not exceed 50 characters")
    private String email;

    private RoleDto role;
}
