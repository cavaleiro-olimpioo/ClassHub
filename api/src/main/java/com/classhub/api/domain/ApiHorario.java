package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

/**
 * Entidade JPA que representa um horário fixo de aula na grade semanal de
 * uma turma: qual disciplina é lecionada, por qual professor, em qual dia da
 * semana e em qual faixa de horário.
 */
@Entity
@Table(name = "api_horarios")
@Getter @Setter @NoArgsConstructor
public class ApiHorario {
    /** Identificador único do horário. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Turma à qual este horário pertence. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    /** Disciplina lecionada neste horário. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    /** Professor responsável por esta aula. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "professor_id") private ApiProfessor professor;
    /** Dia da semana (1 = segunda-feira ... 5 = sexta-feira). */
    @Column(nullable = false) private Integer diaSemana;
    /** Horário de início da aula. */
    @Column(nullable = false) private LocalTime horaInicio;
    /** Horário de término da aula. */
    @Column(nullable = false) private LocalTime horaFim;
}
