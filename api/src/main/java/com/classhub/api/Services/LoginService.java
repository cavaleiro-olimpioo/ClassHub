package com.classhub.api.Services;

import org.springframework.stereotype.Service;
import com.classhub.api.Util.CriptUtil;
import java.util.List;

@Service
public class LoginService {
 
    private CriptUtil bcrypt;
    private boolean[] verify = new boolean[2];
    private List<String[]> users = List.of(
        new String[]{"Guilherme", "$2a$10$kfEqtw.Ys0C4/jy4E3LEw.5HdeBC9M45dt6/EZJiMYQAYAn.HQRIK", "aluno"},
        new String[]{"Luiz", "$2a$10$DR9Qmy3EeQt9/pxXGEwPn.P6L652Lw6obDzs..0LE6H2L1nmt84R2", "aluno"},
        new String[]{"Thiago", "$2a$10$urgKP/r43z.LzN.XfEG7ZeOa00vUAWiCMd2bxcw0xMdmZGRWV0.Fy", "professor"},
        new String[]{"Nigga", "$2a$10$Keqs6c/oz609rbVCEf4n9.b6EB5X17Ryw1Nhov7DogBztXMOgKzJi", "professor"},
        new String[]{"Enzo", "$2a$10$2dXA.3FBCqB/5mZ8iA2Jle3lq1sBAPlMOiI4OP332db8BscmdFEoi", "diretor"}
    );

    public LoginService(){
        this.bcrypt = new CriptUtil();
    }

    public boolean[] verifyLogin(String username, String password, String whoami){
        for(String[] user : users){
            if(user[0].equals(username) && user[2].equals(whoami)){
                verify[0] = true;
                verify[1] = bcrypt.verifyPassword(password, user[1]);
                break;
            }
        }
        
        return verify;
    }
}
