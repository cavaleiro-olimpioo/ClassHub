package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Associação aluno × turma, com os dados da matrícula. */
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
