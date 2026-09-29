package com.classhub.api.service;

import com.classhub.api.domain.ApiUser;
import com.classhub.api.dto.ApiDtos.LoginRequest;
import com.classhub.api.dto.ApiDtos.LoginResponse;
import com.classhub.api.exception.ApiExceptions.UnauthorizedException;
import com.classhub.api.repository.ApiUserRepository;
import com.classhub.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela autenticação de usuários (alunos, professores e
 * funcionários) na API do ClassHub.
 * <p>
 * Valida as credenciais informadas contra a base de usuários e, em caso de
 * sucesso, gera um token JWT para as requisições subsequentes.
 */
@Service
public class AuthService {
    private final ApiUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Cria o serviço de autenticação.
     *
     * @param users repositório de usuários (busca por e-mail)
     * @param passwordEncoder codificador usado para comparar a senha informada com o hash armazenado
     * @param jwtService serviço responsável por gerar o token JWT
     */
    public AuthService(ApiUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autentica um usuário a partir de e-mail e senha.
     *
     * @param request e-mail e senha informados no login
     * @return token JWT, perfil e nome do usuário autenticado
     * @throws UnauthorizedException se o e-mail não existir ou a senha estiver incorreta
     */
    public LoginResponse login(LoginRequest request) {
        ApiUser user = users.findByEmailIgnoreCase(request.email().trim())
            .orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos."));
        if (!passwordEncoder.matches(request.senha(), user.getSenhaHash())) {
            throw new UnauthorizedException("E-mail ou senha inválidos.");
        }
        return new LoginResponse(jwtService.generate(user), user.getPerfil(), user.getNome());
    }
}
