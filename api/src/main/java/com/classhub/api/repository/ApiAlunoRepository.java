package com.classhub.api.repository;

import com.classhub.api.domain.ApiAluno;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiAluno}, com
 * consultas para busca de alunos por turma, nome e e-mail.
 */
public interface ApiAlunoRepository extends JpaRepository<ApiAluno, Long> {
    /**
     * Lista alunos carregando a turma em uma consulta com junção externa,
     * preservando alunos cujas referências legadas de turma estejam ausentes.
     *
     * @return alunos com a turma carregada, quando existente
     */
    @Query("select a from ApiAluno a left join fetch a.turma")
    List<ApiAluno> findAllWithTurma();

    /** Busca alunos por turma, carregando a associação usada no DTO da lista. */
    @EntityGraph(attributePaths = "turma")
    List<ApiAluno> findByTurmaId(Long turmaId);

    /** Busca alunos por nome, carregando a associação usada no DTO da lista. */
    @EntityGraph(attributePaths = "turma")
    List<ApiAluno> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca um aluno pelo e-mail, ignorando maiúsculas/minúsculas.
     *
     * @param email e-mail do aluno
     * @return o aluno encontrado, ou vazio se não existir
     */
    Optional<ApiAluno> findByEmailIgnoreCase(String email);
}
