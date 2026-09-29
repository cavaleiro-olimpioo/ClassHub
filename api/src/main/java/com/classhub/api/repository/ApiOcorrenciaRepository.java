package com.classhub.api.repository;

import com.classhub.api.domain.ApiOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiOcorrencia}, com
 * consulta para listar ocorrências de um aluno específico.
 */
public interface ApiOcorrenciaRepository extends JpaRepository<ApiOcorrencia, Long> {
    /**
     * Lista todas as ocorrências registradas para um aluno.
     *
     * @param alunoId identificador do aluno
     * @return lista de ocorrências do aluno
     */
    List<ApiOcorrencia> findByAlunoId(Long alunoId);
}
