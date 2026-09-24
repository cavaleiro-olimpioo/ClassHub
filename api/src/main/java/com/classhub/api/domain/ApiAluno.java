package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "api_alunos")
@Getter @Setter @NoArgsConstructor
public class ApiAluno extends ApiUser {
    @Column(unique = true)
    private String matricula;

    @Override
    public String getPerfil() {
        return "ALUNO";
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private ApiTurma turma;

    @PrePersist
    private void gerarMatricula() {
        if (matricula == null || matricula.isBlank()) {
            int ano = Year.now().getValue();
            int aleatorio = ThreadLocalRandom.current().nextInt(100000, 999999);
            matricula = ano + String.valueOf(aleatorio);
        }
    }
}