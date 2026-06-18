package br.com.user_service.service;

import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private UsuarioRepository repository;

    @Test
    @DisplayName("Deve retornar usuário quando encontrado pelo email")
    void loadUserByUsernameSucesso() {
        // Arrange
        String email = "joao@email.com";

        Usuario usuario = new Usuario(email,"123456", Set.of(Role.USER));

        when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails response = authorizationService.loadUserByUsername(email);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(email);

        verify(repository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void loadUserByUsernameErro() {
        // Arrange
        String email = "joao@email.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                authorizationService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado");

        verify(repository).findByEmail(email);
    }
}