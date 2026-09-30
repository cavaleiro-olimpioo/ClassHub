package com.classhub.api.service;

import com.classhub.api.domain.*;
import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import com.classhub.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

/**
 * Serviço com a lógica de negócio do "diretório" acadêmico da escola:
 * CRUD de alunos, professores, séries, turmas, disciplinas e vínculos
 * (professor + turma + disciplina).
 * <p>
 * Também expõe métodos auxiliares ({@code requireX}) usados por outros
 * serviços para buscar entidades relacionadas, lançando
 * {@link NotFoundException} quando não encontradas, e métodos de conversão
 * de entidade para DTO ({@code aluno}, {@code professor} etc.) reutilizados
 * pelos demais serviços da aplicação.
 */
@Service
@Transactional
public class DirectoryService {
    private final ApiAlunoRepository alunos;
    private final ApiProfessorRepository professores;
    private final ApiSerieRepository series;
    private final ApiTurmaRepository turmas;
    private final ApiDisciplinaRepository disciplinas;
    private final ApiVinculoRepository vinculos;
    private final ApiNotaRepository notas;
    private final ApiPresencaRepository presencas;
    private final ApiOcorrenciaRepository ocorrencias;
    private final ApiHorarioRepository horarios;

    /**
     * Cria o serviço de diretório injetando todos os repositórios
     * necessários para as entidades acadêmicas e seus relacionamentos.
     *
     * @param alunos repositório de alunos
     * @param professores repositório de professores
     * @param series repositório de séries
     * @param turmas repositório de turmas
     * @param disciplinas repositório de disciplinas
     * @param vinculos repositório de vínculos professor/turma/disciplina
     * @param notas repositório de notas, usado para remover em cascata ao excluir um aluno
     * @param presencas repositório de presenças, usado para remover em cascata ao excluir um aluno
     * @param ocorrencias repositório de ocorrências, usado para remover em cascata ao excluir um aluno
     * @param horarios repositório de horários, usado para remover em cascata ao excluir turma/professor
     */
    public DirectoryService(ApiAlunoRepository alunos, ApiProfessorRepository professores, ApiSerieRepository series,
                            ApiTurmaRepository turmas, ApiDisciplinaRepository disciplinas, ApiVinculoRepository vinculos,
                            ApiNotaRepository notas, ApiPresencaRepository presencas, ApiOcorrenciaRepository ocorrencias,
                            ApiHorarioRepository horarios) {
        this.alunos = alunos; this.professores = professores; this.series = series;
        this.turmas = turmas; this.disciplinas = disciplinas; this.vinculos = vinculos;
        this.notas = notas; this.presencas = presencas; this.ocorrencias = ocorrencias;
        this.horarios = horarios;
    }

    /**
     * Lista alunos, com filtros opcionais por turma e/ou parte do nome,
     * ordenados por nome (ignorando maiúsculas/minúsculas).
     *
     * @param turmaId identificador da turma (opcional)
     * @param nome parte do nome do aluno para busca (opcional)
     * @return lista de alunos convertidos em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<AlunoResponse> listAlunos(Long turmaId, String nome) {
        List<ApiAluno> result = turmaId != null ? alunos.findByTurmaId(turmaId)
            : nome != null && !nome.isBlank() ? alunos.findByNomeContainingIgnoreCase(nome.trim()) : alunos.findAll();
        if (turmaId != null && nome != null && !nome.isBlank()) {
            result = result.stream().filter(a -> a.getNome().toLowerCase().contains(nome.trim().toLowerCase())).toList();
        }
        return result.stream().sorted(Comparator.comparing(ApiAluno::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::aluno).toList();
    }

    /**
     * Busca um aluno pelo identificador.
     *
     * @param id identificador do aluno
     * @return o aluno convertido em DTO de resposta
     * @throws NotFoundException se o aluno não existir
     */
    @Transactional(readOnly = true)
    public AlunoResponse getAluno(Long id) { return aluno(requireAluno(id)); }

    /**
     * Cria um novo aluno. Uma senha padrão é definida automaticamente
     * (ver {@link #applyAluno}).
     *
     * @param request dados do aluno a ser criado
     * @return o aluno recém-criado
     */
    public AlunoResponse createAluno(AlunoRequest request) {
        ApiAluno entity = new ApiAluno();
        applyAluno(entity, request);
        return aluno(alunos.save(entity));
    }

    /**
     * Exclui um aluno e todos os registros dependentes dele (notas,
     * presenças e ocorrências), para manter a integridade dos dados.
     *
     * @param id identificador do aluno a ser excluído
     * @throws NotFoundException se o aluno não existir
     */
    public void deleteAluno(Long id) {
        ApiAluno entity = requireAluno(id);
        notas.findByAlunoId(id).forEach(notas::delete);
        presencas.findByAlunoId(id).forEach(presencas::delete);
        ocorrencias.findByAlunoId(id).forEach(ocorrencias::delete);
        alunos.delete(entity);
    }

    /**
     * Atualiza os dados de um aluno existente.
     *
     * @param id identificador do aluno
     * @param request novos dados do aluno
     * @return o aluno atualizado
     * @throws NotFoundException se o aluno não existir
     */
    public AlunoResponse updateAluno(Long id, AlunoRequest request) {
        ApiAluno entity = requireAluno(id);
        applyAluno(entity, request);
        return aluno(entity);
    }

    /**
     * Altera a turma em que um aluno está matriculado (ou remove a
     * associação, se {@code turmaId} for {@code null}).
     *
     * @param id identificador do aluno
     * @param request nova turma do aluno
     * @return o aluno com a turma atualizada
     * @throws NotFoundException se o aluno ou a turma não existirem
     */
    public AlunoResponse changeTurma(Long id, AlunoTurmaRequest request) {
        ApiAluno entity = requireAluno(id);
        entity.setTurma(request.turmaId() == null ? null : requireTurma(request.turmaId()));
        return aluno(entity);
    }

    /**
     * Lista todos os professores, ordenados por nome (ignorando maiúsculas/minúsculas).
     *
     * @return lista de professores convertidos em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<ProfessorResponse> listProfessores() {
        return professores.findAll().stream().sorted(Comparator.comparing(ApiProfessor::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::professor).toList();
    }

    /**
     * Cria um novo professor. Uma senha padrão é definida automaticamente
     * (ver {@link #applyProfessor}).
     *
     * @param request dados do professor a ser criado
     * @return o professor recém-criado
     */
    public ProfessorResponse createProfessor(ProfessorRequest request) {
        ApiProfessor entity = new ApiProfessor(); applyProfessor(entity, request);
        return professor(professores.save(entity));
    }

    /**
     * Atualiza os dados de um professor existente.
     *
     * @param id identificador do professor
     * @param request novos dados do professor
     * @return o professor atualizado
     * @throws NotFoundException se o professor não existir
     */
    public ProfessorResponse updateProfessor(Long id, ProfessorRequest request) {
        ApiProfessor entity = requireProfessor(id); applyProfessor(entity, request); return professor(entity);
    }

    /**
     * Exclui um professor e seus horários e vínculos associados, para
     * manter a integridade dos dados.
     *
     * @param id identificador do professor a ser excluído
     * @throws NotFoundException se o professor não existir
     */
    public void deleteProfessor(Long id) {
        ApiProfessor entity = requireProfessor(id);
        horarios.findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(id).forEach(horarios::delete);
        vinculos.findByProfessorId(id).forEach(vinculos::delete);
        professores.delete(entity);
    }

    /**
     * Lista todas as séries, ordenadas por nome (ignorando maiúsculas/minúsculas).
     *
     * @return lista de séries convertidas em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<SerieResponse> listSeries() {
        return series.findAll().stream().sorted(Comparator.comparing(ApiSerie::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::serie).toList();
    }

    /**
     * Cria uma nova série.
     *
     * @param request dados da série a ser criada
     * @return a série recém-criada
     */
    public SerieResponse createSerie(SerieRequest request) {
        ApiSerie entity = new ApiSerie(); entity.setNome(request.nome().trim()); entity.setNivel(request.nivel().trim());
        return serie(series.save(entity));
    }

    /**
     * Atualiza os dados de uma série existente.
     *
     * @param id identificador da série
     * @param request novos dados da série
     * @return a série atualizada
     * @throws NotFoundException se a série não existir
     */
    public SerieResponse updateSerie(Long id, SerieRequest request) {
        ApiSerie entity = requireSerie(id);
        entity.setNome(request.nome().trim());
        entity.setNivel(request.nivel().trim());
        return serie(entity);
    }

    /**
     * Exclui uma série.
     *
     * @param id identificador da série a ser excluída
     * @throws NotFoundException se a série não existir
     */
    public void deleteSerie(Long id) {
        ApiSerie entity = requireSerie(id);
        series.delete(entity);
    }

    /**
     * Lista todas as turmas, ordenadas por nome (ignorando maiúsculas/minúsculas).
     *
     * @return lista de turmas convertidas em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<TurmaResponse> listTurmas() {
        return turmas.findAll().stream().sorted(Comparator.comparing(ApiTurma::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::turma).toList();
    }

    /**
     * Cria uma nova turma.
     *
     * @param request dados da turma a ser criada
     * @return a turma recém-criada
     * @throws NotFoundException se a série informada não existir
     */
    public TurmaResponse createTurma(TurmaRequest request) {
        ApiTurma entity = new ApiTurma(); applyTurma(entity, request); return turma(turmas.save(entity));
    }

    /**
     * Atualiza os dados de uma turma existente.
     *
     * @param id identificador da turma
     * @param request novos dados da turma
     * @return a turma atualizada
     * @throws NotFoundException se a turma ou a série informada não existirem
     */
    public TurmaResponse updateTurma(Long id, TurmaRequest request) {
        ApiTurma entity = requireTurma(id); applyTurma(entity, request); return turma(entity);
    }

    /**
     * Exclui uma turma, desassociando os alunos matriculados nela e
     * removendo seus horários e vínculos associados, para manter a
     * integridade dos dados.
     *
     * @param id identificador da turma a ser excluída
     * @throws NotFoundException se a turma não existir
     */
    public void deleteTurma(Long id) {
        ApiTurma entity = requireTurma(id);
        alunos.findByTurmaId(id).forEach(a -> { a.setTurma(null); alunos.save(a); });
        horarios.findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(id).forEach(horarios::delete);
        vinculos.findAll().stream().filter(v -> v.getTurma().getId().equals(id)).forEach(vinculos::delete);
        turmas.delete(entity);
    }

    /**
     * Lista todas as disciplinas, ordenadas por nome (ignorando maiúsculas/minúsculas).
     *
     * @return lista de disciplinas convertidas em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<DisciplinaResponse> listDisciplinas() {
        return disciplinas.findAll().stream().sorted(Comparator.comparing(ApiDisciplina::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::disciplina).toList();
    }

    /**
     * Cria uma nova disciplina.
     *
     * @param request dados da disciplina a ser criada
     * @return a disciplina recém-criada
     */
    public DisciplinaResponse createDisciplina(DisciplinaRequest request) {
        ApiDisciplina entity = new ApiDisciplina(); applyDisciplina(entity, request); return disciplina(disciplinas.save(entity));
    }

    /**
     * Atualiza os dados de uma disciplina existente.
     *
     * @param id identificador da disciplina
     * @param request novos dados da disciplina
     * @return a disciplina atualizada
     * @throws NotFoundException se a disciplina não existir
     */
    public DisciplinaResponse updateDisciplina(Long id, DisciplinaRequest request) {
        ApiDisciplina entity = requireDisciplina(id); applyDisciplina(entity, request); return disciplina(entity);
    }

    /**
     * Exclui uma disciplina, removendo seus vínculos e horários
     * associados, para manter a integridade dos dados.
     *
     * @param id identificador da disciplina a ser excluída
     * @throws NotFoundException se a disciplina não existir
     */
    public void deleteDisciplina(Long id) {
        ApiDisciplina entity = requireDisciplina(id);
        vinculos.findAll().stream().filter(v -> v.getDisciplina().getId().equals(id)).forEach(vinculos::delete);
        horarios.findAll().stream().filter(h -> h.getDisciplina().getId().equals(id)).forEach(horarios::delete);
        disciplinas.delete(entity);
    }

    /**
     * Lista vínculos entre professores, turmas e disciplinas, com filtro
     * opcional por professor.
     *
     * @param professorId identificador do professor (opcional)
     * @return lista de vínculos convertidos em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<VinculoResponse> listVinculos(Long professorId) {
        List<ApiVinculo> result = professorId == null ? vinculos.findAll() : vinculos.findByProfessorId(professorId);
        return result.stream().map(this::vinculo).toList();
    }

    /**
     * Cria um novo vínculo entre professor, turma e disciplina, em um
     * determinado ano letivo.
     *
     * @param request dados do vínculo a ser criado
     * @return o vínculo recém-criado
     * @throws NotFoundException se o professor, a turma ou a disciplina não existirem
     */
    public VinculoResponse createVinculo(VinculoRequest request) {
        ApiVinculo entity = new ApiVinculo();
        entity.setProfessor(requireProfessor(request.professorId()));
        entity.setTurma(requireTurma(request.turmaId()));
        entity.setDisciplina(requireDisciplina(request.disciplinaId()));
        entity.setAnoLetivo(request.anoLetivo());
        return vinculo(vinculos.save(entity));
    }

    /**
     * Exclui um vínculo entre professor, turma e disciplina.
     *
     * @param id identificador do vínculo a ser excluído
     * @throws NotFoundException se o vínculo não existir
     */
    public void deleteVinculo(Long id) { vinculos.delete(requireVinculo(id)); }

    /**
     * Busca um aluno pelo identificador, lançando exceção caso não exista.
     * Reutilizado por outros serviços da aplicação.
     *
     * @param id identificador do aluno
     * @return a entidade do aluno encontrada
     * @throws NotFoundException se o aluno não existir
     */
    public ApiAluno requireAluno(Long id) { return alunos.findById(id).orElseThrow(() -> new NotFoundException("Aluno não encontrado.")); }
    /**
     * Busca um professor pelo identificador, lançando exceção caso não exista.
     * Reutilizado por outros serviços da aplicação.
     *
     * @param id identificador do professor
     * @return a entidade do professor encontrada
     * @throws NotFoundException se o professor não existir
     */
    public ApiProfessor requireProfessor(Long id) { return professores.findById(id).orElseThrow(() -> new NotFoundException("Professor não encontrado.")); }
    /**
     * Busca uma turma pelo identificador, lançando exceção caso não exista.
     * Reutilizado por outros serviços da aplicação.
     *
     * @param id identificador da turma
     * @return a entidade da turma encontrada
     * @throws NotFoundException se a turma não existir
     */
    public ApiTurma requireTurma(Long id) { return turmas.findById(id).orElseThrow(() -> new NotFoundException("Turma não encontrada.")); }
    /**
     * Busca uma disciplina pelo identificador, lançando exceção caso não exista.
     * Reutilizado por outros serviços da aplicação.
     *
     * @param id identificador da disciplina
     * @return a entidade da disciplina encontrada
     * @throws NotFoundException se a disciplina não existir
     */
    public ApiDisciplina requireDisciplina(Long id) { return disciplinas.findById(id).orElseThrow(() -> new NotFoundException("Disciplina não encontrada.")); }
    /**
     * Busca uma série pelo identificador, lançando exceção caso não exista.
     *
     * @param id identificador da série
     * @return a entidade da série encontrada
     * @throws NotFoundException se a série não existir
     */
    private ApiSerie requireSerie(Long id) { return series.findById(id).orElseThrow(() -> new NotFoundException("Série não encontrada.")); }
    /**
     * Busca um vínculo pelo identificador, lançando exceção caso não exista.
     *
     * @param id identificador do vínculo
     * @return a entidade do vínculo encontrada
     * @throws NotFoundException se o vínculo não existir
     */
    private ApiVinculo requireVinculo(Long id) { return vinculos.findById(id).orElseThrow(() -> new NotFoundException("Vínculo não encontrado.")); }

    /**
     * Aplica os dados de um {@link AlunoRequest} sobre a entidade,
     * normalizando nome/e-mail e definindo uma senha padrão ("aluno123")
     * e telefone padrão, além de associar a turma informada (se houver).
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     */
    private void applyAluno(ApiAluno entity, AlunoRequest request) {
        entity.setNome(request.nome().trim());
        entity.setEmail(request.email().trim().toLowerCase());
        entity.setSenhaHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("aluno123"));
        entity.setTelefone("(11) 90000-0000");
        entity.setMatricula(request.matricula().trim());
        entity.setDataNascimento(request.dataNascimento());
        entity.setTurma(request.turmaId() == null ? null : requireTurma(request.turmaId()));
    }
    /**
     * Aplica os dados de um {@link ProfessorRequest} sobre a entidade,
     * normalizando nome/e-mail e definindo uma senha padrão ("professor123"),
     * telefone e data de nascimento padrão.
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     */
    private void applyProfessor(ApiProfessor entity, ProfessorRequest request) {
        entity.setNome(request.nome().trim());
        entity.setEmail(request.email().trim().toLowerCase());
        entity.setSenhaHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("professor123"));
        entity.setTelefone("(11) 90000-0000");
        entity.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
    }
    /**
     * Aplica os dados de um {@link TurmaRequest} sobre a entidade,
     * associando a série informada e garantindo um turno padrão
     * ("MATUTINO") quando não definido.
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     */
    private void applyTurma(ApiTurma entity, TurmaRequest request) {
        entity.setNome(request.nome().trim());
        entity.setSerie(requireSerie(request.serieId()));
        entity.setAnoLetivo(request.anoLetivo());
        if (entity.getTurno() == null || entity.getTurno().isBlank()) {
            entity.setTurno("MATUTINO");
        }
    }
    /**
     * Aplica os dados de um {@link DisciplinaRequest} sobre a entidade.
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     */
    private void applyDisciplina(ApiDisciplina entity, DisciplinaRequest request) { entity.setNome(request.nome().trim()); entity.setCargaHoraria(request.cargaHoraria()); }

    /**
     * Converte uma entidade {@link ApiAluno} para o DTO {@link AlunoResponse}.
     *
     * @param a entidade do aluno
     * @return DTO de resposta correspondente
     */
    public AlunoResponse aluno(ApiAluno a) {
        TurmaSummary turma = a.getTurma() == null ? null : turmaSummary(a.getTurma());
        return new AlunoResponse(a.getId(), a.getNome(), a.getEmail(), a.getMatricula(), a.getDataNascimento(),
            a.getTurma() == null ? null : a.getTurma().getId(), turma, turma == null ? null : turma.nome());
    }
    /** Converte uma entidade {@link ApiAluno} para o resumo {@link AlunoSummary}. */
    public AlunoSummary alunoSummary(ApiAluno a) { return new AlunoSummary(a.getId(), a.getNome(), a.getMatricula()); }
    /** Converte uma entidade {@link ApiProfessor} para o DTO {@link ProfessorResponse}. */
    public ProfessorResponse professor(ApiProfessor p) { return new ProfessorResponse(p.getId(), p.getNome(), p.getEmail()); }
    /** Converte uma entidade {@link ApiProfessor} para o resumo {@link ProfessorSummary}. */
    public ProfessorSummary professorSummary(ApiProfessor p) { return new ProfessorSummary(p.getId(), p.getNome()); }
    /** Converte uma entidade {@link ApiSerie} para o DTO {@link SerieResponse}. */
    public SerieResponse serie(ApiSerie s) { return new SerieResponse(s.getId(), s.getNome(), s.getNivel()); }
    /** Converte uma entidade {@link ApiSerie} para o resumo {@link SerieSummary}. */
    public SerieSummary serieSummary(ApiSerie s) { return new SerieSummary(s.getId(), s.getNome()); }
    /** Converte uma entidade {@link ApiTurma} para o DTO {@link TurmaResponse}. */
    public TurmaResponse turma(ApiTurma t) { return new TurmaResponse(t.getId(), t.getNome(), serieSummary(t.getSerie()), t.getSerie().getId(), t.getAnoLetivo()); }
    /** Converte uma entidade {@link ApiTurma} para o resumo {@link TurmaSummary}. */
    public TurmaSummary turmaSummary(ApiTurma t) { return new TurmaSummary(t.getId(), t.getNome(), t.getAnoLetivo()); }
    /** Converte uma entidade {@link ApiDisciplina} para o DTO {@link DisciplinaResponse}. */
    public DisciplinaResponse disciplina(ApiDisciplina d) { return new DisciplinaResponse(d.getId(), d.getNome(), d.getCargaHoraria()); }
    /** Converte uma entidade {@link ApiDisciplina} para o resumo {@link DisciplinaSummary}. */
    public DisciplinaSummary disciplinaSummary(ApiDisciplina d) { return new DisciplinaSummary(d.getId(), d.getNome()); }
    /** Converte uma entidade {@link ApiVinculo} para o DTO {@link VinculoResponse}. */
    private VinculoResponse vinculo(ApiVinculo v) { return new VinculoResponse(v.getId(), v.getProfessor().getId(), professorSummary(v.getProfessor()), v.getTurma().getId(), turmaSummary(v.getTurma()), v.getDisciplina().getId(), disciplinaSummary(v.getDisciplina()), v.getAnoLetivo()); }
}
