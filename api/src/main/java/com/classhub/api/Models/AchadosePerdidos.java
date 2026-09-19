package com.classhub.api.Models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "Achados_e_perdidos")
public class AchadosePerdidos {
    @Id 
    @Column 
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id_achado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    @Getter @Setter
    private FuncionarioModel funcionario;

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
