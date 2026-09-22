package com.classhub.api.repository;

import com.classhub.api.domain.ApiHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalTime;
import java.util.List;

public interface ApiHorarioRepository extends JpaRepository<ApiHorario, Long> {
    List<ApiHorario> findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(Long turmaId);
    List<ApiHorario> findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(Long professorId);
    List<ApiHorario> findByTurmaIdAndDiaSemana(Long turmaId, Integer diaSemana);
    List<ApiHorario> findByProfessorIdAndDiaSemana(Long professorId, Integer diaSemana);
}
