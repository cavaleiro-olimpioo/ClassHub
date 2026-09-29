package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
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
@Table (name = "tb_diciplina")
public class DiciplinaModel {
    /** Identificador único da disciplina. */
    @Column 
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Getter 
    @Setter
    private int id_diciplina;

    /** Nome da disciplina. */
    @Column 
    @Getter 
    @Setter 
    private String nome;

    /** Carga horária total da disciplina. */
    @Column 
    @Getter 
    @Setter 
    private int carga_horaria;

    /** Registros de frequência associados a esta disciplina. */
    @OneToMany(mappedBy = "disciplina")
    @Getter @Setter
    private java.util.List<FrequenciaModel> frequencias = new java.util.ArrayList<>();

    /** Notas lançadas nesta disciplina. */
    @OneToMany(mappedBy = "disciplina")
    @Getter @Setter
    private java.util.List<NotaModel> notas = new java.util.ArrayList<>();

    /** Turmas em que esta disciplina é lecionada. */
    @OneToMany(mappedBy = "disciplina")
    @Getter @Setter
    private java.util.List<TurmaDisciplinaModel> turmas = new java.util.ArrayList<>();
}
