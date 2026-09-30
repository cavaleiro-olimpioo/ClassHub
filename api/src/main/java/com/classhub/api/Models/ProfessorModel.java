package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_professor")
public class ProfessorModel extends UserModel {
    /** Formação acadêmica do professor. */
    @Column
    @Getter 
    @Setter
    private String formacao;

    /** Identificador textual do tipo de usuário (sempre "professor"). */
    @Column
    @Getter 
    @Setter 
    private String whoami = "professor";

    /** Turmas pelas quais o professor é responsável. */
    @OneToMany(mappedBy = "professorResponsavel")
    @Getter @Setter
    private java.util.List<TurmaModel> turmasResponsaveis = new java.util.ArrayList<>();

    /** Ocorrências geradas por este professor. */
    @OneToMany(mappedBy = "professor")
    @Getter @Setter
    private java.util.List<OcorrenciaModel> ocorrenciasGeradas = new java.util.ArrayList<>();
}
