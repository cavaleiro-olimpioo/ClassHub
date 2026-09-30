package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Entidade JPA que representa um evento do calendário escolar (ex.: feriado,
 * recesso, prova, reunião de pais), associado a um ano letivo.
 */
@Entity
@Table(name = "api_calendario")
@Getter @Setter @NoArgsConstructor
public class ApiCalendario {
    /** Identificador único do evento. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    /** Data em que o evento ocorre. */
    @Column(nullable = false) private LocalDate data;
    /** Ano letivo ao qual o evento pertence. */
    @Column(nullable = false) private Integer anoLetivo;
    /** Tipo do evento (ex.: "FERIADO", "PROVA", "REUNIAO"). */
    @Column(nullable = false) private String tipo;
    /** Título curto do evento, exibido nas listagens. */
    @Column(nullable = false) private String titulo;
    /** Descrição detalhada e opcional do evento. */
    @Column(length = 2000) private String descricao;
}
