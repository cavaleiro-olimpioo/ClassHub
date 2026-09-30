package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que marca o fechamento (encerramento) do boletim de uma turma
 * em um determinado bimestre e ano letivo, impedindo novas alterações de
 * notas para aquele período após o fechamento.
 * <p>
 * A combinação (ano letivo, bimestre, turma) é única.
 */
@Entity
@Table(name = "api_fechamentos_boletim", uniqueConstraints = @UniqueConstraint(columnNames = {"anoLetivo", "bimestre", "turma_id"}))
@Getter @Setter @NoArgsConstructor
public class ApiFechamentoBoletim {
    /** Identificador único do fechamento. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Ano letivo ao qual o fechamento se refere. */
    @Column(nullable = false) private Integer anoLetivo;
    /** Número do bimestre (1 a 4) que foi fechado. */
    @Column(nullable = false) private Integer bimestre;
    /** Turma cujo boletim foi fechado. */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
}
