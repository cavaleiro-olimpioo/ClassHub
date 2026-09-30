package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência base legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@MappedSuperclass 
public class UserModel {
    /** Identificador único do usuário. */
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @Getter 
    @Setter
    private int id_usuario;

    /** Nome completo do usuário. */
    @Column
    @Getter
    @Setter 
    private String nome;

    /** E-mail do usuário. */
    @Column
    @Getter 
    @Setter
    private String email;

    /** Senha (ou hash de senha) do usuário. */
    @Column
    @Getter
    @Setter
    private String password;

    /** Telefone de contato do usuário. */
    @Column
    @Getter 
    @Setter 
    private String telefone;
}
