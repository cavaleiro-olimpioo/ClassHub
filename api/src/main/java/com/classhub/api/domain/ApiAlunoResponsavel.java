package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_aluno_responsaveis", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "responsavel_id"}))
@Getter @Setter @NoArgsConstructor
public class ApiAlunoResponsavel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "aluno_id") 
    private ApiAluno aluno;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "responsavel_id") 
    private ApiResponsavel responsavel;
    
    @Column(nullable = false) private Boolean responsavelFinanceiro;
}
