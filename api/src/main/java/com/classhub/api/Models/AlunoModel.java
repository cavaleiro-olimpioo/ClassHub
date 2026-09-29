package com.classhub.api.Models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
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
@Table (name = "tb_aluno")
public class AlunoModel {
    /** Identificador único do aluno. */
    @Id 
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id_aluno;

    /** Nome completo do aluno. */
    @Column 
    @Getter 
    @Setter 
    private String nome;

    /** Data de nascimento do aluno. */
    @Column 
    @Getter 
    @Setter 
    private LocalDate data_nascimento;

    /** Sexo do aluno. */
    @Column 
    @Getter 
    @Setter 
    private char sexo;

    /** CPF do aluno. */
    @Column 
    @Getter 
    @Setter 
    private int cpf;

    /** Caminho/URL da foto do aluno. */
    @Column 
    @Getter 
    @Setter 
    private String foto;

    /** Matrículas do aluno ao longo dos anos letivos. */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<MatriculaModel> matriculas = new java.util.ArrayList<>();

    /** Ocorrências registradas para o aluno. */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<OcorrenciaModel> ocorrencias = new java.util.ArrayList<>();

    /** Documentos anexados ao cadastro do aluno. */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<DocumentoModel> documentos = new java.util.ArrayList<>();

    /** Responsáveis vinculados ao aluno. */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<AlunoResponsavelModel> responsaveis = new java.util.ArrayList<>();

    /** Registros de frequência (presença/falta) do aluno. */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<FrequenciaModel> frequencias = new java.util.ArrayList<>();

    /** Notas lançadas para o aluno. */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private java.util.List<NotaModel> notas = new java.util.ArrayList<>();
}
