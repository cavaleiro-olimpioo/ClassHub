package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_disciplinas")
@Getter @Setter @NoArgsConstructor
public class ApiDisciplina {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String nome;
    @Column(nullable = false) private Integer cargaHoraria;
}
