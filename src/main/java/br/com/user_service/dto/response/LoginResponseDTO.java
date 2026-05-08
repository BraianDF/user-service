package br.com.user_service.dto.response;

import br.com.user_service.enums.Role;

import java.util.Set;

public record LoginResponseDTO(
        String token,
        Set<Role> roles
) {
}
