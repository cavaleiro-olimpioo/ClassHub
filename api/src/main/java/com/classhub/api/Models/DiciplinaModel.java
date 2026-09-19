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
@Table (name = "tb_diciplina")
public class DiciplinaModel {
    @Column 
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id_diciplina;

    @Column 
    @Getter 
    @Setter 
    private String nome;

    @Column 
    @Getter 
    @Setter 
    private int carga_horaria;
}
