package com.classhub.api.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Associação das disciplinas ministradas em cada turma. */
/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
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
