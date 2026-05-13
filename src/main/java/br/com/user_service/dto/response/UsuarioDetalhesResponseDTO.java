package br.com.user_service.dto.response;

import br.com.user_service.enums.Role;
import java.util.Set;
import java.util.UUID;

public record UsuarioDetalhesResponseDTO(
        UUID idUsuario,
        String email,
        Set<Role> roles,
        Boolean ativo
) {
}
