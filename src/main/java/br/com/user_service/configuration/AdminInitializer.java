package br.com.user_service.configuration;

import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository repository;

    @Value("${api.security.admin.email}")
    private String email;

    @Value("${api.security.admin.password}")
    private String senha;

    public AdminInitializer(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        Usuario usuario = repository.findByEmail(email);

        if (usuario == null) {
            criarAdmin();
            return;
        }

        System.out.println("USER-SERVICE: Admin criado anteriormente.");

        if (Boolean.TRUE.equals(usuario.getStatus())) {
            System.out.println("USER-SERVICE: Admin está ativo.");
            return;
        }

        usuario.setStatus(true);
        repository.save(usuario);

        System.out.println("USER-SERVICE: Admin ativado.");
    }

    private void criarAdmin() {
        String encryptedPassword = new BCryptPasswordEncoder().encode(senha);

        Usuario novoUsuario = new Usuario(email, encryptedPassword, Set.of(Role.ADMIN));

        repository.save(novoUsuario);

        System.out.println("USER-SERVICE: Admin criado com sucesso.");
    }
}
