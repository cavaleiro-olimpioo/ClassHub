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
 * Modelo de persistência legado, mantido apenas como referência histórica.
 * Este pacote não faz parte, intencionalmente, do modelo de domínio JPA
 * ativo da aplicação (ver {@code com.classhub.api.domain}).
 * Não deve ser usado em lógica de negócio ou configuração de persistência atuais.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@Entity
@Table (name = "tb_funcionario")
public class FuncionarioModel {
    /** Identificador único do funcionário. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_funcionario;


    /** Cargo ocupado pelo funcionário. */
    @Column
    @Getter 
    @Setter
    private String cargo;

    /** Setor de atuação do funcionário. */
    @Column 
    @Getter 
    @Setter 
    private String setor;

    /** Identificador textual do tipo de usuário (sempre "funcionario"). */
    @Column 
    @Getter 
    @Setter 
    private String whoami = "funcionario";

    /** Itens de achados e perdidos registrados por este funcionário. */
    @OneToMany(mappedBy = "funcionario")
    @Getter @Setter
    private java.util.List<AchadosePerdidos> achadosRegistrados = new java.util.ArrayList<>();
}
