package com.classhub.api.Models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_nota", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id", "bimestre"}))
public class NotaModel {
    /** Identificador único da nota. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_nota;
    /** Aluno avaliado. */
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    /** Disciplina referente à nota. */
    @ManyToOne(optional = false) @JoinColumn(name = "disciplina_id", nullable = false)
    @Getter @Setter private DiciplinaModel disciplina;
    /** Valor da nota obtida. */
    @Column(nullable = false, precision = 5, scale = 2) @Getter @Setter private BigDecimal valor;
    /** Bimestre a que a nota se refere. */
    @Column(nullable = false) @Getter @Setter private int bimestre;
}
