package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa um professor. Estende {@link ApiUser},
 * herdando os dados básicos de usuário e adicionando a formação acadêmica.
 */
@Entity
@Table(name = "api_professores")
@Getter @Setter @NoArgsConstructor
public class ApiProfessor extends ApiUser {
    /** Formação acadêmica do professor. */
    @Column
    private String formacao;

    /** {@inheritDoc} Sempre retorna {@code "PROFESSOR"} para esta entidade. */
    @Override
    public String getPerfil() {
        return "PROFESSOR";
    }
}
