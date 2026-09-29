package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Entidade JPA que representa o registro de presença/falta de um aluno em
 * uma aula de uma disciplina/turma em uma data específica.
 * <p>
 * A combinação (aluno, turma, disciplina, data) é única, evitando registros
 * duplicados para a mesma aula.
 */
@Entity
@Table(name = "api_presencas", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "turma_id", "disciplina_id", "data"}))
@Getter @Setter @NoArgsConstructor
public class ApiPresenca {
    /** Identificador único do registro de presença. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Aluno referente ao registro. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "aluno_id") private ApiAluno aluno;
    /** Turma referente ao registro. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    /** Disciplina/aula referente ao registro. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    /** Data da aula em que a presença/falta foi registrada. */
    @Column(nullable = false) private LocalDate data;
    /** Status do registro (ex.: "PRESENTE", "FALTA", "JUSTIFICADA"). */
    @Column(nullable = false) private String status;
    /** Justificativa da falta, quando aplicável. */
    @Column(length = 2000) private String justificativa;
}
