package com.classhub.api.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "tb_documento")
public class DocumentoModel {
    @Column 
    @Getter 
    @Setter 
    private String arquivo_url;

    @Column 
    @Getter 
    @Setter 
    private String data_upload;

    @Column 
    @Getter 
    @Setter 
    private String tipo_documento;
}
