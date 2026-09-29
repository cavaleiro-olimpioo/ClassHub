package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Representa a associação aluno x turma, com os dados da matrícula.
 * <p>
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_matricula", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "turma_id", "ano_letivo"}))
public class MatriculaModel {
    /** Identificador único da matrícula. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_matricula;

    /** Aluno matriculado. */
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;

    /** Turma em que o aluno foi matriculado. */
    @ManyToOne(optional = false) @JoinColumn(name = "turma_id", nullable = false)
    @Getter @Setter private TurmaModel turma;

    /** Ano letivo da matrícula. */
    @Column(nullable = false) @Getter @Setter private int ano_letivo;
    /** Situação da matrícula. */
    @Column(nullable = false) @Getter @Setter private String status;
    /** Data em que a matrícula foi realizada. */
    @Column(nullable = false) @Getter @Setter private LocalDate data_matricula;
}
