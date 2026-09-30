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
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table (name = "tb_documento")
public class DocumentoModel {
    /** Identificador único do documento. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_documento;

    /** Documento é dependente de um aluno e não pode existir sem ele. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    @Getter @Setter
    private AlunoModel aluno;

    /** URL/caminho do arquivo do documento. */
    @Column 
    @Getter 
    @Setter 
    private String arquivo_url;

    /** Data em que o documento foi enviado. */
    @Column 
    @Getter 
    @Setter 
    private String data_upload;

    /** Tipo do documento (ex.: RG, CPF). */
    @Column 
    @Getter 
    @Setter 
    private String tipo_documento;
}
