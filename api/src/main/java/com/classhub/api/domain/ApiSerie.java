package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_series")
@Getter @Setter @NoArgsConstructor
public class ApiSerie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String nome;
    @Column(nullable = false) private String nivel;
}
