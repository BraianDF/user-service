package br.com.user_service.dto.response;

import java.util.UUID;

public record UsuarioListarResponseDTO(
        UUID idUsuario,
        String email,
        Boolean status
) {
}
