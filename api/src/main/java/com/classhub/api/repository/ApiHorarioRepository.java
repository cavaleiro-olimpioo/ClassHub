package com.classhub.api.repository;

import com.classhub.api.domain.ApiHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiHorario}, com
 * consultas para listar horários por turma ou professor e para checar
 * conflitos de horário em um mesmo dia da semana.
 */
public interface ApiHorarioRepository extends JpaRepository<ApiHorario, Long> {
    /**
     * Lista os horários de uma turma, ordenados por dia da semana e horário de início.
     *
     * @param turmaId identificador da turma
     * @return lista ordenada de horários da turma
     */
    List<ApiHorario> findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(Long turmaId);
    /**
     * Lista os horários de um professor, ordenados por dia da semana e horário de início.
     *
     * @param professorId identificador do professor
     * @return lista ordenada de horários do professor
     */
    List<ApiHorario> findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(Long professorId);
    /**
     * Busca os horários de uma turma em um dia da semana específico
     * (usado para detectar conflitos de horário).
     *
     * @param turmaId identificador da turma
     * @param diaSemana dia da semana (1 a 5)
     * @return lista de horários da turma no dia informado
     */
    List<ApiHorario> findByTurmaIdAndDiaSemana(Long turmaId, Integer diaSemana);
    /**
     * Busca os horários de um professor em um dia da semana específico
     * (usado para detectar conflitos de horário).
     *
     * @param professorId identificador do professor
     * @param diaSemana dia da semana (1 a 5)
     * @return lista de horários do professor no dia informado
     */
    List<ApiHorario> findByProfessorIdAndDiaSemana(Long professorId, Integer diaSemana);
}
