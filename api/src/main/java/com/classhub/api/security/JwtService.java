package com.classhub.api.security;

import com.classhub.api.domain.ApiUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Serviço responsável por gerar e validar tokens JWT usados para
 * autenticação stateless na API do ClassHub.
 */
@Service
public class JwtService {
    /** Chave secreta usada para assinar e verificar os tokens. */
    private final SecretKey key;
    /** Tempo de expiração dos tokens gerados, em horas. */
    private final long expirationHours;

    /**
     * Cria o serviço de JWT.
     *
     * @param secret segredo usado para gerar a chave de assinatura HMAC (configurável via {@code app.jwt.secret})
     * @param expirationHours quantidade de horas até o token expirar (configurável via {@code app.jwt.expiration-hours})
     */
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-hours:8}") long expirationHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
    }

    /**
     * Gera um novo token JWT para o usuário informado, contendo o
     * identificador do usuário como sujeito e o perfil/e-mail como claims
     * adicionais.
     *
     * @param user usuário autenticado para o qual o token será gerado
     * @return o token JWT assinado, em formato compacto
     */
    public String generate(ApiUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("perfil", user.getPerfil())
            .claim("email", user.getEmail())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
            .signWith(key)
            .compact();
    }

    /**
     * Decodifica e valida a assinatura de um token JWT, retornando suas claims.
     *
     * @param token token JWT recebido na requisição
     * @return as claims (dados) contidas no token
     * @throws io.jsonwebtoken.JwtException se o token for inválido, malformado ou estiver expirado
     */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
