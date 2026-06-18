package br.com.user_service.service;

import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(
                tokenService,
                "secret",
                "minha-chave-secreta"
        );
    }

    @Test
    @DisplayName("Deve gerar token com sucesso")
    void generateTokenSucesso() {
        // Arrange (preparação)
        Usuario usuario = new Usuario("joao@email.com","123456",Set.of(Role.USER));
        usuario.setPublicId(UUID.randomUUID());

        // Act (ação)
        String token = tokenService.generateToken(usuario);

        // Assert (validação)
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Deve validar token e retornar o publicId do usuário")
    void validateTokenSucesso() {
        // Arrange (preparação)
        UUID publicId = UUID.randomUUID();

        Usuario usuario = new Usuario("joao@email.com","123456",Set.of(Role.USER));
        usuario.setPublicId(publicId);

        String token = tokenService.generateToken(usuario);

        // Act (ação)
        String subject = tokenService.validateToken(token);

        // Assert (validação)
        assertEquals(publicId.toString(), subject);
    }

    @Test
    @DisplayName("Deve retornar vazio quando token for inválido")
    void validateTokenErro1() {
        // Arrange (preparação)
        String tokenInvalido = "token.invalido.aqui";

        // Act (ação)
        String resultado = tokenService.validateToken(tokenInvalido);

        // Assert (validação)
        assertEquals("", resultado);
    }

    @Test
    @DisplayName("Deve retornar vazio quando token for assinado com outra chave")
    void validateTokenErro2() {
        // Arrange (preparação)
        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService,"secret","outra-chave");

        Usuario usuario = new Usuario("joao@email.com", "123456",Set.of(Role.USER));
        usuario.setPublicId(UUID.randomUUID());

        String token = outroService.generateToken(usuario);

        // Act (ação)
        String resultado = tokenService.validateToken(token);

        // Assert (validação)
        assertEquals("", resultado);
    }
}