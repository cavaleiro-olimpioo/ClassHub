package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa um responsável legal por um ou mais alunos
 * (ex.: pai, mãe ou tutor), com seus dados de contato pessoais.
 */
@Entity
@Table(name = "api_responsaveis")
@Getter @Setter @NoArgsConstructor
public class ApiResponsavel {
    /** Identificador único do responsável. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    /** Nome completo do responsável. */
    @Column(nullable = false) private String nome;
    /** Telefone de contato do responsável. */
    @Column private String telefone;
    /** CPF do responsável. */
    @Column private String cpf;
    /** Endereço residencial do responsável. */
    @Column private String endereco;
    /** Grau de parentesco com o aluno (ex.: "MAE", "PAI", "TUTOR"). */
    @Column private String parentesco;
}
