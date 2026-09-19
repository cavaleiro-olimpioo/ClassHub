package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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


}
