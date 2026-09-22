package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "api_achados_perdidos")
@Getter @Setter @NoArgsConstructor
public class ApiAchadoPerdido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 2000) private String descricao;
    @Column(nullable = false) private String categoria;
    @Column(nullable = false) private String localEncontrado;
    @Column(nullable = false) private LocalDate data;
    @Column(nullable = false) private String status;
}
