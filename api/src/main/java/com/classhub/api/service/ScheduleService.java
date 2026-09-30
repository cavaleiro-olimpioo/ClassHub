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

/**
 * Serviço com a lógica de negócio da grade de horários das turmas e dos
 * registros de presença/falta dos alunos, incluindo a validação de
 * conflitos de horário entre turma e professor.
 */
@Service
@Transactional
public class ScheduleService {
    /** Formato usado para converter horários em texto (ex.: "08:00") e vice-versa. */
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    /** Valores válidos para o status de um registro de presença. */
    private static final Set<String> PRESENCE_STATUS = Set.of("PRESENTE", "FALTA", "FALTA_JUSTIFICADA");
    private final ApiHorarioRepository horarios;
    private final ApiPresencaRepository presencas;
    private final DirectoryService directory;

    /**
     * Cria o serviço de horários e presenças.
     *
     * @param horarios repositório de horários de aula
     * @param presencas repositório de registros de presença/falta
     * @param directory serviço de diretório, usado para buscar turmas, professores, alunos e disciplinas
     */
    public ScheduleService(ApiHorarioRepository horarios, ApiPresencaRepository presencas, DirectoryService directory) {
        this.horarios = horarios; this.presencas = presencas; this.directory = directory;
    }

    /**
     * Lista os horários de aula de uma turma, ordenados por dia da semana e horário de início.
     *
     * @param turmaId identificador da turma
     * @return lista de horários da turma convertidos em DTO de resposta
     * @throws NotFoundException se a turma não existir
     */
    @Transactional(readOnly = true)
    public List<HorarioResponse> horariosTurma(Long turmaId) {
        directory.requireTurma(turmaId);
        return horarios.findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(turmaId).stream().map(this::horario).toList();
    }

    /**
     * Lista os horários de aula de um professor, ordenados por dia da semana e horário de início.
     *
     * @param professorId identificador do professor
     * @return lista de horários do professor convertidos em DTO de resposta
     * @throws NotFoundException se o professor não existir
     */
    @Transactional(readOnly = true)
    public List<HorarioResponse> horariosProfessor(Long professorId) {
        directory.requireProfessor(professorId);
        return horarios.findByProfessorIdOrderByDiaSemanaAscHoraInicioAsc(professorId).stream().map(this::horario).toList();
    }

    /**
     * Cria um novo horário de aula, validando o formato dos horários
     * informados e garantindo que não haja conflito de horário (mesma
     * turma ou mesmo professor já com aula no período) no mesmo dia da semana.
     *
     * @param request dados do horário a ser criado
     * @return o horário recém-criado
     * @throws BadRequestException se o horário de início não for anterior ao de fim, ou se o formato de hora for inválido
     * @throws NotFoundException se a turma, o professor ou a disciplina não existirem
     * @throws ConflictException se a turma ou o professor já tiverem aula no período informado
     */
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

    /**
     * Exclui um horário de aula.
     *
     * @param id identificador do horário a ser excluído
     * @throws NotFoundException se o horário não existir
     */
    public void deleteHorario(Long id) {
        ApiHorario horario = horarios.findById(id).orElseThrow(() -> new NotFoundException("Horário não encontrado."));
        horarios.delete(horario);
    }

    /**
     * Lista registros de presença/falta, com filtros opcionais por aluno,
     * turma, disciplina e data, ordenados da data mais recente para a mais antiga.
     *
     * @param alunoId identificador do aluno (opcional, tem prioridade sobre os demais filtros)
     * @param turmaId identificador da turma (opcional)
     * @param disciplinaId identificador da disciplina (opcional, usado junto com turma e data)
     * @param data data da aula (opcional, usado junto com turma e disciplina)
     * @return lista de registros de presença que atendem aos filtros informados
     */
    @Transactional(readOnly = true)
    public List<PresencaResponse> listPresencas(Long alunoId, Long turmaId, Long disciplinaId, LocalDate data) {
        List<ApiPresenca> result;
        if (alunoId != null) result = presencas.findByAlunoId(alunoId);
        else if (turmaId != null && disciplinaId != null && data != null) result = presencas.findByTurmaIdAndDisciplinaIdAndData(turmaId, disciplinaId, data);
        else if (turmaId != null) result = presencas.findByTurmaId(turmaId);
        else result = presencas.findAll();
        return result.stream().sorted(Comparator.comparing(ApiPresenca::getData).reversed()).map(this::presenca).toList();
    }

    /**
     * Salva em lote uma lista de registros de presença/falta (ex.: chamada
     * de uma turma inteira em um dia). Cada registro é criado ou
     * atualizado (upsert) conforme já exista ou não um registro para o
     * mesmo aluno/turma/disciplina/data.
     *
     * @param requests lista de registros de presença a serem salvos
     * @return lista de registros de presença salvos, na mesma ordem da entrada
     * @throws BadRequestException se a lista estiver vazia ou nula, ou se algum status for inválido
     */
    public List<PresencaResponse> savePresencas(List<PresencaRequest> requests) {
        if (requests == null || requests.isEmpty()) throw new BadRequestException("Informe ao menos uma presença.");
        return requests.stream().map(this::savePresenca).toList();
    }

    /**
     * Cria ou atualiza (upsert) um único registro de presença/falta.
     *
     * @param request dados do registro de presença
     * @return o registro salvo, convertido em DTO de resposta
     * @throws BadRequestException se o status informado não for válido
     * @throws NotFoundException se o aluno, a turma ou a disciplina não existirem
     */
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

    /**
     * Verifica se o intervalo [inicio, fim) informado se sobrepõe a algum
     * dos horários existentes.
     *
     * @param existing horários já cadastrados para o mesmo dia da semana (mesma turma ou mesmo professor)
     * @param inicio horário de início do novo horário
     * @param fim horário de fim do novo horário
     * @return {@code true} se houver sobreposição de horário
     */
    private boolean hasConflict(List<ApiHorario> existing, LocalTime inicio, LocalTime fim) {
        return existing.stream().anyMatch(item -> item.getHoraInicio().isBefore(fim) && inicio.isBefore(item.getHoraFim()));
    }
    /**
     * Converte um texto no formato "HH:mm" para {@link LocalTime}.
     *
     * @param value texto do horário
     * @return horário convertido
     * @throws BadRequestException se o texto não estiver no formato esperado
     */
    private LocalTime parseTime(String value) {
        try { return LocalTime.parse(value, TIME); }
        catch (DateTimeParseException ex) { throw new BadRequestException("Horário deve usar o formato HH:mm."); }
    }
    /** Converte uma entidade {@link ApiHorario} para o DTO {@link HorarioResponse}. */
    private HorarioResponse horario(ApiHorario h) {
        return new HorarioResponse(h.getId(), h.getTurma().getId(), directory.turmaSummary(h.getTurma()), h.getTurma().getNome(),
            h.getDisciplina().getId(), directory.disciplinaSummary(h.getDisciplina()), h.getDisciplina().getNome(),
            h.getProfessor().getId(), directory.professorSummary(h.getProfessor()), h.getProfessor().getNome(),
            h.getDiaSemana(), TIME.format(h.getHoraInicio()), TIME.format(h.getHoraFim()));
    }
    /** Converte uma entidade {@link ApiPresenca} para o DTO {@link PresencaResponse}. */
    private PresencaResponse presenca(ApiPresenca p) {
        return new PresencaResponse(p.getId(), p.getAluno().getId(), p.getTurma().getId(), p.getDisciplina().getId(),
            p.getDisciplina().getNome(), p.getData(), p.getStatus(), p.getJustificativa());
    }
}
