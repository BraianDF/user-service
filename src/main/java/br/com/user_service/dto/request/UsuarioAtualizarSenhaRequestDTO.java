package br.com.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UsuarioAtualizarSenhaRequestDTO(
        @NotBlank(message = "Senha atual é obrigatória.")
        String senhaAtual,
        @NotBlank(message = "Senha nova é obrigatória.")
        String senhaNova
) {
}
