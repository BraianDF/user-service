package br.com.user_service.service;

import br.com.exceptions.RegraNegocioException;
import br.com.user_service.dto.request.LoginRequestDTO;
import br.com.user_service.dto.request.RegisterRequestDTO;
import br.com.user_service.dto.response.LoginResponseDTO;
import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.enums.Role;
import br.com.user_service.mapper.UsuarioMapper;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper mapper;

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void loginSucesso() {
        // Arrange (preparação)
        String email = "joao123@email.com";
        String senha = "123456";
        String tokenEsperado = "jwt-token";
        LoginRequestDTO dto = new LoginRequestDTO(email, senha);

        Usuario usuario = new Usuario(email, senha, Set.of(Role.USER));

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(usuario);

        when(tokenService.generateToken(usuario)).thenReturn(tokenEsperado);

        // Act (ação)
        LoginResponseDTO response = authenticationService.login(dto);

        // Assert (validação)
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(tokenEsperado);

        verify(authenticationManager).authenticate(any());
        verify(tokenService).generateToken(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando as credenciais forem inválidas")
    void loginErro1() {
        // Arrange (preparação)
        LoginRequestDTO dto = new LoginRequestDTO("joao@email.com", "senhaErrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act + Assert
        assertThatThrownBy(() -> authenticationService.login(dto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(authenticationManager).authenticate(any());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro ao gerar token")
    void loginErro2() {
        // Arrange (preparação)
        String email = "joao@email.com";
        String senha = "123456";
        LoginRequestDTO dto = new LoginRequestDTO(email, senha);

        Usuario usuario = new Usuario(email, senha, Set.of(Role.USER));

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(usuario);

        when(tokenService.generateToken(usuario)).thenThrow(new RuntimeException("Erro ao gerar token"));

        // Act + Assert
        assertThatThrownBy(() -> authenticationService.login(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao gerar token");
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void registerSucesso() {
        // Arrange
        String email = "joao@email.com";
        String senha = "123456";
        String senhaCriptografada = "senha-criptografada";

        RegisterRequestDTO dto = new RegisterRequestDTO(email, senha);

        UsuarioDetalhesResponseDTO responseDTO = new UsuarioDetalhesResponseDTO(
                UUID.randomUUID(),
                email,
                Set.of(Role.USER),
                true
        );

        when(repository.existsByEmail(email)).thenReturn(false);

        when(passwordEncoder.encode(senha)).thenReturn(senhaCriptografada);

        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toDetalhesResponseDTO(any(Usuario.class))).thenReturn(responseDTO);

        // Act
        UsuarioDetalhesResponseDTO response = authenticationService.register(dto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(email);

        verify(repository).existsByEmail(email);
        verify(passwordEncoder).encode(senha);
        verify(repository).save(any(Usuario.class));
        verify(mapper).toDetalhesResponseDTO(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existir")
    void registerErro1() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO("joao@email.com","123456");

        when(repository.existsByEmail(anyString())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() ->
                authenticationService.register(dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este e-mail já está sendo utilizado.");

        verify(repository).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer violação de integridade")
    void registerErro2() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO("joao@email.com","123456");

        when(repository.existsByEmail(anyString())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("senha-criptografada");

        when(repository.save(any())).thenThrow(new DataIntegrityViolationException("erro"));

        // Act + Assert
        assertThatThrownBy(() ->
                authenticationService.register(dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este e-mail já está sendo utilizado.");
    }
}