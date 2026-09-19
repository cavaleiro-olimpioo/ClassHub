package com.classhub.api.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Associação das disciplinas ministradas em cada turma. */
@Entity
@Table(name = "tb_turma_disciplina", uniqueConstraints = @UniqueConstraint(columnNames = {"turma_id", "disciplina_id"}))
public class TurmaDisciplinaModel {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_turma_disciplina;
    @ManyToOne(optional = false) @JoinColumn(name = "turma_id", nullable = false)
    @Getter @Setter private TurmaModel turma;
    @ManyToOne(optional = false) @JoinColumn(name = "disciplina_id", nullable = false)
    @Getter @Setter private DiciplinaModel disciplina;
}
