package br.com.user_service.dto.request;

import br.com.user_service.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UsuarioCadastrarRequestDTO(
        @Email(message = "Precisa ser um e-mail válido.")
        @NotBlank(message = "Email é obrigatório.")
        String email,
        @NotBlank(message = "Senha é obrigatória.")
        String senha,
        @NotNull(message = "Roles são obrigatórias.")
        Set<Role> roles
) {
}
