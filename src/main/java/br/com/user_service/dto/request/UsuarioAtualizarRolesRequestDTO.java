package br.com.user_service.dto.request;

import br.com.user_service.enums.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UsuarioAtualizarRolesRequestDTO(
        @NotNull(message = "Roles são obrigatórias.")
        Set<Role> roles
) {
}
