package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass 
public class user {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id_usuario;

    @Column 
    private String nome;

    @Column
    private String email;

    @Column
    private String password;

    @Column 
    private String telefone;
}
