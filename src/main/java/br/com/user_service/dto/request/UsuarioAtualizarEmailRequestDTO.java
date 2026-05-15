package br.com.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioAtualizarEmailRequestDTO(
        @Email(message = "Precisa ser um e-mail válido.")
        @NotBlank(message = "Email é obrigatório.")
        String email
) {
}
