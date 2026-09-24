package com.classhub.api.service;

import com.classhub.api.domain.ApiUser;
import com.classhub.api.dto.ApiDtos.LoginRequest;
import com.classhub.api.dto.ApiDtos.LoginResponse;
import com.classhub.api.exception.ApiExceptions.UnauthorizedException;
import com.classhub.api.repository.ApiUserRepository;
import com.classhub.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ApiUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ApiUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        ApiUser user = users.findByEmailIgnoreCase(request.email().trim())
            .orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos."));
        if (!passwordEncoder.matches(request.senha(), user.getSenhaHash())) {
            throw new UnauthorizedException("E-mail ou senha inválidos.");
        }
        return new LoginResponse(jwtService.generate(user), user.getPerfil(), user.getNome());
    }
}
