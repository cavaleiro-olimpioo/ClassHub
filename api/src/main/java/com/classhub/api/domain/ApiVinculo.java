package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_vinculos", uniqueConstraints = @UniqueConstraint(columnNames = {"professor_id", "turma_id", "disciplina_id", "anoLetivo"}))
@Getter @Setter @NoArgsConstructor
public class ApiVinculo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "professor_id") private ApiProfessor professor;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    @Column(nullable = false) private Integer anoLetivo;
}
