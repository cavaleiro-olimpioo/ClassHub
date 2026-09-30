package com.classhub.api.Util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente utilitário para criptografar senhas e verificar se uma senha
 * em texto puro corresponde a um hash já armazenado, usando o algoritmo BCrypt.
 */
@Component
public class CriptUtil {
    /**
     * Gera o hash BCrypt de uma senha em texto puro.
     *
     * @param password senha em texto puro a ser criptografada
     * @return o hash BCrypt correspondente à senha informada
     */
    public String criptografar(String password){
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode(password);

        return hash;
    }

    /**
     * Verifica se uma senha em texto puro corresponde ao hash armazenado.
     *
     * @param password senha em texto puro informada pelo usuário
     * @param hashPassDB hash da senha armazenado no banco de dados
     * @return {@code true} se a senha corresponder ao hash informado
     */
    public boolean verifyPassword(String password, String hashPassDB){
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.matches(password, hashPassDB);
    }
}
