package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "api_calendario")
@Getter @Setter @NoArgsConstructor
public class ApiCalendario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private LocalDate data;
    @Column(nullable = false) private Integer anoLetivo;
    @Column(nullable = false) private String tipo;
    @Column(nullable = false) private String titulo;
    @Column(length = 2000) private String descricao;
}
