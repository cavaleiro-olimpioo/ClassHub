package com.classhub.api.Services;

import org.springframework.stereotype.Service;
import com.classhub.api.Util.CriptUtil;

@Service
public class LoginService {
    private String username;
    private String password;
    private CriptUtil bcrypt;
    private boolean[] verify = new boolean[2];

    public LoginService(String username, String password){
        this.username = username;
        this.password = password;
        this.bcrypt = new CriptUtil();
    }

    public boolean[] verifyLogin(){
        return verify;
    }
}
