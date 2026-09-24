package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_funcionarios")
@DiscriminatorValue("FUNCIONARIO")
@Getter @Setter @NoArgsConstructor
public class ApiFuncionario extends ApiUser {
    @Column
    private String cargo;

    @Column
    private String setor;
}
