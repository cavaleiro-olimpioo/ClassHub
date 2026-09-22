package com.classhub.api.repository;

import com.classhub.api.domain.ApiPresenca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApiPresencaRepository extends JpaRepository<ApiPresenca, Long> {
    List<ApiPresenca> findByAlunoId(Long alunoId);
    List<ApiPresenca> findByTurmaIdAndDisciplinaIdAndData(Long turmaId, Long disciplinaId, LocalDate data);
    List<ApiPresenca> findByTurmaId(Long turmaId);
    Optional<ApiPresenca> findByAlunoIdAndTurmaIdAndDisciplinaIdAndData(Long alunoId, Long turmaId, Long disciplinaId, LocalDate data);
}
