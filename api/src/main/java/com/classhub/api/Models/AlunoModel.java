package com.classhub.api.Models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
}
