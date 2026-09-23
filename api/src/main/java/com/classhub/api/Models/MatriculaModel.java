package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Associação aluno × turma, com os dados da matrícula. */
/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_matricula", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "turma_id", "ano_letivo"}))
public class MatriculaModel {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_matricula;

    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;

    @ManyToOne(optional = false) @JoinColumn(name = "turma_id", nullable = false)
    @Getter @Setter private TurmaModel turma;

    @Column(nullable = false) @Getter @Setter private int ano_letivo;
    @Column(nullable = false) @Getter @Setter private String status;
    @Column(nullable = false) @Getter @Setter private LocalDate data_matricula;
}
