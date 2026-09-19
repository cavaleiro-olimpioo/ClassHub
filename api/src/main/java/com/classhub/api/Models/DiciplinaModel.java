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
