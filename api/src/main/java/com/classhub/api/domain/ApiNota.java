package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_notas", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id", "bimestre", "tipo"}))
@Getter @Setter @NoArgsConstructor
public class ApiNota {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "aluno_id") private ApiAluno aluno;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    @Column(nullable = false) private Integer bimestre;
    @Column(nullable = false) private String tipo;
    @Column(nullable = false) private Double peso;
    @Column(nullable = false) private Double valor;
}
