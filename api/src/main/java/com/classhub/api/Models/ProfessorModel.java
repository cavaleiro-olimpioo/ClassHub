package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;;

@Entity
@Table(name = "tb_professor")
public class ProfessorModel extends UserModel {
    @Column
    @Getter 
    @Setter
    private String formacao;

    @Column
    @Getter 
    @Setter 
    private String whoami = "professor";
}
