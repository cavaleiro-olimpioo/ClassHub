package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }

    @PostMapping("/recuperar-senha")
    public MessageResponse recuperarSenha(@RequestBody java.util.Map<String, String> ignored) {
        return new MessageResponse("Se o e-mail estiver cadastrado, você receberá as instruções de recuperação.");
    }
}
