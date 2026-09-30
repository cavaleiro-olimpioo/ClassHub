package com.classhub.api.Models;

import lombok.Getter;
import lombok.Setter;

/**
 * Modelo legado usado como payload de autenticação, mantido apenas como
 * referência histórica. Não é utilizado pelo fluxo de autenticação atual.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
public class User {
    /** Nome de usuário informado no login. */
    @Getter
    @Setter
    private String name;

    /** Senha informada no login. */
    @Getter
    @Setter
    private String password;

    /** Papel/perfil informado no login (ex.: "aluno", "professor"). */
    @Getter
    @Setter
    private String whoami;

    /**
     * Cria um objeto de dados de login.
     *
     * @param name nome de usuário
     * @param password senha em texto puro
     * @param whoami papel/perfil do usuário
     */
    public User(String name, String password, String whoami){
        this.name = name;
        this.password = password;
        this.whoami = whoami;
    }


}
