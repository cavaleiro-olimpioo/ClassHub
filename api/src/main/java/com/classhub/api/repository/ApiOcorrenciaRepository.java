package com.classhub.api.repository;

import com.classhub.api.domain.ApiOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApiOcorrenciaRepository extends JpaRepository<ApiOcorrencia, Long> {
    List<ApiOcorrencia> findByAlunoId(Long alunoId);
}
