package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ScheduleController {
    private final ScheduleService service;
    public ScheduleController(ScheduleService service) { this.service = service; }

    @GetMapping("/horarios/turma/{turmaId}") public List<HorarioResponse> horariosTurma(@PathVariable Long turmaId) { return service.horariosTurma(turmaId); }
    @GetMapping("/horarios/professor/{professorId}") public List<HorarioResponse> horariosProfessor(@PathVariable Long professorId) { return service.horariosProfessor(professorId); }
    @PostMapping("/horarios") @ResponseStatus(HttpStatus.CREATED) public HorarioResponse criarHorario(@Valid @RequestBody HorarioRequest request) { return service.createHorario(request); }
    @DeleteMapping("/horarios/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirHorario(@PathVariable Long id) { service.deleteHorario(id); }

    @GetMapping("/presencas")
    public List<PresencaResponse> presencas(@RequestParam(required = false) Long alunoId, @RequestParam(required = false) Long turmaId,
                                             @RequestParam(required = false) Long disciplinaId, @RequestParam(required = false) LocalDate data) {
        return service.listPresencas(alunoId, turmaId, disciplinaId, data);
    }
    @PostMapping("/presencas/lote") public List<PresencaResponse> salvarPresencas(@Valid @RequestBody List<@Valid PresencaRequest> requests) { return service.savePresencas(requests); }
}
