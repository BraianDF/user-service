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
    private final PasswordEncoder passwordEncoder;

    @Value("${api.security.admin.email}")
    private String email;

    @Value("${api.security.admin.password}")
    private String senha;

    public AdminInitializer(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Usuario usuario = repository.findByEmail(email);

        if (usuario == null) {
            criarAdmin();
            return;
        }

        System.out.println("USER-SERVICE: Admin criado anteriormente.");

        boolean alterado = false;

        if (Boolean.TRUE.equals(usuario.getStatus())) {
            System.out.println("USER-SERVICE: Admin está ativo.");
        } else {
            usuario.setStatus(true);
            System.out.println("USER-SERVICE: Admin ativado.");
            alterado = true;
        }

        if (passwordEncoder.matches(senha, usuario.getSenha())) {
            System.out.println("USER-SERVICE: Não houve alteração na senha do Admin.");
        } else {
            String encryptedPassword = passwordEncoder.encode(senha);
            usuario.setSenha(encryptedPassword);
            System.out.println("USER-SERVICE: Senha do Admin resetada com sucesso.");
            alterado = true;
        }

        if (usuario.getRoles().contains(Role.ADMIN)) {
            System.out.println("USER-SERVICE: Não houve alteração na permissão do Admin.");
        } else {
            usuario.adicionarRole(Role.ADMIN);
            System.out.println("USER-SERVICE: Permissões do Admin resetada com sucesso.");
            alterado = true;
        }

        if (alterado) {
            repository.save(usuario);
        }

    }

    private void criarAdmin() {
        String encryptedPassword = passwordEncoder.encode(senha);

        Usuario novoUsuario = new Usuario(email, encryptedPassword, Set.of(Role.ADMIN));

        repository.save(novoUsuario);

        System.out.println("USER-SERVICE: Admin criado com sucesso.");
    }
}
