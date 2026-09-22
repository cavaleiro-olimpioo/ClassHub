package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "api_presencas", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "turma_id", "disciplina_id", "data"}))
@Getter @Setter @NoArgsConstructor
public class ApiPresenca {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "aluno_id") private ApiAluno aluno;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    @Column(nullable = false) private LocalDate data;
    @Column(nullable = false) private String status;
    @Column(length = 2000) private String justificativa;
}
