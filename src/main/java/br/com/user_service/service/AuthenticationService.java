package br.com.user_service.service;

import br.com.user_service.dto.request.LoginRequestDTO;
import br.com.user_service.dto.response.LoginResponseDTO;
import br.com.user_service.dto.request.RegisterRequestDTO;
import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.enums.Role;
import br.com.user_service.exceptions.RegraNegocioException;
import br.com.user_service.mapper.UsuarioMapper;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository repository;
    private final TokenService tokenService;
    private final UsuarioMapper mapper;

    public AuthenticationService(AuthenticationManager authenticationManager, UsuarioRepository repository, TokenService tokenService, UsuarioMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }

    @Transactional
    public UsuarioDetalhesResponseDTO register(RegisterRequestDTO dto) {

        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Este e-mail já está sendo utilizado.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.senha());

        Usuario usuario = new Usuario(dto.email(), encryptedPassword, Set.of(Role.USER));
        repository.save(usuario);

        return mapper.toDetalhesResponseDTO(usuario);
    }

}
