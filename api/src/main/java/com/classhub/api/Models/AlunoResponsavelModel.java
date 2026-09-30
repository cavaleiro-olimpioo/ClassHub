package com.classhub.api.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Representa a associação aluno x responsável, indicando quem responde
 * financeiramente pelo aluno.
 * <p>
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_aluno_responsavel", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "responsavel_id"}))
public class AlunoResponsavelModel {
    /** Identificador único do vínculo aluno/responsável. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_aluno_responsavel;
    /** Aluno associado. */
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    /** Responsável associado. */
    @ManyToOne(optional = false) @JoinColumn(name = "responsavel_id", nullable = false)
    @Getter @Setter private ResponsavelModel responsavel;
    /** Indica se este responsável é o responsável financeiro pelo aluno. */
    @Column(nullable = false) @Getter @Setter private boolean responsavel_financeiro;
}
