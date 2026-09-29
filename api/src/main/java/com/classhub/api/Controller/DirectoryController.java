package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.DirectoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST responsável pelo "diretório" acadêmico da escola:
 * CRUD de alunos, professores, séries, turmas, disciplinas e vínculos
 * (professor + turma + disciplina).
 */
@RestController
@RequestMapping(path = {"", "/api"})
public class DirectoryController {
    private final DirectoryService service;
    /**
     * Cria o controlador injetando o serviço de diretório.
     *
     * @param service serviço com a lógica de negócio do diretório acadêmico
     */
    public DirectoryController(DirectoryService service) { this.service = service; }

    /**
     * Lista os alunos cadastrados, com filtros opcionais por turma e nome.
     *
     * @param turmaId identificador da turma (opcional)
     * @param nome parte do nome do aluno para busca (opcional)
     * @return lista de alunos que atendem aos filtros informados
     */
    @GetMapping("/alunos") public List<AlunoResponse> alunos(@RequestParam(required = false) Long turmaId, @RequestParam(required = false) String nome) { return service.listAlunos(turmaId, nome); }
    /**
     * Busca um aluno pelo identificador.
     *
     * @param id identificador do aluno
     * @return os dados completos do aluno
     */
    @GetMapping("/alunos/{id}") public AlunoResponse aluno(@PathVariable Long id) { return service.getAluno(id); }
    /**
     * Cria um novo aluno.
     *
     * @param request dados do aluno a ser criado
     * @return o aluno recém-criado
     */
    @PostMapping("/alunos") @ResponseStatus(HttpStatus.CREATED) public AlunoResponse criarAluno(@Valid @RequestBody AlunoRequest request) { return service.createAluno(request); }
    /**
     * Atualiza os dados de um aluno existente.
     *
     * @param id identificador do aluno
     * @param request novos dados do aluno
     * @return o aluno atualizado
     */
    @PutMapping("/alunos/{id}") public AlunoResponse editarAluno(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) { return service.updateAluno(id, request); }
    /**
     * Altera a turma em que um aluno está matriculado.
     *
     * @param id identificador do aluno
     * @param request nova turma do aluno
     * @return o aluno com a turma atualizada
     */
    @PutMapping("/alunos/{id}/turma") public AlunoResponse alterarTurma(@PathVariable Long id, @RequestBody AlunoTurmaRequest request) { return service.changeTurma(id, request); }
    /**
     * Exclui um aluno.
     *
     * @param id identificador do aluno a ser excluído
     */
    @DeleteMapping("/alunos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirAluno(@PathVariable Long id) { service.deleteAluno(id); }

    /**
     * Lista todos os professores cadastrados.
     *
     * @return lista de professores
     */
    @GetMapping("/professores") public List<ProfessorResponse> professores() { return service.listProfessores(); }
    /**
     * Cria um novo professor.
     *
     * @param request dados do professor a ser criado
     * @return o professor recém-criado
     */
    @PostMapping("/professores") @ResponseStatus(HttpStatus.CREATED) public ProfessorResponse criarProfessor(@Valid @RequestBody ProfessorRequest request) { return service.createProfessor(request); }
    /**
     * Atualiza os dados de um professor existente.
     *
     * @param id identificador do professor
     * @param request novos dados do professor
     * @return o professor atualizado
     */
    @PutMapping("/professores/{id}") public ProfessorResponse editarProfessor(@PathVariable Long id, @Valid @RequestBody ProfessorRequest request) { return service.updateProfessor(id, request); }
    /**
     * Exclui um professor.
     *
     * @param id identificador do professor a ser excluído
     */
    @DeleteMapping("/professores/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirProfessor(@PathVariable Long id) { service.deleteProfessor(id); }

    /**
     * Lista todas as séries cadastradas.
     *
     * @return lista de séries
     */
    @GetMapping("/series") public List<SerieResponse> series() { return service.listSeries(); }
    /**
     * Cria uma nova série.
     *
     * @param request dados da série a ser criada
     * @return a série recém-criada
     */
    @PostMapping("/series") @ResponseStatus(HttpStatus.CREATED) public SerieResponse criarSerie(@Valid @RequestBody SerieRequest request) { return service.createSerie(request); }
    /**
     * Atualiza os dados de uma série existente.
     *
     * @param id identificador da série
     * @param request novos dados da série
     * @return a série atualizada
     */
    @PutMapping("/series/{id}") public SerieResponse editarSerie(@PathVariable Long id, @Valid @RequestBody SerieRequest request) { return service.updateSerie(id, request); }
    /**
     * Exclui uma série.
     *
     * @param id identificador da série a ser excluída
     */
    @DeleteMapping("/series/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirSerie(@PathVariable Long id) { service.deleteSerie(id); }

    /**
     * Lista todas as turmas cadastradas.
     *
     * @return lista de turmas
     */
    @GetMapping("/turmas") public List<TurmaResponse> turmas() { return service.listTurmas(); }
    /**
     * Cria uma nova turma.
     *
     * @param request dados da turma a ser criada
     * @return a turma recém-criada
     */
    @PostMapping("/turmas") @ResponseStatus(HttpStatus.CREATED) public TurmaResponse criarTurma(@Valid @RequestBody TurmaRequest request) { return service.createTurma(request); }
    /**
     * Atualiza os dados de uma turma existente.
     *
     * @param id identificador da turma
     * @param request novos dados da turma
     * @return a turma atualizada
     */
    @PutMapping("/turmas/{id}") public TurmaResponse editarTurma(@PathVariable Long id, @Valid @RequestBody TurmaRequest request) { return service.updateTurma(id, request); }
    /**
     * Exclui uma turma.
     *
     * @param id identificador da turma a ser excluída
     */
    @DeleteMapping("/turmas/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirTurma(@PathVariable Long id) { service.deleteTurma(id); }

    /**
     * Lista todas as disciplinas cadastradas.
     *
     * @return lista de disciplinas
     */
    @GetMapping("/disciplinas") public List<DisciplinaResponse> disciplinas() { return service.listDisciplinas(); }
    /**
     * Cria uma nova disciplina.
     *
     * @param request dados da disciplina a ser criada
     * @return a disciplina recém-criada
     */
    @PostMapping("/disciplinas") @ResponseStatus(HttpStatus.CREATED) public DisciplinaResponse criarDisciplina(@Valid @RequestBody DisciplinaRequest request) { return service.createDisciplina(request); }
    /**
     * Atualiza os dados de uma disciplina existente.
     *
     * @param id identificador da disciplina
     * @param request novos dados da disciplina
     * @return a disciplina atualizada
     */
    @PutMapping("/disciplinas/{id}") public DisciplinaResponse editarDisciplina(@PathVariable Long id, @Valid @RequestBody DisciplinaRequest request) { return service.updateDisciplina(id, request); }
    /**
     * Exclui uma disciplina.
     *
     * @param id identificador da disciplina a ser excluída
     */
    @DeleteMapping("/disciplinas/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirDisciplina(@PathVariable Long id) { service.deleteDisciplina(id); }

    /**
     * Lista os vínculos entre professores, turmas e disciplinas, com filtro opcional por professor.
     *
     * @param professorId identificador do professor (opcional)
     * @return lista de vínculos que atendem ao filtro informado
     */
    @GetMapping("/vinculos") public List<VinculoResponse> vinculos(@RequestParam(required = false) Long professorId) { return service.listVinculos(professorId); }
    /**
     * Cria um novo vínculo entre professor, turma e disciplina.
     *
     * @param request dados do vínculo a ser criado
     * @return o vínculo recém-criado
     */
    @PostMapping("/vinculos") @ResponseStatus(HttpStatus.CREATED) public VinculoResponse criarVinculo(@Valid @RequestBody VinculoRequest request) { return service.createVinculo(request); }
    /**
     * Exclui um vínculo entre professor, turma e disciplina.
     *
     * @param id identificador do vínculo a ser excluído
     */
    @DeleteMapping("/vinculos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirVinculo(@PathVariable Long id) { service.deleteVinculo(id); }
}
