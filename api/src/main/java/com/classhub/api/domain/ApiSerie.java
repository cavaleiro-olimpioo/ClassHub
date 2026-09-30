package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa uma série/ano escolar (ex.: "1º Ano do Ensino
 * Médio"), classificada por nível de ensino.
 */
@Entity
@Table(name = "api_series")
@Getter @Setter @NoArgsConstructor
public class ApiSerie {
    /** Identificador único da série. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Nome da série (único), ex.: "1º Ano". */
    @Column(nullable = false, unique = true) private String nome;
    /** Nível de ensino ao qual a série pertence (ex.: "FUNDAMENTAL", "MEDIO"). */
    @Column(nullable = false) private String nivel;
}
