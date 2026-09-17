package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

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
