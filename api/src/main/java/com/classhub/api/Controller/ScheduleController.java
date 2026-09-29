package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST responsável pela grade de horários das turmas e pelos
 * registros de presença/falta dos alunos.
 */
@RestController
@RequestMapping(path = {"", "/api"})
public class ScheduleController {
    private final ScheduleService service;
    /**
     * Cria o controlador injetando o serviço de horários e presenças.
     *
     * @param service serviço com a lógica de negócio de horários e presenças
     */
    public ScheduleController(ScheduleService service) { this.service = service; }

    /**
     * Lista os horários de aula de uma turma específica.
     *
     * @param turmaId identificador da turma
     * @return lista de horários da turma
     */
    @GetMapping("/horarios/turma/{turmaId}") public List<HorarioResponse> horariosTurma(@PathVariable Long turmaId) { return service.horariosTurma(turmaId); }
    /**
     * Lista os horários de aula de um professor específico.
     *
     * @param professorId identificador do professor
     * @return lista de horários do professor
     */
    @GetMapping("/horarios/professor/{professorId}") public List<HorarioResponse> horariosProfessor(@PathVariable Long professorId) { return service.horariosProfessor(professorId); }
    /**
     * Cria um novo horário de aula para uma turma.
     *
     * @param request dados do horário a ser criado
     * @return o horário recém-criado
     */
    @PostMapping("/horarios") @ResponseStatus(HttpStatus.CREATED) public HorarioResponse criarHorario(@Valid @RequestBody HorarioRequest request) { return service.createHorario(request); }
    /**
     * Exclui um horário de aula.
     *
     * @param id identificador do horário a ser excluído
     */
    @DeleteMapping("/horarios/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirHorario(@PathVariable Long id) { service.deleteHorario(id); }

    /**
     * Lista registros de presença/falta, com filtros opcionais por aluno,
     * turma, disciplina e data.
     *
     * @param alunoId identificador do aluno (opcional)
     * @param turmaId identificador da turma (opcional)
     * @param disciplinaId identificador da disciplina (opcional)
     * @param data data da aula (opcional)
     * @return lista de registros de presença que atendem aos filtros informados
     */
    @GetMapping("/presencas")
    public List<PresencaResponse> presencas(@RequestParam(required = false) Long alunoId, @RequestParam(required = false) Long turmaId,
                                             @RequestParam(required = false) Long disciplinaId, @RequestParam(required = false) LocalDate data) {
        return service.listPresencas(alunoId, turmaId, disciplinaId, data);
    }
    /**
     * Salva em lote os registros de presença/falta de uma chamada (ex.: de uma turma inteira em um dia).
     *
     * @param requests lista de registros de presença a serem salvos
     * @return lista de registros de presença salvos
     */
    @PostMapping("/presencas/lote") public List<PresencaResponse> salvarPresencas(@Valid @RequestBody List<@Valid PresencaRequest> requests) { return service.savePresencas(requests); }
}
