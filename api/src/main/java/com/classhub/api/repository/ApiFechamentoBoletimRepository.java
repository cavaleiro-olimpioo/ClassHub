package com.classhub.api.repository;

import com.classhub.api.domain.ApiFechamentoBoletim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiFechamentoBoletimRepository extends JpaRepository<ApiFechamentoBoletim, Long> {
    Optional<ApiFechamentoBoletim> findByAnoLetivoAndBimestreAndTurmaId(Integer anoLetivo, Integer bimestre, Long turmaId);
    Optional<ApiFechamentoBoletim> findByAnoLetivoAndBimestreAndTurmaIsNull(Integer anoLetivo, Integer bimestre);
}
