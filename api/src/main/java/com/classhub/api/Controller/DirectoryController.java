package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.DirectoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = {"", "/api"})
public class DirectoryController {
    private final DirectoryService service;
    public DirectoryController(DirectoryService service) { this.service = service; }

    @GetMapping("/alunos") public List<AlunoResponse> alunos(@RequestParam(required = false) Long turmaId, @RequestParam(required = false) String nome) { return service.listAlunos(turmaId, nome); }
    @GetMapping("/alunos/{id}") public AlunoResponse aluno(@PathVariable Long id) { return service.getAluno(id); }
    @PostMapping("/alunos") @ResponseStatus(HttpStatus.CREATED) public AlunoResponse criarAluno(@Valid @RequestBody AlunoRequest request) { return service.createAluno(request); }
    @PutMapping("/alunos/{id}") public AlunoResponse editarAluno(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) { return service.updateAluno(id, request); }
    @PutMapping("/alunos/{id}/turma") public AlunoResponse alterarTurma(@PathVariable Long id, @RequestBody AlunoTurmaRequest request) { return service.changeTurma(id, request); }
    @DeleteMapping("/alunos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirAluno(@PathVariable Long id) { service.deleteAluno(id); }

    @GetMapping("/professores") public List<ProfessorResponse> professores() { return service.listProfessores(); }
    @PostMapping("/professores") @ResponseStatus(HttpStatus.CREATED) public ProfessorResponse criarProfessor(@Valid @RequestBody ProfessorRequest request) { return service.createProfessor(request); }
    @PutMapping("/professores/{id}") public ProfessorResponse editarProfessor(@PathVariable Long id, @Valid @RequestBody ProfessorRequest request) { return service.updateProfessor(id, request); }
    @DeleteMapping("/professores/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirProfessor(@PathVariable Long id) { service.deleteProfessor(id); }

    @GetMapping("/series") public List<SerieResponse> series() { return service.listSeries(); }
    @PostMapping("/series") @ResponseStatus(HttpStatus.CREATED) public SerieResponse criarSerie(@Valid @RequestBody SerieRequest request) { return service.createSerie(request); }
    @PutMapping("/series/{id}") public SerieResponse editarSerie(@PathVariable Long id, @Valid @RequestBody SerieRequest request) { return service.updateSerie(id, request); }
    @DeleteMapping("/series/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirSerie(@PathVariable Long id) { service.deleteSerie(id); }

    @GetMapping("/turmas") public List<TurmaResponse> turmas() { return service.listTurmas(); }
    @PostMapping("/turmas") @ResponseStatus(HttpStatus.CREATED) public TurmaResponse criarTurma(@Valid @RequestBody TurmaRequest request) { return service.createTurma(request); }
    @PutMapping("/turmas/{id}") public TurmaResponse editarTurma(@PathVariable Long id, @Valid @RequestBody TurmaRequest request) { return service.updateTurma(id, request); }
    @DeleteMapping("/turmas/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirTurma(@PathVariable Long id) { service.deleteTurma(id); }

    @GetMapping("/disciplinas") public List<DisciplinaResponse> disciplinas() { return service.listDisciplinas(); }
    @PostMapping("/disciplinas") @ResponseStatus(HttpStatus.CREATED) public DisciplinaResponse criarDisciplina(@Valid @RequestBody DisciplinaRequest request) { return service.createDisciplina(request); }
    @PutMapping("/disciplinas/{id}") public DisciplinaResponse editarDisciplina(@PathVariable Long id, @Valid @RequestBody DisciplinaRequest request) { return service.updateDisciplina(id, request); }
    @DeleteMapping("/disciplinas/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirDisciplina(@PathVariable Long id) { service.deleteDisciplina(id); }

    @GetMapping("/vinculos") public List<VinculoResponse> vinculos(@RequestParam(required = false) Long professorId) { return service.listVinculos(professorId); }
    @PostMapping("/vinculos") @ResponseStatus(HttpStatus.CREATED) public VinculoResponse criarVinculo(@Valid @RequestBody VinculoRequest request) { return service.createVinculo(request); }
    @DeleteMapping("/vinculos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirVinculo(@PathVariable Long id) { service.deleteVinculo(id); }
}
