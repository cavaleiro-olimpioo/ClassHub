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

/**
 * Legacy persistence model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
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
