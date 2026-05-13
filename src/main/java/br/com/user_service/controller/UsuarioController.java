package br.com.user_service.controller;

import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.dto.response.UsuarioListarResponseDTO;
import br.com.user_service.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UsuarioListarResponseDTO>> listar(@PageableDefault(page = 0, size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(defaultValue = "") String email) {
        Page<UsuarioListarResponseDTO> response = service.listar(pageable, email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioDetalhesResponseDTO> buscarPorId(@PathVariable UUID idUsuario) {
        UsuarioDetalhesResponseDTO response = service.buscarPorId(idUsuario);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDetalhesResponseDTO> buscarPorUsuario(Authentication authentication) {
        UsuarioDetalhesResponseDTO response = service.buscarPorUsuario(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
