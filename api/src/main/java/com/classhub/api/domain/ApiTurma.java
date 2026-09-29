package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa uma turma: agrupamento de alunos de uma mesma
 * série, em um turno e ano letivo específicos, com um professor responsável.
 * <p>
 * A combinação (nome, série, ano letivo) é única.
 */
@Entity
@Table(name = "api_turmas", uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "serie_id", "anoLetivo"}))
@Getter @Setter @NoArgsConstructor
public class ApiTurma {
    /** Identificador único da turma. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Nome/identificação da turma (ex.: "A", "B"). */
    @Column(nullable = false) private String nome;
    /** Serie a qual a turma pertence. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "serie_id") private ApiSerie serie;
    /** Ano letivo em que a turma esta em vigor. */
    @Column(nullable = false) private Integer anoLetivo;
    /** Turno da turma (ex.: "MATUTINO", "VESPERTINO", "NOTURNO"). Padrao: "MATUTINO". */
    @Column(nullable = false) private String turno = "MATUTINO";
    /** Professor responsavel (titular) pela turma. */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "professor_responsavel_id") private ApiProfessor professorResponsavel;

    /**
     * Callback executado antes de persistir a turma pela primeira vez.
     * Garante que o turno tenha um valor padrao ("MATUTINO") caso nao seja informado.
     */
    @PrePersist
    private void prePersist() {
        if (turno == null || turno.isBlank()) {
            turno = "MATUTINO";
        }
    }
}
