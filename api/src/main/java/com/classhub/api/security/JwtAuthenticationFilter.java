package com.classhub.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Filtro de autenticação executado uma vez por requisição, responsável por
 * ler o token JWT do cabeçalho {@code Authorization: Bearer ...}, validá-lo
 * e popular o contexto de segurança do Spring com o usuário autenticado.
 * <p>
 * Requisições sem token são simplesmente repassadas adiante (o acesso será
 * negado mais tarde pelas regras de autorização, caso a rota exija
 * autenticação). Requisições com token inválido ou expirado recebem
 * imediatamente uma resposta 401.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    /**
     * Cria o filtro de autenticação JWT.
     *
     * @param jwtService serviço usado para validar e decodificar o token
     * @param objectMapper usado para escrever a resposta JSON em caso de erro de autenticação
     */
    public JwtAuthenticationFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    /**
     * Executa a lógica do filtro: extrai o token do cabeçalho
     * {@code Authorization}, valida-o e, se válido, autentica o usuário no
     * contexto de segurança com uma autoridade baseada no perfil
     * ({@code ROLE_ALUNO}, {@code ROLE_PROFESSOR} ou {@code ROLE_FUNCIONARIO}).
     *
     * @param request requisição HTTP recebida
     * @param response resposta HTTP a ser preenchida em caso de erro
     * @param filterChain cadeia de filtros a ser continuada
     * @throws ServletException se ocorrer um erro no processamento do filtro
     * @throws IOException se ocorrer um erro de I/O ao escrever a resposta
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Claims claims = jwtService.parse(header.substring(7));
            String userId = claims.getSubject();
            String perfil = claims.get("perfil", String.class);
            if (userId == null || perfil == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                unauthorized(response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + ("FUNCIONARIO".equals(perfil) ? "ADMIN" : perfil))));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            unauthorized(response);
        }
    }

    /**
     * Escreve uma resposta HTTP 401 (Não Autorizado) em formato JSON,
     * usada quando o token informado é inválido ou expirado.
     *
     * @param response resposta HTTP a ser preenchida
     * @throws IOException se ocorrer um erro de I/O ao escrever a resposta
     */
    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of("mensagem", "Token inválido ou expirado."));
    }
}
