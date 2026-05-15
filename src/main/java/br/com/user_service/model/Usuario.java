package br.com.user_service.model;

import br.com.user_service.enums.Role;
import br.com.user_service.utils.TextoUtils;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idUsuario;

    @Column(name = "publicId", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "senha", length = 100, nullable = false)
    private String senha;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = Role.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuarioRoles", joinColumns = @JoinColumn(name = "idUsuario"))
    @Column(name = "roles", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "status")
    private Boolean status;

    public Usuario() {
    }

    public Usuario(Long idUsuario, String email, String senha, Set<Role> roles) {
        this.idUsuario = idUsuario;
        this.email = TextoUtils.normalizarMinusculo(email);
        this.senha = senha;
        this.roles = roles;
    }

    public Usuario(String email, String senha, Set<Role> roles) {
        this.email = TextoUtils.normalizarMinusculo(email);
        this.senha = senha;
        this.roles = roles;
    }

    @PrePersist
    public void prePersist() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID();
        }

        if (this.status == null) {
            this.status = true;
        }
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = TextoUtils.normalizarMinusculo(email);
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void adicionarRole(Role role) {
        this.roles.add(role);
    }

    public void removerRole(Role role) {
        this.roles.remove(role);
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public long getDuracaoToken() {
        return roles.stream()
                .mapToLong(Role::getExpirationMinutes)
                .max()
                .orElse(0);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.status);
    }
}
