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
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity 
@Table (name = "Achados_e_perdidos")
public class AchadosePerdidos {
    /** Identificador único do registro. */
    @Id 
    @Column 
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id_achado;

    /** Funcionário que registrou o item encontrado. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    @Getter @Setter
    private FuncionarioModel funcionario;

    /** Data em que o item foi registrado. */
    @Column 
    @Getter 
    @Setter 
    private LocalDate data_registro;

    /** Descrição do item encontrado. */
    @Column 
    @Getter 
    @Setter 
    private String descricao;

    /** Categoria do item encontrado. */
    @Column
    @Getter 
    @Setter 
    private String categoria;

    /** Status atual do item (ex.: guardado, devolvido). */
    @Column 
    @Getter 
    @Setter 
    private String status;
}
