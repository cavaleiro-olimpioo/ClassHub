package com.classhub.api.Models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_nota", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id", "bimestre"}))
public class NotaModel {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_nota;
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    @ManyToOne(optional = false) @JoinColumn(name = "disciplina_id", nullable = false)
    @Getter @Setter private DiciplinaModel disciplina;
    @Column(nullable = false, precision = 5, scale = 2) @Getter @Setter private BigDecimal valor;
    @Column(nullable = false) @Getter @Setter private int bimestre;
}
