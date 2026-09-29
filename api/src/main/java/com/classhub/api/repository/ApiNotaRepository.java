package com.classhub.api.repository;

import com.classhub.api.domain.ApiNota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiNota}, com
 * consultas para listar notas por aluno, turma/disciplina/bimestre, e
 * aluno/bimestre (usada no cálculo de boletim).
 */
public interface ApiNotaRepository extends JpaRepository<ApiNota, Long> {
    /**
     * Lista todas as notas de um aluno.
     *
     * @param alunoId identificador do aluno
     * @return lista de notas do aluno
     */
    List<ApiNota> findByAlunoId(Long alunoId);
    /**
     * Lista as notas de uma turma, disciplina e bimestre específicos.
     *
     * @param turmaId identificador da turma
     * @param disciplinaId identificador da disciplina
     * @param bimestre número do bimestre
     * @return lista de notas que atendem aos critérios informados
     */
    List<ApiNota> findByTurmaIdAndDisciplinaIdAndBimestre(Long turmaId, Long disciplinaId, Integer bimestre);
    /**
     * Lista as notas de um aluno em um bimestre específico (usado no cálculo do boletim).
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return lista de notas do aluno no bimestre informado
     */
    List<ApiNota> findByAlunoIdAndBimestre(Long alunoId, Integer bimestre);
}
