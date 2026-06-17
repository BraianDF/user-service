package br.com.user_service.service;

import br.com.user_service.dto.request.LoginRequestDTO;
import br.com.user_service.dto.response.LoginResponseDTO;
import br.com.user_service.dto.request.RegisterRequestDTO;
import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.enums.Role;
import br.com.exceptions.RegraNegocioException;
import br.com.user_service.mapper.UsuarioMapper;
import br.com.user_service.model.Usuario;
import br.com.user_service.repository.UsuarioRepository;
import br.com.utils.TextoUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository repository;
    private final TokenService tokenService;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, UsuarioRepository repository, TokenService tokenService, UsuarioMapper mapper, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.tokenService = tokenService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(TextoUtils.normalizarMinusculo(dto.email()), dto.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }

    @Transactional
    public UsuarioDetalhesResponseDTO register(RegisterRequestDTO dto) {

        if (repository.existsByEmail(TextoUtils.normalizarMinusculo(dto.email()))) {
            throw new RegraNegocioException("Este e-mail já está sendo utilizado.");
        }

        try {
            String encryptedPassword = passwordEncoder.encode(dto.senha());

            Usuario usuario = new Usuario(dto.email(), encryptedPassword, Set.of(Role.USER));
            repository.save(usuario);

            return mapper.toDetalhesResponseDTO(usuario);

        } catch (DataIntegrityViolationException e) {
            throw new RegraNegocioException("Este e-mail já está sendo utilizado.");
        }

    }

}
