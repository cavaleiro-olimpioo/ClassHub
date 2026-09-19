package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "tb_responsavel")
public class ResponsavelModel {
    @Column 
    @Getter 
    @Setter 
    private String endereco;

    @Column 
    @Getter 
    @Setter 
    private String parentesco;
}
