package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa uma disciplina (matéria) lecionada na escola,
 * com sua carga horária total.
 */
@Entity
@Table(name = "api_disciplinas")
@Getter @Setter @NoArgsConstructor
public class ApiDisciplina {
    /** Identificador único da disciplina. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Nome da disciplina (único). */
    @Column(nullable = false, unique = true) private String nome;
    /** Carga horária total da disciplina, em horas. */
    @Column(nullable = false) private Integer cargaHoraria;
}
