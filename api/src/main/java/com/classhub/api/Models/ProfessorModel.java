package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;;

@Entity
@Table(name = "tb_professor")
public class ProfessorModel extends UserModel {
    @Column
    @Getter 
    @Setter
    private String formacao;

    @Column
    @Getter 
    @Setter 
    private String whoami = "professor";

    @OneToMany(mappedBy = "professorResponsavel")
    @Getter @Setter
    private java.util.List<TurmaModel> turmasResponsaveis = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "professor")
    @Getter @Setter
    private java.util.List<OcorrenciaModel> ocorrenciasGeradas = new java.util.ArrayList<>();
}
