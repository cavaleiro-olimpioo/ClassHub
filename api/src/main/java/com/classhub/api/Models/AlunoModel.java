package com.classhub.api.Models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;

/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity 
@Table (name = "tb_aluno")
public class AlunoModel {
    @Id 
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id_aluno;

    @Column 
    @Getter 
    @Setter 
    private String nome;

    @Column 
    @Getter 
    @Setter 
    private LocalDate data_nascimento;

    @Column 
    @Getter 
    @Setter 
    private char sexo;

    @Column 
    @Getter 
    @Setter 
    private int cpf;

    @Column 
    @Getter 
    @Setter 
    private String foto;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<MatriculaModel> matriculas = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<OcorrenciaModel> ocorrencias = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<DocumentoModel> documentos = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<AlunoResponsavelModel> responsaveis = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<FrequenciaModel> frequencias = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<NotaModel> notas = new java.util.ArrayList<>();
}
