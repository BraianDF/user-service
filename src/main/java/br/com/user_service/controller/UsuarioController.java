package br.com.user_service.controller;

import br.com.user_service.dto.request.UsuarioAtualizarEmailRequestDTO;
import br.com.user_service.dto.request.UsuarioCadastrarRequestDTO;
import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.dto.response.UsuarioListarResponseDTO;
import br.com.user_service.service.UsuarioService;
import jakarta.validation.Valid;
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
    @PostMapping
    public ResponseEntity<UsuarioDetalhesResponseDTO> cadastrar(@RequestBody @Valid UsuarioCadastrarRequestDTO dto) {
        UsuarioDetalhesResponseDTO response = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> excluir(@PathVariable UUID idUsuario) {
        service.excluir(idUsuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UsuarioListarResponseDTO>> listar(@PageableDefault(page = 0, size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(defaultValue = "") String email) {
        Page<UsuarioListarResponseDTO> response = service.listar(pageable, email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioDetalhesResponseDTO> buscar(@PathVariable UUID idUsuario) {
        UsuarioDetalhesResponseDTO response = service.buscar(idUsuario);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDetalhesResponseDTO> buscar(Authentication authentication) {
        UsuarioDetalhesResponseDTO response = service.buscar(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{idUsuario}/email")
    public ResponseEntity<UsuarioDetalhesResponseDTO> atualizarEmail(@PathVariable UUID idUsuario, @RequestBody @Valid UsuarioAtualizarEmailRequestDTO dto) {
        UsuarioDetalhesResponseDTO response = service.atualizarEmail(idUsuario, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/me/email")
    public ResponseEntity<UsuarioDetalhesResponseDTO> atualizarEmail(Authentication authentication, @RequestBody @Valid UsuarioAtualizarEmailRequestDTO dto) {
        UsuarioDetalhesResponseDTO response = service.atualizarEmail(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
