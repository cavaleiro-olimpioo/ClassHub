package com.classhub.api.Models;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;;

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
