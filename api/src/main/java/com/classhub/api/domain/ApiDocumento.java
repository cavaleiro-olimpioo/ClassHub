package com.classhub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Entidade JPA que representa um documento anexado ao cadastro de um aluno
 * (ex.: RG, certidão de nascimento, comprovante de residência).
 * <p>
 * Um mesmo aluno não pode ter dois documentos do mesmo {@code tipoDocumento}
 * (restrição de unicidade composta).
 */
@Entity
@Table(name = "api_documentos", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "tipoDocumento"}))
@Getter @Setter @NoArgsConstructor
public class ApiDocumento {
    /** Identificador único do documento. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    /** Aluno ao qual o documento pertence. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "aluno_id") 
    private ApiAluno aluno;
    
    /** URL/caminho onde o arquivo do documento está armazenado. */
    @Column(nullable = false) private String arquivoUrl;
    /** Data em que o documento foi enviado (upload). */
    @Column(nullable = false) private LocalDate dataUpload;
    /** Tipo do documento (ex.: "RG", "CPF", "COMPROVANTE_RESIDENCIA"). */
    @Column(nullable = false) private String tipoDocumento;
}
