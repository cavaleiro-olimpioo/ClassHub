package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Entidade JPA que representa a matrícula de um aluno em uma turma, para um
 * determinado ano letivo, incluindo a data da matrícula e o seu status
 * (ex.: "ATIVA", "TRANCADA", "CANCELADA").
 */
@Entity
@Table(name = "api_matriculas")
@Getter @Setter @NoArgsConstructor
public class ApiMatricula {
    /** Identificador único da matrícula. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    /** Aluno matriculado. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "aluno_id") 
    private ApiAluno aluno;
    
    /** Turma na qual o aluno foi matriculado. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "turma_id") 
    private ApiTurma turma;
    
    /** Ano letivo referente a esta matrícula. */
    @Column(nullable = false) private Integer anoLetivo;
    /** Situação atual da matrícula. */
    @Column(nullable = false) private String status;
    /** Data em que a matrícula foi efetivada. */
    @Column(nullable = false) private LocalDate dataMatricula;
}
