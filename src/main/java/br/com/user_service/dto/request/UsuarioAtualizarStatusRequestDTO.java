package br.com.user_service.dto.request;

import jakarta.validation.constraints.NotNull;

public record UsuarioAtualizarStatusRequestDTO(
        @NotNull(message = "Stats é obrigatório")
        Boolean status
) {
}
