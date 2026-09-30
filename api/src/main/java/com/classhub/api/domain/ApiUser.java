package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe base abstrata para todos os tipos de usuário do sistema
 * (aluno, professor e funcionário), usando estratégia de herança
 * {@code TABLE_PER_CLASS} (cada subclasse tem sua própria tabela).
 * <p>
 * Concentra os dados comuns a qualquer usuário: identificação, credenciais
 * de acesso e dados pessoais básicos.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter @Setter @NoArgsConstructor
public abstract class ApiUser {
    /** Identificador único do usuário. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /** E-mail do usuário, usado como identificador de login (único). */
    @Column(nullable = false, unique = true)
    private String email;
    /** Hash da senha do usuário (nunca a senha em texto puro). */
    @Column(nullable = false)
    private String senhaHash;
    /** Nome completo do usuário. */
    @Column(nullable = false)
    private String nome;
    /** Telefone de contato do usuário. */
    @Column
    private String telefone;
    /** Data de nascimento do usuário. */
    @Column
    private java.time.LocalDate dataNascimento;

    /** Sexo/gênero do usuário. */
    @Column
    private String sexo;
    /** CPF do usuário. */
    @Column
    private String cpf;
    /** URL/caminho da foto de perfil do usuário. */
    @Column
    private String foto;

    /**
     * Retorna o perfil (papel/role) do usuário, usado para controle de
     * acesso e para identificar o tipo concreto de usuário.
     *
     * @return o perfil do usuário, ex.: {@code "ALUNO"}, {@code "PROFESSOR"} ou {@code "FUNCIONARIO"}
     */
    public abstract String getPerfil();
}
