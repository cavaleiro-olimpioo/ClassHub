package com.classhub.api.service;

import com.classhub.api.domain.*;
import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import com.classhub.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

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

    public DirectoryService(ApiAlunoRepository alunos, ApiProfessorRepository professores, ApiSerieRepository series,
                            ApiTurmaRepository turmas, ApiDisciplinaRepository disciplinas, ApiVinculoRepository vinculos,
                            ApiNotaRepository notas, ApiPresencaRepository presencas, ApiOcorrenciaRepository ocorrencias,
                            ApiHorarioRepository horarios) {
        this.alunos = alunos; this.professores = professores; this.series = series;
        this.turmas = turmas; this.disciplinas = disciplinas; this.vinculos = vinculos;
        this.notas = notas; this.presencas = presencas; this.ocorrencias = ocorrencias;
        this.horarios = horarios;
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> listAlunos(Long turmaId, String nome) {
        List<ApiAluno> result = turmaId != null ? alunos.findByTurmaId(turmaId)
            : nome != null && !nome.isBlank() ? alunos.findByNomeContainingIgnoreCase(nome.trim()) : alunos.findAll();
        if (turmaId != null && nome != null && !nome.isBlank()) {
            result = result.stream().filter(a -> a.getNome().toLowerCase().contains(nome.trim().toLowerCase())).toList();
        }
        return result.stream().sorted(Comparator.comparing(ApiAluno::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::aluno).toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponse getAluno(Long id) { return aluno(requireAluno(id)); }

    public AlunoResponse createAluno(AlunoRequest request) {
        ApiAluno entity = new ApiAluno();
        applyAluno(entity, request);
        return aluno(alunos.save(entity));
    }

    public void deleteAluno(Long id) {
        ApiAluno entity = requireAluno(id);
        notas.findByAlunoId(id).forEach(notas::delete);
        presencas.findByAlunoId(id).forEach(presencas::delete);
        ocorrencias.findByAlunoId(id).forEach(ocorrencias::delete);
        alunos.delete(entity);
    }

    public AlunoResponse updateAluno(Long id, AlunoRequest request) {
        ApiAluno entity = requireAluno(id);
        applyAluno(entity, request);
        return aluno(entity);
    }

    public AlunoResponse changeTurma(Long id, AlunoTurmaRequest request) {
        ApiAluno entity = requireAluno(id);
        entity.setTurma(request.turmaId() == null ? null : requireTurma(request.turmaId()));
        return aluno(entity);
    }

    @Transactional(readOnly = true)
    public List<ProfessorResponse> listProfessores() {
        return professores.findAll().stream().sorted(Comparator.comparing(ApiProfessor::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::professor).toList();
    }

    public ProfessorResponse createProfessor(ProfessorRequest request) {
        ApiProfessor entity = new ApiProfessor(); applyProfessor(entity, request);
        return professor(professores.save(entity));
    }

    public ProfessorResponse updateProfessor(Long id, ProfessorRequest request) {
        ApiProfessor entity = requireProfessor(id); applyProfessor(entity, request); return professor(entity);
    }

    public void deleteProfessor(Long id) {
        ApiProfessor entity = requireProfessor(id);
        horarios.findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(id).forEach(horarios::delete);
        vinculos.findByProfessorId(id).forEach(vinculos::delete);
        professores.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<SerieResponse> listSeries() {
        return series.findAll().stream().sorted(Comparator.comparing(ApiSerie::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::serie).toList();
    }

    public SerieResponse createSerie(SerieRequest request) {
        ApiSerie entity = new ApiSerie(); entity.setNome(request.nome().trim()); entity.setNivel(request.nivel().trim());
        return serie(series.save(entity));
    }

    public SerieResponse updateSerie(Long id, SerieRequest request) {
        ApiSerie entity = requireSerie(id);
        entity.setNome(request.nome().trim());
        entity.setNivel(request.nivel().trim());
        return serie(entity);
    }

    public void deleteSerie(Long id) {
        ApiSerie entity = requireSerie(id);
        series.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<TurmaResponse> listTurmas() {
        return turmas.findAll().stream().sorted(Comparator.comparing(ApiTurma::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::turma).toList();
    }

    public TurmaResponse createTurma(TurmaRequest request) {
        ApiTurma entity = new ApiTurma(); applyTurma(entity, request); return turma(turmas.save(entity));
    }

    public TurmaResponse updateTurma(Long id, TurmaRequest request) {
        ApiTurma entity = requireTurma(id); applyTurma(entity, request); return turma(entity);
    }

    public void deleteTurma(Long id) {
        ApiTurma entity = requireTurma(id);
        alunos.findByTurmaId(id).forEach(a -> { a.setTurma(null); alunos.save(a); });
        horarios.findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(id).forEach(horarios::delete);
        vinculos.findAll().stream().filter(v -> v.getTurma().getId().equals(id)).forEach(vinculos::delete);
        turmas.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<DisciplinaResponse> listDisciplinas() {
        return disciplinas.findAll().stream().sorted(Comparator.comparing(ApiDisciplina::getNome, String.CASE_INSENSITIVE_ORDER)).map(this::disciplina).toList();
    }

    public DisciplinaResponse createDisciplina(DisciplinaRequest request) {
        ApiDisciplina entity = new ApiDisciplina(); applyDisciplina(entity, request); return disciplina(disciplinas.save(entity));
    }

    public DisciplinaResponse updateDisciplina(Long id, DisciplinaRequest request) {
        ApiDisciplina entity = requireDisciplina(id); applyDisciplina(entity, request); return disciplina(entity);
    }

    public void deleteDisciplina(Long id) {
        ApiDisciplina entity = requireDisciplina(id);
        vinculos.findAll().stream().filter(v -> v.getDisciplina().getId().equals(id)).forEach(vinculos::delete);
        horarios.findAll().stream().filter(h -> h.getDisciplina().getId().equals(id)).forEach(horarios::delete);
        disciplinas.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<VinculoResponse> listVinculos(Long professorId) {
        List<ApiVinculo> result = professorId == null ? vinculos.findAll() : vinculos.findByProfessorId(professorId);
        return result.stream().map(this::vinculo).toList();
    }

    public VinculoResponse createVinculo(VinculoRequest request) {
        ApiVinculo entity = new ApiVinculo();
        entity.setProfessor(requireProfessor(request.professorId()));
        entity.setTurma(requireTurma(request.turmaId()));
        entity.setDisciplina(requireDisciplina(request.disciplinaId()));
        entity.setAnoLetivo(request.anoLetivo());
        return vinculo(vinculos.save(entity));
    }

    public void deleteVinculo(Long id) { vinculos.delete(requireVinculo(id)); }

    public ApiAluno requireAluno(Long id) { return alunos.findById(id).orElseThrow(() -> new NotFoundException("Aluno não encontrado.")); }
    public ApiProfessor requireProfessor(Long id) { return professores.findById(id).orElseThrow(() -> new NotFoundException("Professor não encontrado.")); }
    public ApiTurma requireTurma(Long id) { return turmas.findById(id).orElseThrow(() -> new NotFoundException("Turma não encontrada.")); }
    public ApiDisciplina requireDisciplina(Long id) { return disciplinas.findById(id).orElseThrow(() -> new NotFoundException("Disciplina não encontrada.")); }
    private ApiSerie requireSerie(Long id) { return series.findById(id).orElseThrow(() -> new NotFoundException("Série não encontrada.")); }
    private ApiVinculo requireVinculo(Long id) { return vinculos.findById(id).orElseThrow(() -> new NotFoundException("Vínculo não encontrado.")); }

    private void applyAluno(ApiAluno entity, AlunoRequest request) {
        entity.setNome(request.nome().trim());
        entity.setEmail(request.email().trim().toLowerCase());
        entity.setSenhaHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("aluno123"));
        entity.setTelefone("(11) 90000-0000");
        entity.setMatricula(request.matricula().trim());
        entity.setDataNascimento(request.dataNascimento());
        entity.setTurma(request.turmaId() == null ? null : requireTurma(request.turmaId()));
    }
    private void applyProfessor(ApiProfessor entity, ProfessorRequest request) {
        entity.setNome(request.nome().trim());
        entity.setEmail(request.email().trim().toLowerCase());
        entity.setSenhaHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("professor123"));
        entity.setTelefone("(11) 90000-0000");
        entity.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
    }
    private void applyTurma(ApiTurma entity, TurmaRequest request) {
        entity.setNome(request.nome().trim());
        entity.setSerie(requireSerie(request.serieId()));
        entity.setAnoLetivo(request.anoLetivo());
        if (entity.getTurno() == null || entity.getTurno().isBlank()) {
            entity.setTurno("MATUTINO");
        }
    }
    private void applyDisciplina(ApiDisciplina entity, DisciplinaRequest request) { entity.setNome(request.nome().trim()); entity.setCargaHoraria(request.cargaHoraria()); }

    public AlunoResponse aluno(ApiAluno a) {
        TurmaSummary turma = a.getTurma() == null ? null : turmaSummary(a.getTurma());
        return new AlunoResponse(a.getId(), a.getNome(), a.getEmail(), a.getMatricula(), a.getDataNascimento(),
            a.getTurma() == null ? null : a.getTurma().getId(), turma, turma == null ? null : turma.nome());
    }
    public AlunoSummary alunoSummary(ApiAluno a) { return new AlunoSummary(a.getId(), a.getNome(), a.getMatricula()); }
    public ProfessorResponse professor(ApiProfessor p) { return new ProfessorResponse(p.getId(), p.getNome(), p.getEmail()); }
    public ProfessorSummary professorSummary(ApiProfessor p) { return new ProfessorSummary(p.getId(), p.getNome()); }
    public SerieResponse serie(ApiSerie s) { return new SerieResponse(s.getId(), s.getNome(), s.getNivel()); }
    public SerieSummary serieSummary(ApiSerie s) { return new SerieSummary(s.getId(), s.getNome()); }
    public TurmaResponse turma(ApiTurma t) { return new TurmaResponse(t.getId(), t.getNome(), serieSummary(t.getSerie()), t.getSerie().getId(), t.getAnoLetivo()); }
    public TurmaSummary turmaSummary(ApiTurma t) { return new TurmaSummary(t.getId(), t.getNome(), t.getAnoLetivo()); }
    public DisciplinaResponse disciplina(ApiDisciplina d) { return new DisciplinaResponse(d.getId(), d.getNome(), d.getCargaHoraria()); }
    public DisciplinaSummary disciplinaSummary(ApiDisciplina d) { return new DisciplinaSummary(d.getId(), d.getNome()); }
    private VinculoResponse vinculo(ApiVinculo v) { return new VinculoResponse(v.getId(), v.getProfessor().getId(), professorSummary(v.getProfessor()), v.getTurma().getId(), turmaSummary(v.getTurma()), v.getDisciplina().getId(), disciplinaSummary(v.getDisciplina()), v.getAnoLetivo()); }
}
