package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "api_matriculas")
@Getter @Setter @NoArgsConstructor
public class ApiMatricula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "aluno_id") 
    private ApiAluno aluno;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "turma_id") 
    private ApiTurma turma;
    
    @Column(nullable = false) private Integer anoLetivo;
    @Column(nullable = false) private String status;
    @Column(nullable = false) private LocalDate dataMatricula;
}
