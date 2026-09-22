package com.classhub.api.service;

import com.classhub.api.domain.*;
import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.BadRequestException;
import com.classhub.api.exception.ApiExceptions.ConflictException;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import com.classhub.api.repository.ApiHorarioRepository;
import com.classhub.api.repository.ApiPresencaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ScheduleService {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final Set<String> PRESENCE_STATUS = Set.of("PRESENTE", "FALTA", "FALTA_JUSTIFICADA");
    private final ApiHorarioRepository horarios;
    private final ApiPresencaRepository presencas;
    private final DirectoryService directory;

    public ScheduleService(ApiHorarioRepository horarios, ApiPresencaRepository presencas, DirectoryService directory) {
        this.horarios = horarios; this.presencas = presencas; this.directory = directory;
    }

    @Transactional(readOnly = true)
    public List<HorarioResponse> horariosTurma(Long turmaId) {
        directory.requireTurma(turmaId);
        return horarios.findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(turmaId).stream().map(this::horario).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioResponse> horariosProfessor(Long professorId) {
        directory.requireProfessor(professorId);
        return horarios.findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(professorId).stream().map(this::horario).toList();
    }

    public HorarioResponse createHorario(HorarioRequest request) {
        LocalTime inicio = parseTime(request.horaInicio());
        LocalTime fim = parseTime(request.horaFim());
        if (!inicio.isBefore(fim)) throw new BadRequestException("A hora de início deve ser anterior à hora de fim.");
        ApiTurma turma = directory.requireTurma(request.turmaId());
        ApiProfessor professor = directory.requireProfessor(request.professorId());
        if (hasConflict(horarios.findByTurmaIdAndDiaSemana(turma.getId(), request.diaSemana()), inicio, fim)
                || hasConflict(horarios.findByProfessorIdAndDiaSemana(professor.getId(), request.diaSemana()), inicio, fim)) {
            throw new ConflictException("Conflito de horário: a turma ou o professor já possui aula nesse período.");
        }
        ApiHorario entity = new ApiHorario();
        entity.setTurma(turma); entity.setProfessor(professor); entity.setDisciplina(directory.requireDisciplina(request.disciplinaId()));
        entity.setDiaSemana(request.diaSemana()); entity.setHoraInicio(inicio); entity.setHoraFim(fim);
        return horario(horarios.save(entity));
    }

    public void deleteHorario(Long id) {
        ApiHorario horario = horarios.findById(id).orElseThrow(() -> new NotFoundException("Horário não encontrado."));
        horarios.delete(horario);
    }

    @Transactional(readOnly = true)
    public List<PresencaResponse> listPresencas(Long alunoId, Long turmaId, Long disciplinaId, LocalDate data) {
        List<ApiPresenca> result;
        if (alunoId != null) result = presencas.findByAlunoId(alunoId);
        else if (turmaId != null && disciplinaId != null && data != null) result = presencas.findByTurmaIdAndDisciplinaIdAndData(turmaId, disciplinaId, data);
        else if (turmaId != null) result = presencas.findByTurmaId(turmaId);
        else result = presencas.findAll();
        return result.stream().sorted(Comparator.comparing(ApiPresenca::getData).reversed()).map(this::presenca).toList();
    }

    public List<PresencaResponse> savePresencas(List<PresencaRequest> requests) {
        if (requests == null || requests.isEmpty()) throw new BadRequestException("Informe ao menos uma presença.");
        return requests.stream().map(this::savePresenca).toList();
    }

    private PresencaResponse savePresenca(PresencaRequest request) {
        if (!PRESENCE_STATUS.contains(request.status())) throw new BadRequestException("Status de presença inválido.");
        ApiAluno aluno = directory.requireAluno(request.alunoId());
        ApiTurma turma = directory.requireTurma(request.turmaId());
        ApiDisciplina disciplina = directory.requireDisciplina(request.disciplinaId());
        ApiPresenca entity = presencas.findByAlunoIdAndTurmaIdAndDisciplinaIdAndData(aluno.getId(), turma.getId(), disciplina.getId(), request.data()).orElseGet(ApiPresenca::new);
        entity.setAluno(aluno); entity.setTurma(turma); entity.setDisciplina(disciplina); entity.setData(request.data());
        entity.setStatus(request.status());
        entity.setJustificativa("FALTA_JUSTIFICADA".equals(request.status()) ? request.justificativa() : null);
        return presenca(presencas.save(entity));
    }

    private boolean hasConflict(List<ApiHorario> existing, LocalTime inicio, LocalTime fim) {
        return existing.stream().anyMatch(item -> item.getHoraInicio().isBefore(fim) && inicio.isBefore(item.getHoraFim()));
    }
    private LocalTime parseTime(String value) {
        try { return LocalTime.parse(value, TIME); }
        catch (DateTimeParseException ex) { throw new BadRequestException("Horário deve usar o formato HH:mm."); }
    }
    private HorarioResponse horario(ApiHorario h) {
        return new HorarioResponse(h.getId(), h.getTurma().getId(), directory.turmaSummary(h.getTurma()), h.getTurma().getNome(),
            h.getDisciplina().getId(), directory.disciplinaSummary(h.getDisciplina()), h.getDisciplina().getNome(),
            h.getProfessor().getId(), directory.professorSummary(h.getProfessor()), h.getProfessor().getNome(),
            h.getDiaSemana(), TIME.format(h.getHoraInicio()), TIME.format(h.getHoraFim()));
    }
    private PresencaResponse presenca(ApiPresenca p) {
        return new PresencaResponse(p.getId(), p.getAluno().getId(), p.getTurma().getId(), p.getDisciplina().getId(),
            p.getDisciplina().getNome(), p.getData(), p.getStatus(), p.getJustificativa());
    }
}
