package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa um funcionário administrativo da escola
 * (não docente). Estende {@link ApiUser}, herdando os dados básicos de
 * usuário e adicionando o cargo e o setor de atuação.
 */
@Entity
@Table(name = "api_funcionarios")
@Getter @Setter @NoArgsConstructor
public class ApiFuncionario extends ApiUser {
    /** Cargo ocupado pelo funcionário. */
    @Column
    private String cargo;

    /** Setor/departamento em que o funcionário atua. */
    @Column
    private String setor;

    /** {@inheritDoc} Sempre retorna {@code "FUNCIONARIO"} para esta entidade. */
    @Override
    public String getPerfil() {
        return "FUNCIONARIO";
    }
}
