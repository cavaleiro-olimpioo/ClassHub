package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

/**
 * Legacy persistence base model retained only for historical reference.
 * This package is intentionally not part of the active JPA domain model.
 * It should not be used in current business logic or persistence configuration.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
@MappedSuperclass 
public class UserModel {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @Getter 
    @Setter
    private int id_usuario;

    @Column
    @Getter
    @Setter 
    private String nome;

    @Column
    @Getter 
    @Setter
    private String email;

    @Column
    @Getter
    @Setter
    private String password;

    @Column
    @Getter 
    @Setter 
    private String telefone;
}
