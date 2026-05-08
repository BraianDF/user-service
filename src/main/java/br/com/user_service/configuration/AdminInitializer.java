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

        if (repository.existsByEmail(email)) {
            System.out.println("Admin já foi criado anteriormente.");
            return;
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(senha);

        Usuario usuario = new Usuario(email, encryptedPassword, Set.of(Role.ADMIN));
        repository.save(usuario);

        System.out.println("Admin criado com sucesso.");
    }
}
