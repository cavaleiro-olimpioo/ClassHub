package com.classhub.api.repository;

import com.classhub.api.domain.ApiFechamentoBoletim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiFechamentoBoletim},
 * usado para verificar se um bimestre já foi fechado para uma turma (ou
 * para todas as turmas).
 */
public interface ApiFechamentoBoletimRepository extends JpaRepository<ApiFechamentoBoletim, Long> {
    /**
     * Busca o fechamento de um bimestre para uma turma específica.
     *
     * @param anoLetivo ano letivo do fechamento
     * @param bimestre número do bimestre
     * @param turmaId identificador da turma
     * @return o fechamento encontrado, ou vazio se o bimestre não tiver sido fechado para essa turma
     */
    Optional<ApiFechamentoBoletim> findByAnoLetivoAndBimestreAndTurmaId(Integer anoLetivo, Integer bimestre, Long turmaId);
    /**
     * Busca o fechamento de um bimestre aplicado a todas as turmas (sem turma específica associada).
     *
     * @param anoLetivo ano letivo do fechamento
     * @param bimestre número do bimestre
     * @return o fechamento encontrado, ou vazio se não existir
     */
    Optional<ApiFechamentoBoletim> findByAnoLetivoAndBimestreAndTurmaIsNull(Integer anoLetivo, Integer bimestre);
}
