package br.com.user_service.service;

import br.com.user_service.dto.request.UsuarioAtualizarEmailRequestDTO;
import br.com.user_service.dto.request.UsuarioAtualizarStatusRequestDTO;
import br.com.user_service.dto.request.UsuarioCadastrarRequestDTO;
import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.dto.response.UsuarioListarResponseDTO;
import br.com.user_service.exceptions.RecursoNaoEncontradoException;
import br.com.user_service.exceptions.RegraNegocioException;
import br.com.user_service.mapper.UsuarioMapper;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import br.com.user_service.utils.TextoUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioMapper mapper;
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioMapper mapper, UsuarioRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Transactional
    public UsuarioDetalhesResponseDTO cadastrar(UsuarioCadastrarRequestDTO dto) {

        if (repository.existsByEmail(TextoUtils.normalizarMinusculo(dto.email()))) {
            throw new RegraNegocioException("Este e-mail já está sendo utilizado.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.senha());

        Usuario usuario = new Usuario(dto.email(), encryptedPassword, dto.roles());
        repository.save(usuario);

        return mapper.toDetalhesResponseDTO(usuario);
    }

    @Transactional
    public void excluir(UUID idUsuario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        repository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioListarResponseDTO> listar(Pageable pageable, String email) {
        if (email == null || email.isBlank()) {
            return repository.findAll(pageable)
                    .map(mapper::toListarResponseDTO);
        }
        return repository.findByEmailContainingIgnoreCase(email, pageable)
                .map(mapper::toListarResponseDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioDetalhesResponseDTO buscar(UUID idUsuario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        return mapper.toDetalhesResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDetalhesResponseDTO buscar(Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        return mapper.toDetalhesResponseDTO(usuario);
    }

    @Transactional
    public UsuarioDetalhesResponseDTO atualizarEmail(UUID idUsuario, UsuarioAtualizarEmailRequestDTO dto) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        return mapper.toDetalhesResponseDTO(atualizarEmail(usuario, dto));
    }

    @Transactional
    public UsuarioDetalhesResponseDTO atualizarEmail(Authentication authentication, UsuarioAtualizarEmailRequestDTO dto) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        return mapper.toDetalhesResponseDTO(atualizarEmail(usuario, dto));
    }

    @Transactional
    public UsuarioDetalhesResponseDTO atualizarStatus(UUID idUsuario, UsuarioAtualizarStatusRequestDTO dto) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        return mapper.toDetalhesResponseDTO(atualizarStatus(usuario, dto));
    }

    @Transactional
    public UsuarioDetalhesResponseDTO atualizarStatus(Authentication authentication, UsuarioAtualizarStatusRequestDTO dto) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        return mapper.toDetalhesResponseDTO(atualizarStatus(usuario, dto));
    }

    private Usuario buscarUsuarioPorId(UUID idUsuario) {
        Usuario usuario = repository.findByPublicId(idUsuario);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário com ID " + idUsuario + " não encontrado.");
        }
        return usuario;
    }

    private Usuario buscarUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado.");
        }
        return (Usuario) authentication.getPrincipal();
    }

    private Usuario atualizarEmail(Usuario usuario, UsuarioAtualizarEmailRequestDTO dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            throw new RegraNegocioException("Email é obrigatório.");
        }

        if (TextoUtils.equalsNormalizado(usuario.getEmail(), dto.email())) {
            return usuario;
        }

        if (repository.existsByEmailAndPublicIdNot(TextoUtils.normalizarMinusculo(dto.email()), usuario.getPublicId())) {
            throw new RegraNegocioException("Este e-mail já está sendo utilizado.");
        }

        usuario.setEmail(dto.email());

        return repository.save(usuario);
    }

    private Usuario atualizarStatus(Usuario usuario, UsuarioAtualizarStatusRequestDTO dto) {
        if (dto.status() == null) {
            throw new RegraNegocioException("Status é obrigatório.");
        }

        if (Objects.equals(usuario.getStatus(), dto.status())) {
            return usuario;
        }

        usuario.setStatus(dto.status());

        return repository.save(usuario);
    }
}
