package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table (name = "tb_documento")
public class DocumentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_documento;

    /** Documento é dependente de um aluno e não pode existir sem ele. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter
    private AlunoModel aluno;

    @Column 
    @Getter 
    @Setter 
    private String arquivo_url;

    @Column 
    @Getter 
    @Setter 
    private String data_upload;

    @Column 
    @Getter 
    @Setter 
    private String tipo_documento;
}
