package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table (name = "tb_responsavel")
public class ResponsavelModel {
    /** Identificador único do responsável. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_responsavel;

    /** Endereço do responsável. */
    @Column 
    @Getter 
    @Setter 
    private String endereco;

    /** Grau de parentesco com o aluno. */
    @Column 
    @Getter 
    @Setter 
    private String parentesco;

    /** Alunos vinculados a este responsável. */
    @OneToMany(mappedBy = "responsavel")
    @Getter @Setter
    private java.util.List<AlunoResponsavelModel> alunos = new java.util.ArrayList<>();
}
