package com.classhub.api.Models;

import java.time.LocalDate;
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
@Table(name = "tb_frequencia", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id", "data"}))
public class FrequenciaModel {
    /** Identificador único do registro de frequência. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_frequencia;
    /** Aluno referente ao registro. */
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    /** Disciplina referente ao registro. */
    @ManyToOne(optional = false) @JoinColumn(name = "disciplina_id", nullable = false)
    @Getter @Setter private DiciplinaModel disciplina;
    /** Data da aula. */
    @Column(nullable = false) @Getter @Setter private LocalDate data;
    /** Indica se o aluno estava presente na aula. */
    @Column(nullable = false) @Getter @Setter private boolean presente;
}
