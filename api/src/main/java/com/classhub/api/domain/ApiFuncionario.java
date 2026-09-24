package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_funcionarios")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter @NoArgsConstructor
public class ApiFuncionario extends ApiUser {
    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private String setor;
}
