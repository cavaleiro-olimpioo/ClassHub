package com.classhub.api.repository;

import com.classhub.api.domain.ApiNota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApiNotaRepository extends JpaRepository<ApiNota, Long> {
    List<ApiNota> findByAlunoId(Long alunoId);
    List<ApiNota> findByTurmaIdAndDisciplinaIdAndBimestre(Long turmaId, Long disciplinaId, Integer bimestre);
    List<ApiNota> findByAlunoIdAndBimestre(Long alunoId, Integer bimestre);
}
