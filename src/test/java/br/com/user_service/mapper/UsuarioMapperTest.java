package br.com.user_service.mapper;

import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.dto.response.UsuarioListarResponseDTO;
import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    @DisplayName("Deve converter Usuario para UsuarioDetalhesResponseDTO com sucesso")
    void toDetalhesResponseDTOSucesso() {
        // Arrange (preparação)
        String email = "joao@email.com";
        UUID publicId = UUID.randomUUID();

        Usuario usuario = new Usuario();

        usuario.setEmail(email);
        usuario.setPublicId(publicId);
        usuario.setStatus(true);
        usuario.setRoles(Set.of(Role.USER));

        // Act (ação)
        UsuarioDetalhesResponseDTO response = mapper.toDetalhesResponseDTO(usuario);

        // Assert (validação)
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(publicId, response.idUsuario()),
                () -> assertEquals(email, response.email()),
                () -> assertEquals(true, response.status()),
                () -> assertEquals(Set.of(Role.USER), response.roles())
        );
    }

    @Test
    @DisplayName("Deve retornar null quando Usuario for null")
    void toDetalhesResponseDTOErro() {
        // Act (ação)
        UsuarioDetalhesResponseDTO response = mapper.toDetalhesResponseDTO(null);

        // Assert (validação)
        assertNull(response);
    }

    @Test
    @DisplayName("Deve converter Usuario para UsuarioListarResponseDTO com sucesso")
    void toListarResponseDTOSucesso() {
        // Arrange (preparação)
        String email = "joao@email.com";
        UUID publicId = UUID.randomUUID();

        Usuario usuario = new Usuario();

        usuario.setEmail(email);
        usuario.setPublicId(publicId);
        usuario.setStatus(true);

        // Act (ação)
        UsuarioListarResponseDTO response = mapper.toListarResponseDTO(usuario);

        // Assert (validação)
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(publicId, response.idUsuario()),
                () -> assertEquals(email, response.email()),
                () -> assertEquals(true, response.status())
        );
    }

    @Test
    @DisplayName("Deve retornar null quando Usuario for null")
    void toListarResponseDTOErro() {
        // Act (ação)
        UsuarioListarResponseDTO response = mapper.toListarResponseDTO(null);

        // Assert (validação)
        assertNull(response);
    }
}