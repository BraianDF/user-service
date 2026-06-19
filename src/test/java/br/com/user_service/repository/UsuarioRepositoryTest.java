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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Deve obter o usuário com sucesso do BD")
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
    @DisplayName("Não deve obter o usuário do BD quando o usuário não existir")
    void findByEmailErro() {
        // Arrange (preparação)
        String email = "joao123@email.com";

        // Act (ação)
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        // Assert (validação)
        assertThat(usuario.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar true quando o e-mail existir")
    void existsByEmailSucesso() {
        // Arrange (preparação)
        String email = "joao123@email.com";
        UsuarioCadastrarRequestDTO dto = new UsuarioCadastrarRequestDTO(email, "123456", Set.of(Role.USER));
        criarUsuario(dto);

        // Act (ação)
        boolean existe = usuarioRepository.existsByEmail(email);

        // Assert (validação)
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar false quando o e-mail não existir")
    void existsByEmailErro() {
        // Arrange (preparação)
        String email = "naoexiste@email.com";

        // Act (ação)
        boolean existe = usuarioRepository.existsByEmail(email);

        // Assert (validação)
        assertFalse(existe);
    }

    @Test
    @DisplayName("Deve retornar true quando existir outro usuário com o mesmo e-mail")
    void existsByEmailAndPublicIdNotSucesso() {
        // Arrange (preparação)
        String email = "joao123@email.com";
        UUID publicId = UUID.randomUUID();
        UUID outroId = UUID.randomUUID();
        UsuarioCadastrarRequestDTO dto = new UsuarioCadastrarRequestDTO(email, "123456", Set.of(Role.USER));
        criarUsuarioPublicId(dto, publicId);

        // Act (ação)
        boolean existe = usuarioRepository.existsByEmailAndPublicIdNot(email, outroId);

        // Assert (validação)
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar false quando o e-mail pertencer ao próprio usuário")
    void existsByEmailAndPublicIdNotErro1() {
        // Arrange (preparação)
        String email = "joao123@email.com";
        UUID publicId = UUID.randomUUID();
        UsuarioCadastrarRequestDTO dto = new UsuarioCadastrarRequestDTO(email, "123456", Set.of(Role.USER));
        criarUsuarioPublicId(dto, publicId);

        // Act (ação)
        boolean existe = usuarioRepository.existsByEmailAndPublicIdNot(email, publicId);

        // Assert (validação)
        assertFalse(existe);
    }

    @Test
    @DisplayName("Deve retornar false quando existir usuário com outro e-mail")
    void existsByEmailAndPublicIdNotErro2() {
        // Arrange (preparação)
        String email = "joao123@email.com";
        String outroEmail = "naoexiste@email.com";
        UUID publicId = UUID.randomUUID();
        UsuarioCadastrarRequestDTO dto = new UsuarioCadastrarRequestDTO(email, "123456", Set.of(Role.USER));
        criarUsuarioPublicId(dto, publicId);

        // Act (ação)
        boolean existe = usuarioRepository.existsByEmailAndPublicIdNot(outroEmail, publicId);

        // Assert (validação)
        assertFalse(existe);
    }

    @Test
    @DisplayName("Deve retornar false quando o e-mail não existir")
    void existsByEmailAndPublicIdNotErro3() {
        // Arrange (preparação)
        String email = "naoexiste@email.com";
        UUID publicId = UUID.randomUUID();

        // Act (ação)
        boolean existe = usuarioRepository.existsByEmailAndPublicIdNot(email, publicId);

        // Assert (validação)
        assertFalse(existe);
    }

    private Usuario criarUsuario(UsuarioCadastrarRequestDTO dto) {
        Usuario usuario = new Usuario(dto.email(), dto.senha(), dto.roles());
        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }

    private Usuario criarUsuarioPublicId(UsuarioCadastrarRequestDTO dto, UUID publicId) {
        Usuario usuario = new Usuario(dto.email(), dto.senha(), dto.roles());
        usuario.setPublicId(publicId);
        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }
}