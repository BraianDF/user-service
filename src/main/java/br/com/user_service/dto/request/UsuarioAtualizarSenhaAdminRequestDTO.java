package br.com.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UsuarioAtualizarSenhaAdminRequestDTO(
        @NotBlank(message = "Senha nova é obrigatória.")
        String senhaNova
) {
}
