package br.com.user_service.service;

import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    private static final String SECRET = "minha-chave-secreta";
    private static final String ISSUER = "user-service";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService,"secret",SECRET);
        ReflectionTestUtils.setField(tokenService,"service",ISSUER);
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
    @DisplayName("Deve lançar exceção quando token for inválido")
    void validateTokenErro1() {
        // Arrange (preparação)
        String tokenInvalido = "token.invalido.aqui";

        // Act & Assert
        JWTVerificationException exception = assertThrows(
                JWTVerificationException.class,
                () -> tokenService.validateToken(tokenInvalido)
        );

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando token for assinado com outra chave")
    void validateTokenErro2() {
        // Arrange (preparação)
        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService,"secret","outra-chave");
        ReflectionTestUtils.setField(outroService,"service",ISSUER);

        Usuario usuario = new Usuario("joao@email.com", "123456",Set.of(Role.USER));
        usuario.setPublicId(UUID.randomUUID());

        String token = outroService.generateToken(usuario);

        // Act & Assert
        JWTVerificationException exception = assertThrows(
                JWTVerificationException.class,
                () -> tokenService.validateToken(token)
        );

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando token possuir issuer diferente")
    void validateTokenErro3() {
        // Arrange (preparação)
        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService,"secret",SECRET);
        ReflectionTestUtils.setField(outroService,"service","outro-service");

        Usuario usuario = new Usuario("joao@email.com", "123456",Set.of(Role.USER));
        usuario.setPublicId(UUID.randomUUID());

        String token = outroService.generateToken(usuario);

        // Act & Assert
        JWTVerificationException exception = assertThrows(
                JWTVerificationException.class,
                () -> tokenService.validateToken(token)
        );

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }
}