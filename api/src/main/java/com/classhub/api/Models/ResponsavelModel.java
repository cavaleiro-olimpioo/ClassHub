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
@Table (name = "tb_responsavel")
public class ResponsavelModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private int id_responsavel;

    @Column 
    @Getter 
    @Setter 
    private String endereco;

    @Column 
    @Getter 
    @Setter 
    private String parentesco;

    @OneToMany(mappedBy = "responsavel")
    @Getter @Setter
    private java.util.List<AlunoResponsavelModel> alunos = new java.util.ArrayList<>();
}
