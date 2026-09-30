package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa a associação entre um aluno e um responsável
 * (ex.: pai, mãe ou tutor legal), indicando também se aquele responsável é o
 * responsável financeiro pelo aluno.
 * <p>
 * A combinação (aluno, responsável) é única.
 */
@Entity
@Table(name = "api_aluno_responsaveis", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "responsavel_id"}))
@Getter @Setter @NoArgsConstructor
public class ApiAlunoResponsavel {
    /** Identificador único do vínculo. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    /** Aluno associado ao responsável. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "aluno_id") 
    private ApiAluno aluno;
    
    /** Responsável associado ao aluno. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "responsavel_id") 
    private ApiResponsavel responsavel;
    
    /** Indica se este responsável é o responsável financeiro pelo aluno. */
    @Column(nullable = false) private Boolean responsavelFinanceiro;
}
