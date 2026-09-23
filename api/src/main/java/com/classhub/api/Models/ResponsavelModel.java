package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table (name = "tb_responsavel")
public class ResponsavelModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_responsavel;

    @Column 
    @Getter 
    @Setter 
    private String endereco;

    @Column 
    @Getter 
    @Setter 
    private String parentesco;

    @OneToMany(mappedBy = "responsavel")
    @Getter @Setter
    private java.util.List<AlunoResponsavelModel> alunos = new java.util.ArrayList<>();
}
