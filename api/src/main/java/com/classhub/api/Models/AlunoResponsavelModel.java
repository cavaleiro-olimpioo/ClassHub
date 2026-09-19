package com.classhub.api.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Associação aluno × responsável; define quem responde financeiramente. */
@Entity
@Table(name = "tb_aluno_responsavel", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "responsavel_id"}))
public class AlunoResponsavelModel {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_aluno_responsavel;
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    @ManyToOne(optional = false) @JoinColumn(name = "responsavel_id", nullable = false)
    @Getter @Setter private ResponsavelModel responsavel;
    @Column(nullable = false) @Getter @Setter private boolean responsavel_financeiro;
}
