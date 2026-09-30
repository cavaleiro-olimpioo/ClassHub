package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa uma nota (avaliação) lançada para um aluno em
 * uma disciplina/turma específicas, dentro de um bimestre.
 * <p>
 * A combinação (aluno, disciplina, bimestre, tipo) é única, ou seja, só pode
 * existir uma nota de cada {@code tipo} por aluno/disciplina/bimestre.
 */
@Entity
@Table(name = "api_notas", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id", "bimestre", "tipo"}))
@Getter @Setter @NoArgsConstructor
public class ApiNota {
    /** Identificador único da nota. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Aluno avaliado. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "aluno_id") private ApiAluno aluno;
    /** Disciplina referente à avaliação. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    /** Turma do aluno no momento da avaliação. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    /** Número do bimestre (1 a 4) a que a nota se refere. */
    @Column(nullable = false) private Integer bimestre;
    /** Tipo/identificação da avaliação (ex.: "PROVA", "TRABALHO"). */
    @Column(nullable = false) private String tipo;
    /** Peso da avaliação no cálculo da média do bimestre. */
    @Column(nullable = false) private Double peso;
    /** Valor da nota obtida (0.0 a 10.0). */
    @Column(nullable = false) private Double valor;
}
