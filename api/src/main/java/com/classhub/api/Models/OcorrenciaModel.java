package com.classhub.api.Models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Representa uma ocorrência criada por um professor para um aluno.
 * <p>
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table(name = "tb_ocorrencia")
public class OcorrenciaModel {
    /** Identificador único da ocorrência. */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private int id_ocorrencia;
    /** Professor que registrou a ocorrência. */
    @ManyToOne(optional = false) @JoinColumn(name = "professor_id", nullable = false)
    @Getter @Setter private ProfessorModel professor;
    /** Aluno envolvido na ocorrência. */
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter private AlunoModel aluno;
    /** Data em que a ocorrência foi registrada. */
    @Column(nullable = false) @Getter @Setter private LocalDate data;
    /** Tipo da ocorrência. */
    @Column(nullable = false) @Getter @Setter private String tipo;
    /** Descrição detalhada da ocorrência. */
    @Column(nullable = false, length = 2000) @Getter @Setter private String descricao;
}
