package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "api_alunos")
@Getter @Setter @NoArgsConstructor
public class ApiAluno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nome;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false, unique = true) private String matricula;
    @Column(nullable = false) private LocalDate dataNascimento;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
}
