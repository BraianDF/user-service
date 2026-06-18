package br.com.user_service.repository;

import br.com.user_service.dto.request.UsuarioCadastrarRequestDTO;
import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Deverá obter o usuário com sucesso do banco de dados")
    void findByEmailSucesso() {
        // Arrange (preparação)
        String email = "joao123@email.com";
        UsuarioCadastrarRequestDTO dto = new UsuarioCadastrarRequestDTO(email, "123456", Set.of(Role.USER));
        criarUsuario(dto);

        // Act (ação)
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        // Assert (validação)
        assertThat(usuario.isPresent()).isTrue();
        assertThat(usuario.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Não deverá obter o usuário do banco de dados quando não existir")
    void findByEmailErro() {
        // Arrange (preparação)
        String email = "joao123@email.com";

        // Act (ação)
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        // Assert (validação)
        assertThat(usuario.isEmpty()).isTrue();
    }

    private Usuario criarUsuario(UsuarioCadastrarRequestDTO dto) {
        Usuario usuario = new Usuario(dto.email(), dto.senha(), dto.roles());
        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }
}