package br.com.user_service.controller;

import br.com.user_service.dto.request.LoginRequestDTO;
import br.com.user_service.dto.response.LoginResponseDTO;
import br.com.user_service.dto.request.RegisterRequestDTO;
import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginResponseDTO response = service.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDetalhesResponseDTO> registerPublic(@RequestBody @Valid RegisterRequestDTO dto) {
        UsuarioDetalhesResponseDTO response = service.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
