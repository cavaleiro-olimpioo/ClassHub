package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import java.util.List;
import com.classhub.api.service.DirectoryService;

/**
 * Controlador REST responsável pela grade de horários das turmas e pelos
 * registros de presença/falta dos alunos.
 */
@RestController
@RequestMapping(path = {"", "/api"})
public class ScheduleController {
    private final ScheduleService service;
    private final DirectoryService directory;
    /**
     * Cria o controlador injetando o serviço de horários e presenças.
     *
     * @param service serviço com a lógica de negócio de horários e presenças
     */
    public ScheduleController(ScheduleService service, DirectoryService directory) { this.service = service; this.directory = directory; }

    /**
     * Lista os horários de aula de uma turma específica.
     *
     * @param turmaId identificador da turma
     * @return lista de horários da turma
     */
    @GetMapping("/horarios/turma/{turmaId}") @PreAuthorize("hasAnyRole('ADMIN','ALUNO')") public List<HorarioResponse> horariosTurma(@PathVariable Long turmaId, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"))) {
            var turma = directory.requireAluno(Long.valueOf(authentication.getName())).getTurma();
            if (turma == null || !turma.getId().equals(turmaId)) throw new com.classhub.api.exception.ApiExceptions.ForbiddenException("Acesso negado à turma.");
        }
        return service.horariosTurma(turmaId);
    }
    /**
     * Lista os horários de aula de um professor específico.
     *
     * @param professorId identificador do professor
     * @return lista de horários do professor
     */
    @GetMapping("/horarios/professor/{professorId}") @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')") public List<HorarioResponse> horariosProfessor(@PathVariable Long professorId, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")) && !Long.valueOf(authentication.getName()).equals(professorId)) throw new com.classhub.api.exception.ApiExceptions.ForbiddenException("Acesso negado aos horários de outro professor.");
        return service.horariosProfessor(professorId);
    }
    /**
     * Cria um novo horário de aula para uma turma.
     *
     * @param request dados do horário a ser criado
     * @return o horário recém-criado
     */
    @PostMapping("/horarios") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.CREATED) public HorarioResponse criarHorario(@Valid @RequestBody HorarioRequest request) { return service.createHorario(request); }
    /**
     * Exclui um horário de aula.
     *
     * @param id identificador do horário a ser excluído
     */
    @DeleteMapping("/horarios/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirHorario(@PathVariable Long id) { service.deleteHorario(id); }

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
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public List<PresencaResponse> presencas(@RequestParam(required = false) Long alunoId, @RequestParam(required = false) Long turmaId,
                                             @RequestParam(required = false) Long disciplinaId, @RequestParam(required = false) LocalDate data, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"))) alunoId = Long.valueOf(authentication.getName());
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR"))) {
            Long id = Long.valueOf(authentication.getName());
            var links = directory.listVinculos(id);
            if (turmaId != null && links.stream().noneMatch(v -> v.turmaId().equals(turmaId) && (disciplinaId == null || v.disciplinaId().equals(disciplinaId)))) return List.of();
            if (turmaId == null) return List.of();
        }
        return service.listPresencas(alunoId, turmaId, disciplinaId, data);
    }
    /**
     * Salva em lote os registros de presença/falta de uma chamada (ex.: de uma turma inteira em um dia).
     *
     * @param requests lista de registros de presença a serem salvos
     * @return lista de registros de presença salvos
     */
    @PostMapping("/presencas/lote") @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')") public List<PresencaResponse> salvarPresencas(@Valid @RequestBody List<@Valid PresencaRequest> requests, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR"))) {
            var links = directory.listVinculos(Long.valueOf(authentication.getName()));
            boolean allowed = requests.stream().allMatch(r -> links.stream().anyMatch(v -> v.turmaId().equals(r.turmaId()) && v.disciplinaId().equals(r.disciplinaId())));
            if (!allowed) throw new com.classhub.api.exception.ApiExceptions.ForbiddenException("O professor não possui vínculo com a turma e disciplina informadas.");
        }
        return service.savePresencas(requests);
    }
}
