package com.classhub.api.repository;

import com.classhub.api.domain.ApiVinculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiVinculo}, com
 * consulta para listar os vínculos de um professor específico.
 */
public interface ApiVinculoRepository extends JpaRepository<ApiVinculo, Long> {
    /**
     * Lista todos os vínculos (turma + disciplina) de um professor.
     *
     * @param professorId identificador do professor
     * @return lista de vínculos do professor
     */
    List<ApiVinculo> findByProfessorId(Long professorId);
}
