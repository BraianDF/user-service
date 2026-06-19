package br.com.user_service.configuration;

import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @InjectMocks
    private AdminInitializer adminInitializer;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String EMAIL = "admin@email.com";
    private static final String SENHA = "123456";
    private static final String CRIPTOGRAFADA = "$2a$10.senhaCriptografada";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(adminInitializer, "email",EMAIL);
        ReflectionTestUtils.setField(adminInitializer,"senha",SENHA);
    }

    @Test
    @DisplayName("Deve criar admin quando usuário não existir")
    void runCase1() {
        // Arrange (preparação)
        when(repository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(SENHA))
                .thenReturn(CRIPTOGRAFADA);

        // Act (ação)
        adminInitializer.run();

        // Assert (verificação)
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        verify(repository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();

        assertEquals(EMAIL, usuarioSalvo.getEmail());
        assertEquals(CRIPTOGRAFADA, usuarioSalvo.getSenha());
        assertTrue(usuarioSalvo.getRoles().contains(Role.ADMIN));

        verify(repository).findByEmail(EMAIL);
        verify(passwordEncoder).encode(SENHA);
    }

    @Test
    @DisplayName("Não deve salvar quando admin já estiver configurado corretamente")
    void runCase2() {
        // Arrange (preparação)
        Usuario usuario = new Usuario(EMAIL, CRIPTOGRAFADA, Set.of(Role.ADMIN));
        usuario.setStatus(true);

        when(repository.findByEmail(EMAIL))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(SENHA, CRIPTOGRAFADA))
                .thenReturn(true);

        // Act (ação)
        adminInitializer.run();

        // Assert (verificação)
        verify(repository).findByEmail(EMAIL);
        verify(passwordEncoder).matches(SENHA, CRIPTOGRAFADA);

        verify(repository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve ativar admin quando estiver inativo")
    void runCase3() {
        // Arrange (preparação)
        Usuario usuario = new Usuario(EMAIL, CRIPTOGRAFADA, Set.of(Role.ADMIN));
        usuario.setStatus(false);

        when(repository.findByEmail(EMAIL))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(SENHA, CRIPTOGRAFADA))
                .thenReturn(true);

        // Act (ação)
        adminInitializer.run();

        // Assert (verificação)
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        verify(repository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();

        assertTrue(usuarioSalvo.getStatus());
        assertEquals(EMAIL, usuarioSalvo.getEmail());
        assertEquals(CRIPTOGRAFADA, usuarioSalvo.getSenha());
        assertTrue(usuarioSalvo.getRoles().contains(Role.ADMIN));

        verify(repository).findByEmail(EMAIL);
        verify(passwordEncoder).matches(SENHA, CRIPTOGRAFADA);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve atualizar senha quando estiver diferente")
    void runCase4() {
        // Arrange (preparação)
        String senhaDiferente = "senhaDiferente";

        Usuario usuario = new Usuario(EMAIL, senhaDiferente, Set.of(Role.ADMIN));
        usuario.setStatus(true);

        when(repository.findByEmail(EMAIL))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(SENHA, senhaDiferente))
                .thenReturn(false);

        when(passwordEncoder.encode(SENHA))
                .thenReturn(CRIPTOGRAFADA);

        // Act (ação)
        adminInitializer.run();

        // Assert (verificação)
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        verify(repository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();

        assertTrue(usuarioSalvo.getStatus());
        assertEquals(EMAIL, usuarioSalvo.getEmail());
        assertEquals(CRIPTOGRAFADA, usuarioSalvo.getSenha());
        assertTrue(usuarioSalvo.getRoles().contains(Role.ADMIN));

        verify(repository).findByEmail(EMAIL);
        verify(passwordEncoder).matches(SENHA, senhaDiferente);
        verify(passwordEncoder).encode(SENHA);
    }

    @Test
    @DisplayName("Deve adicionar role ADMIN quando não possuir")
    void runCase5() {
        // Arrange (preparação)
        Usuario usuario = new Usuario(EMAIL, CRIPTOGRAFADA, Set.of(Role.USER));
        usuario.setStatus(true);

        when(repository.findByEmail(EMAIL))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(SENHA, CRIPTOGRAFADA))
                .thenReturn(true);

        // Act (ação)
        adminInitializer.run();

        // Assert (verificação)
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        verify(repository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();

        assertTrue(usuarioSalvo.getStatus());
        assertEquals(EMAIL, usuarioSalvo.getEmail());
        assertEquals(CRIPTOGRAFADA, usuarioSalvo.getSenha());
        assertTrue(usuarioSalvo.getRoles().contains(Role.ADMIN));

        verify(repository).findByEmail(EMAIL);
        verify(passwordEncoder).matches(SENHA, CRIPTOGRAFADA);
        verify(passwordEncoder, never()).encode(anyString());
    }
}