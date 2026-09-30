package com.classhub.api.Config;

import com.classhub.api.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Configuração central de segurança da API do ClassHub.
 * <p>
 * Define a cadeia de filtros de segurança (autenticação via JWT, sem
 * sessão/stateless), as regras de CORS, o serializador JSON padrão e o
 * codificador de senhas usado em toda a aplicação.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Cria o {@link ObjectMapper} usado para serializar/desserializar JSON
     * na aplicação, com suporte a tipos de data/hora do Java 8+ (ex.:
     * {@code LocalDate}) representados como texto (ISO), em vez de timestamps numéricos.
     *
     * @return o {@code ObjectMapper} configurado
     */
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Define a cadeia de filtros de segurança HTTP da aplicação: desabilita
     * CSRF e login por formulário/HTTP básico (a API é stateless e usa
     * JWT), configura o tratamento de erros de autenticação (retorna JSON
     * com status 401), define quais rotas são públicas (health check e
     * login) e insere o filtro {@link JwtAuthenticationFilter} antes do
     * filtro padrão de autenticação por usuário/senha.
     *
     * @param http builder de configuração de segurança HTTP do Spring Security
     * @param jwtFilter filtro responsável por validar o token JWT em cada requisição
     * @param objectMapper usado para escrever a resposta JSON de erro de autenticação
     * @return a cadeia de filtros de segurança configurada
     * @throws Exception se ocorrer erro ao construir a configuração
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter, ObjectMapper objectMapper) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> { })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, ex) -> {
                response.setStatus(401);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(), java.util.Map.of("mensagem", "Não autorizado."));
            }))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                .requestMatchers(HttpMethod.POST,
                    "/auth/login",
                    "/api/auth/login",
                    "/auth/recuperar-senha",
                    "/api/auth/recuperar-senha").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configura as regras de CORS da API, permitindo requisições de
     * origens específicas (configuráveis via propriedade
     * {@code app.cors.allowed-origins}), com os métodos e cabeçalhos
     * necessários para o frontend consumir a API.
     *
     * @param origins lista de origens permitidas, separadas por vírgula
     * @return a fonte de configuração de CORS usada pelo Spring Security
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:5432}") String origins) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).filter(value -> !value.isEmpty()).toList());
        cors.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(java.util.List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT));
        cors.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    /**
     * Cria o codificador de senhas padrão da aplicação, usando o
     * algoritmo BCrypt.
     *
     * @return o codificador de senhas
     */
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
