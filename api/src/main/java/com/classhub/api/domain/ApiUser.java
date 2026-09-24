package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario")
@Table(name = "api_usuarios")
@Getter @Setter @NoArgsConstructor
public class ApiUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senhaHash;
    @Column(nullable = false)
    private String perfil;
    @Column(nullable = false)
    private String nome;
    @Column
    private String telefone;
    @Column
    private java.time.LocalDate dataNascimento;
    private Long vinculoId;
}
