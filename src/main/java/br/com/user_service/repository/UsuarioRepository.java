package br.com.user_service.repository;

import br.com.user_service.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByPublicId(UUID publicId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndPublicIdNot(String email, UUID publicId);
    Page<Usuario> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
