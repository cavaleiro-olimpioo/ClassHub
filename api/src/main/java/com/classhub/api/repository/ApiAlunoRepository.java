package com.classhub.api.repository;

import com.classhub.api.domain.ApiAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiAluno}, com
 * consultas para busca de alunos por turma, nome e e-mail.
 */
public interface ApiAlunoRepository extends JpaRepository<ApiAluno, Long> {
    /**
     * Busca os alunos matriculados em uma turma específica.
     *
     * @param turmaId identificador da turma
     * @return lista de alunos da turma
     */
    List<ApiAluno> findByTurmaId(Long turmaId);
    /**
     * Busca alunos cujo nome contenha o texto informado (ignorando maiúsculas/minúsculas).
     *
     * @param nome trecho do nome a ser buscado
     * @return lista de alunos correspondentes
     */
    List<ApiAluno> findByNomeContainingIgnoreCase(String nome);
    /**
     * Busca um aluno pelo e-mail, ignorando maiúsculas/minúsculas.
     *
     * @param email e-mail do aluno
     * @return o aluno encontrado, ou vazio se não existir
     */
    Optional<ApiAluno> findByEmailIgnoreCase(String email);
}
