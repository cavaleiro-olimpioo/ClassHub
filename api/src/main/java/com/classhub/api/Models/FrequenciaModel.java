package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_frequencia", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id", "data"}))
public class FrequenciaModel {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_frequencia;
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    @ManyToOne(optional = false) @JoinColumn(name = "disciplina_id", nullable = false)
    @Getter @Setter private DiciplinaModel disciplina;
    @Column(nullable = false) @Getter @Setter private LocalDate data;
    @Column(nullable = false) @Getter @Setter private boolean presente;
}
