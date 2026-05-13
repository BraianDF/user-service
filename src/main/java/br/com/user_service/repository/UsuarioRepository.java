package br.com.user_service.repository;

import br.com.user_service.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByEmail(String email);
    Usuario findByPublicId(UUID publicId);
    boolean existsByEmail(String email);
    Page<Usuario> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
