package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;;

/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
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
