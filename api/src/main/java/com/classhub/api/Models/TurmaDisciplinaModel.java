package com.classhub.api.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Representa a associação das disciplinas ministradas em cada turma.
 * <p>
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_turma_disciplina", uniqueConstraints = @UniqueConstraint(columnNames = {"turma_id", "disciplina_id"}))
public class TurmaDisciplinaModel {
    /** Identificador único da associação turma/disciplina. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_turma_disciplina;
    /** Turma associada. */
    @ManyToOne(optional = false) @JoinColumn(name = "turma_id", nullable = false)
    @Getter @Setter private TurmaModel turma;
    /** Disciplina associada. */
    @ManyToOne(optional = false) @JoinColumn(name = "disciplina_id", nullable = false)
    @Getter @Setter private DiciplinaModel disciplina;
}
