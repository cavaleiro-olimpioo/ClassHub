package com.classhub.api.Controller;

import com.classhub.api.Models.User;
import com.classhub.api.Services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador legado de login, mantido apenas como referência histórica.
 * Não faz parte do fluxo de autenticação atual da aplicação (ver
 * {@link com.classhub.api.Controller.AuthController} e
 * {@code com.classhub.api.service.AuthService}).
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    /** Resultado da última verificação de login: [usuário válido, senha válida]. */
    private boolean[] verify = new boolean[2];

    /**
     * Recebe os dados de login e verifica se o usuário e a senha informados
     * são válidos usando a implementação legada de {@link LoginService}.
     *
     * @param user dados de login (nome, senha e papel/whoami)
     * @return vetor booleano indicando o resultado da verificação
     */
    @PostMapping
    public boolean[] returnData(@RequestBody User user){
        LoginService loginVerify = new LoginService();
        verify = loginVerify.verifyLogin(user.getName(), user.getPassword(), user.getWhoami());
        return verify;
    }
}
