package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa o vínculo entre um professor, uma turma e uma
 * disciplina em um determinado ano letivo — ou seja, define quem leciona o
 * quê e para quem.
 * <p>
 * A combinação (professor, turma, disciplina, ano letivo) é única.
 */
@Entity
@Table(name = "api_vinculos", uniqueConstraints = @UniqueConstraint(columnNames = {"professor_id", "turma_id", "disciplina_id", "anoLetivo"}))
@Getter @Setter @NoArgsConstructor
public class ApiVinculo {
    /** Identificador único do vínculo. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Professor vinculado. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "professor_id") private ApiProfessor professor;
    /** Turma vinculada. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
    /** Disciplina vinculada. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "disciplina_id") private ApiDisciplina disciplina;
    /** Ano letivo em que o vínculo é válido. */
    @Column(nullable = false) private Integer anoLetivo;
}
