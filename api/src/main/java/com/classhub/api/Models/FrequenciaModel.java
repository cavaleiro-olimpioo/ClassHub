package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
