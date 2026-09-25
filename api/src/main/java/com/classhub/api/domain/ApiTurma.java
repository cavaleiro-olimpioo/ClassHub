package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_turmas", uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "serie_id", "anoLetivo"}))
@Getter @Setter @NoArgsConstructor
public class ApiTurma {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nome;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "serie_id") private ApiSerie serie;
    @Column(nullable = false) private Integer anoLetivo;
    @Column(nullable = false) private String turno = "MATUTINO";
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "professor_responsavel_id") private ApiProfessor professorResponsavel;

    @PrePersist
    private void prePersist() {
        if (turno == null || turno.isBlank()) {
            turno = "MATUTINO";
        }
    }
}
