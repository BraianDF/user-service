package br.com.user_service.filter;

import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import br.com.user_service.service.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter securityFilter;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar usuário quando o token for válido")
    void doFilterInternalSucesso() throws ServletException, IOException {
        // Arrange (preparação)
        UUID publicId = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setPublicId(publicId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.validateToken("token123"))
                .thenReturn(publicId.toString());

        when(usuarioRepository.findByPublicId(publicId))
                .thenReturn(Optional.of(usuario));

        // Act (ação)
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert (verificação)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(usuario, authentication.getPrincipal());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar o filtro quando não existir header Authorization")
    void doFilterInternalErro1() throws ServletException, IOException {
        // Arrange (preparação)
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act (ação)
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert (verificação)
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(tokenService);
        verifyNoInteractions(usuarioRepository);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar usuário quando token for inválido")
    void doFilterInternalErro2() throws ServletException, IOException {
        // Arrange (preparação)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-invalido");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.validateToken("token-invalido"))
                .thenThrow(new JWTVerificationException("Token inválido ou expirado"));

        // Act (ação)
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert (verificação)
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(tokenService).validateToken("token-invalido");
        verify(usuarioRepository, never()).findByPublicId(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar o filtro quando usuário não for encontrado")
    void doFilterInternalErro3() throws ServletException, IOException {
        // Arrange (preparação)
        UUID publicId = UUID.randomUUID();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.validateToken("token123"))
                .thenReturn(publicId.toString());

        when(usuarioRepository.findByPublicId(publicId))
                .thenReturn(Optional.empty());

        // Act (ação)
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert (verificação)
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(tokenService).validateToken("token123");
        verify(usuarioRepository).findByPublicId(publicId);
        verify(filterChain).doFilter(request, response);
    }
}