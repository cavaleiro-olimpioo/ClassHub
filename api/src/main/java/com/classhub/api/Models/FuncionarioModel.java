package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "tb_funcionario")
public class FuncionarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_funcionario;


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

    @OneToMany(mappedBy = "funcionario")
    @Getter @Setter
    private java.util.List<AchadosePerdidos> achadosRegistrados = new java.util.ArrayList<>();
}
