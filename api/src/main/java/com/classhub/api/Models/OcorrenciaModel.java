package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Ocorrência criada por um professor para um aluno. */
/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_ocorrencia")
public class OcorrenciaModel {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_ocorrencia;
    @ManyToOne(optional = false) @JoinColumn(name = "professor_id", nullable = false)
    @Getter @Setter private ProfessorModel professor;
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    @Column(nullable = false) @Getter @Setter private LocalDate data;
    @Column(nullable = false) @Getter @Setter private String tipo;
    @Column(nullable = false, length = 2000) @Getter @Setter private String descricao;
}
