package com.classhub.api.repository;

import com.classhub.api.domain.ApiPresenca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiPresenca}, com
 * consultas para listar/buscar registros de presença por aluno, turma,
 * disciplina e data.
 */
public interface ApiPresencaRepository extends JpaRepository<ApiPresenca, Long> {
    /**
     * Lista todos os registros de presença de um aluno.
     *
     * @param alunoId identificador do aluno
     * @return lista de registros de presença do aluno
     */
    List<ApiPresenca> findByAlunoId(Long alunoId);
    /**
     * Lista os registros de presença de uma turma, disciplina e data específicos.
     *
     * @param turmaId identificador da turma
     * @param disciplinaId identificador da disciplina
     * @param data data da aula
     * @return lista de registros de presença que atendem aos critérios informados
     */
    List<ApiPresenca> findByTurmaIdAndDisciplinaIdAndData(Long turmaId, Long disciplinaId, LocalDate data);
    /**
     * Lista todos os registros de presença de uma turma.
     *
     * @param turmaId identificador da turma
     * @return lista de registros de presença da turma
     */
    List<ApiPresenca> findByTurmaId(Long turmaId);
    /**
     * Busca o registro de presença único de um aluno, em uma turma,
     * disciplina e data específicos (usado para upsert ao salvar chamadas).
     *
     * @param alunoId identificador do aluno
     * @param turmaId identificador da turma
     * @param disciplinaId identificador da disciplina
     * @param data data da aula
     * @return o registro encontrado, ou vazio se ainda não existir
     */
    Optional<ApiPresenca> findByAlunoIdAndTurmaIdAndDisciplinaIdAndData(Long alunoId, Long turmaId, Long disciplinaId, LocalDate data);
}
