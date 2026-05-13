package br.com.user_service.mapper;

import br.com.user_service.dto.response.UsuarioDetalhesResponseDTO;
import br.com.user_service.dto.response.UsuarioListarResponseDTO;
import br.com.user_service.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDetalhesResponseDTO toResponseDetalhesDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDetalhesResponseDTO(
            usuario.getPublicId(),
            usuario.getEmail(),
            usuario.getRoles(),
            usuario.getAtivo()
        );
    }

    public UsuarioListarResponseDTO toResponseListarDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioListarResponseDTO(
                usuario.getPublicId(),
                usuario.getEmail(),
                usuario.getAtivo()
        );
    }
}
