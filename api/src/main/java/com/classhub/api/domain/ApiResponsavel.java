package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_responsaveis")
@Getter @Setter @NoArgsConstructor
public class ApiResponsavel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @Column(nullable = false) private String nome;
    @Column private String telefone;
    @Column private String cpf;
    @Column private String endereco;
    @Column private String parentesco;
}
