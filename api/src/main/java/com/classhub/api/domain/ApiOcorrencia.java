package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_ocorrencias")
@Getter @Setter @NoArgsConstructor
public class ApiOcorrencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "aluno_id") private ApiAluno aluno;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "professor_id") private ApiProfessor professor;
    @Column(nullable = false) private java.time.LocalDate data;
    @Column(nullable = false) private String tipo;
    @Column(nullable = false, length = 2000) private String descricao;
    @Column(nullable = false) private String status;

    @PrePersist
    private void prePersist() {
        if (data == null) {
            data = java.time.LocalDate.now();
        }
        if (status == null || status.isBlank()) {
            status = "ABERTA";
        }
    }
}
