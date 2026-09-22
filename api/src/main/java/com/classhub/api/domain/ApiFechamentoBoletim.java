package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_fechamentos_boletim", uniqueConstraints = @UniqueConstraint(columnNames = {"anoLetivo", "bimestre", "turma_id"}))
@Getter @Setter @NoArgsConstructor
public class ApiFechamentoBoletim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Integer anoLetivo;
    @Column(nullable = false) private Integer bimestre;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "turma_id") private ApiTurma turma;
}
