package com.classhub.api.Util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CriptUtil {
    public String criptografar(String password){
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode(password);

        return hash;
    }

    public boolean verifyPassword(String password, String hashPassDB){
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.matches(password, hashPassDB);
    }
}
