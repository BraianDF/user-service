package br.com.user_service.service;

import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.dto.response.UsuarioListarResponseDTO;
import br.com.user_service.exceptions.RecursoNaoEncontradoException;
import br.com.user_service.mapper.UsuarioMapper;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioMapper mapper;
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioMapper mapper, UsuarioRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
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
    public UsuarioDetalhesResponseDTO buscarPorId(UUID idUsuario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        return mapper.toDetalhesResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDetalhesResponseDTO buscarPorUsuario(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return mapper.toDetalhesResponseDTO(usuario);
    }

    private Usuario buscarUsuarioPorId(UUID idUsuario) {
        Usuario usuario = repository.findByPublicId(idUsuario);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário com ID " + idUsuario + " não encontrado.");
        }
        return usuario;
    }
}
