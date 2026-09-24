package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "api_documentos", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "tipoDocumento"}))
@Getter @Setter @NoArgsConstructor
public class ApiDocumento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "aluno_id") 
    private ApiAluno aluno;
    
    @Column(nullable = false) private String arquivoUrl;
    @Column(nullable = false) private LocalDate dataUpload;
    @Column(nullable = false) private String tipoDocumento;
}
