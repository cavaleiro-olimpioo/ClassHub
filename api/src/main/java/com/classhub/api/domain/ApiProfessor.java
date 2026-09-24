package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_professores")
@Getter @Setter @NoArgsConstructor
public class ApiProfessor extends ApiUser {
    @Column
    private String formacao;

    @Override
    public String getPerfil() {
        return "PROFESSOR";
    }
}
