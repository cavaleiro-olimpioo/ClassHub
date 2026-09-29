package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Entidade JPA que representa um item do setor de "Achados e Perdidos" da escola.
 * Registra um objeto encontrado, sua categoria, o local e a data em que foi
 * encontrado, além do funcionário que fez o registro e o status atual
 * (ex.: "GUARDADO", "DEVOLVIDO").
 */
@Entity
@Table(name = "api_achados_perdidos")
@Getter @Setter @NoArgsConstructor
public class ApiAchadoPerdido {
    /** Identificador único do registro. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Descrição textual do objeto encontrado. */
    @Column(nullable = false, length = 2000) private String descricao;
    /** Categoria do objeto (ex.: eletrônico, vestuário, documento). */
    @Column(nullable = false) private String categoria;
    /** Local onde o objeto foi encontrado. */
    @Column(nullable = false) private String localEncontrado;
    /** Data em que o objeto foi encontrado/registrado. */
    @Column(nullable = false) private LocalDate data;
    /** Situação atual do item (ex.: "GUARDADO", "DEVOLVIDO"). */
    @Column(nullable = false) private String status;
    /** Funcionário responsável por registrar o achado. */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "funcionario_id") private ApiFuncionario funcionarioRegistrou;
}
