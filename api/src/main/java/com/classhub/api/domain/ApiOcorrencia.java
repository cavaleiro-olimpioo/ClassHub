package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa uma ocorrência disciplinar ou administrativa
 * registrada por um professor sobre um aluno (ex.: advertência, elogio,
 * comunicado aos responsáveis).
 */
@Entity
@Table(name = "api_ocorrencias")
@Getter @Setter @NoArgsConstructor
public class ApiOcorrencia {
    /** Identificador único da ocorrência. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Aluno envolvido na ocorrência. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "aluno_id") private ApiAluno aluno;
    /** Professor que registrou a ocorrência. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "professor_id") private ApiProfessor professor;
    /** Data em que a ocorrência foi registrada. */
    @Column(nullable = false) private java.time.LocalDate data;
    /** Tipo da ocorrência (ex.: "ADVERTENCIA", "ELOGIO"). */
    @Column(nullable = false) private String tipo;
    /** Descrição detalhada da ocorrência. */
    @Column(nullable = false, length = 2000) private String descricao;
    /** Situação atual da ocorrência (ex.: "ABERTA", "RESOLVIDA"). */
    @Column(nullable = false) private String status;

    /**
     * Callback executado antes de persistir a ocorrência pela primeira vez.
     * Preenche a data com a data atual e o status com {@code "ABERTA"} caso
     * não tenham sido informados.
     */
    @PrePersist
    private void prePersist() {
        if (data == null) {
            data = java.time.LocalDate.now();
        }
        if (status == null || status.isBlank()) {
            status = "ABERTA";
        }
    }
}
