package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST responsável pelas operações de autenticação da API
 * (login e recuperação de senha). Exposto tanto em {@code /auth} quanto em
 * {@code /api/auth} para compatibilidade com o frontend.
 */
@RestController
@RequestMapping(path = {"/auth", "/api/auth"})
public class AuthController {
    private final AuthService authService;
    /**
     * Cria o controlador injetando o serviço de autenticação.
     *
     * @param authService serviço responsável pela lógica de autenticação
     */
    public AuthController(AuthService authService) { this.authService = authService; }

    /**
     * Realiza o login do usuário validando e-mail e senha.
     *
     * @param request credenciais informadas (e-mail e senha)
     * @return token JWT, perfil e nome do usuário autenticado
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }

    /**
     * Endpoint de recuperação de senha.
     * <p>
     * Por segurança, sempre retorna a mesma mensagem genérica, independente
     * de o e-mail informado existir ou não na base de dados.
     *
     * @param ignored corpo da requisição (não utilizado atualmente)
     * @return mensagem genérica de confirmação
     */
    @PostMapping("/recuperar-senha")
    public MessageResponse recuperarSenha(@RequestBody java.util.Map<String, String> ignored) {
        return new MessageResponse("Se o e-mail estiver cadastrado, você receberá as instruções de recuperação.");
    }
}
