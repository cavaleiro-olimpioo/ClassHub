package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "tb_funcionario")
public class FuncionarioModel {

    @Column
    @Getter 
    @Setter
    private String cargo;

    @Column 
    @Getter 
    @Setter 
    private String setor;

    @Column 
    @Getter 
    @Setter 
    private String whoami = "funcionario";
}
