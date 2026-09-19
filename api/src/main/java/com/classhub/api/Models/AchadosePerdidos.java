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
@Table (name = "Achados_e_perdidos")
public class AchadosePerdidos {
    @Id 
    @Column 
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id_achado;

    @Column 
    @Getter 
    @Setter 
    private LocalDate data_registro;

    @Column 
    @Getter 
    @Setter 
    private String descricao;

    @Column
    @Getter 
    @Setter 
    private String categoria;

    @Column 
    @Getter 
    @Setter 
    private String status;
}
