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
