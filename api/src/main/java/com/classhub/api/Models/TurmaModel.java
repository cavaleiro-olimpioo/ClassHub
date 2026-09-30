package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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
@Table (name = "tb_turma")
public class TurmaModel {
    /** Identificador único da turma. */
    @Id 
    @Column 
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id_turma;

    /** Série à qual a turma pertence. */
    @Column
    @Getter 
    @Setter
    private int serie;

    /** Letra/identificação da turma. */
    @Column 
    @Getter 
    @Setter 
    private char nome;

    /** Turno da turma. */
    @Column 
    @Getter 
    @Setter 
    private String turno;

    /** Cada turma possui um único professor responsável. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    @Getter @Setter
    private ProfessorModel professorResponsavel;

    /** Matrículas de alunos nesta turma. */
    @OneToMany(mappedBy = "turma")
    @Getter @Setter
    private java.util.List<MatriculaModel> matriculas = new java.util.ArrayList<>();

    /** Disciplinas lecionadas nesta turma. */
    @OneToMany(mappedBy = "turma")
    @Getter @Setter
    private java.util.List<TurmaDisciplinaModel> disciplinas = new java.util.ArrayList<>();


}
