package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Ocorrência criada por um professor para um aluno. */
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
