package com.classhub.api.Services;

import org.springframework.stereotype.Service;
import com.classhub.api.Util.CriptUtil;
import java.util.List;

/**
 * Serviço legado de autenticação, mantido apenas como referência histórica.
 * Utiliza uma lista fixa de usuários em memória (não faz parte do fluxo de
 * autenticação atual, que usa {@link com.classhub.api.domain.ApiUser} e
 * JWT — ver {@code com.classhub.api.service.AuthService}).
 */
@Service
public class LoginService {
 
    /** Utilitário usado para verificar a senha informada contra o hash armazenado. */
    private CriptUtil bcrypt;
    /** Resultado da última verificação de login: [usuário encontrado, senha válida]. */
    private boolean[] verify = new boolean[2];
    /** Lista fixa (em memória) de usuários de exemplo: [nome, hash da senha, papel]. */
    private List<String[]> users = List.of(
        new String[]{"Guilherme", "$2a$10$kfEqtw.Ys0C4/jy4E3LEw.5HdeBC9M45dt6/EZJiMYQAYAn.HQRIK", "aluno"},
        new String[]{"Luiz", "$2a$10$DR9Qmy3EeQt9/pxXGEwPn.P6L652Lw6obDzs..0LE6H2L1nmt84R2", "aluno"},
        new String[]{"Thiago", "$2a$10$urgKP/r43z.LzN.XfEG7ZeOa00vUAWiCMd2bxcw0xMdmZGRWV0.Fy", "professor"},
        new String[]{"Professor Exemplo", "$2a$10$Keqs6c/oz609rbVCEf4n9.b6EB5X17Ryw1Nhov7DogBztXMOgKzJi", "professor"},
        new String[]{"Enzo", "$2a$10$2dXA.3FBCqB/5mZ8iA2Jle3lq1sBAPlMOiI4OP332db8BscmdFEoi", "diretor"}
    );

    /** Cria o serviço legado de login, instanciando o utilitário de criptografia de senhas. */
    public LoginService(){
        this.bcrypt = new CriptUtil();
    }

    /**
     * Verifica se existe, na lista fixa de usuários, um usuário com o nome
     * e papel informados e se a senha corresponde ao hash armazenado.
     *
     * @param username nome de usuário informado
     * @param password senha em texto puro informada
     * @param whoami papel/perfil informado (ex.: "aluno", "professor", "diretor")
     * @return vetor booleano: posição 0 indica se o usuário/papel foi encontrado,
     *         posição 1 indica se a senha informada é válida
     */
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
