package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@Entity
@Table(name = "api_horarios")
@Getter @Setter @NoArgsConstructor
public class ApiHorario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "professor_id") private ApiProfessor professor;
    @Column(nullable = false) private Integer diaSemana;
    @Column(nullable = false) private LocalTime horaInicio;
    @Column(nullable = false) private LocalTime horaFim;
}
