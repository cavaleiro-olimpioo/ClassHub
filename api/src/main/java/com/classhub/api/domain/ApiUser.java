package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter @Setter @NoArgsConstructor
public abstract class ApiUser {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senhaHash;
    @Column(nullable = false)
    private String nome;
    @Column
    private String telefone;
    @Column
    private java.time.LocalDate dataNascimento;

    @Column
    private String sexo;
    @Column
    private String cpf;
    @Column
    private String foto;

    public abstract String getPerfil();
}
