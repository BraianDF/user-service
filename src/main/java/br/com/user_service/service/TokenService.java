package br.com.user_service.service;

import br.com.user_service.enums.Role;
import br.com.user_service.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${info.app.name}")
    private String service;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer(service) //nome da aplicação
                    .withJWTId(UUID.randomUUID().toString()) //id do token
                    .withIssuedAt(Instant.now()) //quando o token foi gerado
                    .withExpiresAt(genExpirationDate(usuario.getDuracaoToken())) //tempo de expiração do token
                    .withSubject(usuario.getPublicId().toString()) //quem solicitou o token
                    .withClaim("roles", usuario.getRoles().stream().map(Role::name).toList()) //permissoes do usuario
                    .sign(algorithm); //assinatura
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro enquanto gerava o token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(service)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token inválido ou expirado");
        }
    }

    private Instant genExpirationDate(Long minutos) {
        return Instant.now().plus(minutos, ChronoUnit.MINUTES);
    }
}
