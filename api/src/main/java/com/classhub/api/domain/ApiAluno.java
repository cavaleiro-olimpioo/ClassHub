package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Entidade JPA que representa um aluno matriculado na escola.
 * Estende {@link ApiUser}, herdando os dados básicos de usuário (nome,
 * e-mail, senha etc.) e adiciona o número de matrícula e a turma atual do
 * aluno.
 */
@Entity
@Table(name = "api_alunos")
@Getter @Setter @NoArgsConstructor
public class ApiAluno extends ApiUser {
    /** Número de matrícula único do aluno, gerado automaticamente caso não seja informado. */
    @Column(unique = true)
    private String matricula;

    /** {@inheritDoc} Sempre retorna {@code "ALUNO"} para esta entidade. */
    @Override
    public String getPerfil() {
        return "ALUNO";
    }

    /** Turma em que o aluno está atualmente matriculado. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private ApiTurma turma;

    /**
     * Callback executado antes de persistir o aluno pela primeira vez.
     * Gera automaticamente um número de matrícula (ano letivo + número
     * aleatório de 6 dígitos) caso nenhum tenha sido definido.
     */
    @PrePersist
    private void gerarMatricula() {
        if (matricula == null || matricula.isBlank()) {
            int ano = Year.now().getValue();
            int aleatorio = ThreadLocalRandom.current().nextInt(100000, 999999);
            matricula = ano + String.valueOf(aleatorio);
        }
    }
}
