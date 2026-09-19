package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "tb_turma")
public class TurmaModel {
    @Id 
    @Column 
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id_turma;

    @Column
    @Getter 
    @Setter
    private int serie;

    @Column 
    @Getter 
    @Setter 
    private char nome;

    @Column 
    @Getter 
    @Setter 
    private String turno;

    /** Cada turma possui um único professor responsável. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    @Getter @Setter
    private ProfessorModel professorResponsavel;

    @OneToMany(mappedBy = "turma")
    @Getter @Setter
    private java.util.List<MatriculaModel> matriculas = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "turma")
    @Getter @Setter
    private java.util.List<TurmaDisciplinaModel> disciplinas = new java.util.ArrayList<>();


}
