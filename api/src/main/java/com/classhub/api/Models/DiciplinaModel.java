package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table (name = "tb_diciplina")
public class DiciplinaModel {
    @Column 
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Getter 
    @Setter
    private int id_diciplina;

    @Column 
    @Getter 
    @Setter 
    private String nome;

    @Column 
    @Getter 
    @Setter 
    private int carga_horaria;

    @OneToMany(mappedBy = "disciplina")
    @Getter @Setter
    private java.util.List<FrequenciaModel> frequencias = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "disciplina")
    @Getter @Setter
    private java.util.List<NotaModel> notas = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "disciplina")
    @Getter @Setter
    private java.util.List<TurmaDisciplinaModel> turmas = new java.util.ArrayList<>();
}
