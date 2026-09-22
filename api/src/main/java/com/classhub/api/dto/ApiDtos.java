package com.classhub.api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/** DTOs deliberately mirror the JSON field names consumed by the frontend. */
public final class ApiDtos {
    private ApiDtos() { }

    public record MessageResponse(String mensagem) { }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) { }
    public record LoginResponse(String token, String perfil, String nome, Long vinculoId) { }

    public record SerieRequest(@NotBlank String nome, @NotBlank String nivel) { }
    public record SerieResponse(Long id, String nome, String nivel) { }
    public record SerieSummary(Long id, String nome) { }

    public record TurmaRequest(@NotBlank String nome, @NotNull Long serieId, @NotNull @Min(2000) Integer anoLetivo) { }
    public record TurmaResponse(Long id, String nome, SerieSummary serie, Long serieId, Integer anoLetivo) { }
    public record TurmaSummary(Long id, String nome, Integer anoLetivo) { }

    public record AlunoRequest(@NotBlank String nome, @NotBlank @Email String email,
                               @NotBlank String matricula, @NotNull LocalDate dataNascimento, Long turmaId) { }
    public record AlunoTurmaRequest(Long turmaId) { }
    public record AlunoResponse(Long id, String nome, String email, String matricula, LocalDate dataNascimento,
                                Long turmaId, TurmaSummary turma, String turmaNome) { }
    public record AlunoSummary(Long id, String nome, String matricula) { }

    public record ProfessorRequest(@NotBlank String nome, @NotBlank @Email String email) { }
    public record ProfessorResponse(Long id, String nome, String email) { }
    public record ProfessorSummary(Long id, String nome) { }

    public record DisciplinaRequest(@NotBlank String nome, @NotNull @Positive Integer cargaHoraria) { }
    public record DisciplinaResponse(Long id, String nome, Integer cargaHoraria) { }
    public record DisciplinaSummary(Long id, String nome) { }

    public record VinculoRequest(@NotNull Long professorId, @NotNull Long turmaId, @NotNull Long disciplinaId,
                                 @NotNull @Min(2000) Integer anoLetivo) { }
    public record VinculoResponse(Long id, Long professorId, ProfessorSummary professor, Long turmaId,
                                  TurmaSummary turma, Long disciplinaId, DisciplinaSummary disciplina, Integer anoLetivo) { }

    public record HorarioRequest(@NotNull Long turmaId, @NotNull @Min(1) @Max(5) Integer diaSemana,
                                 @NotBlank String horaInicio, @NotBlank String horaFim,
                                 @NotNull Long disciplinaId, @NotNull Long professorId) { }
    public record HorarioResponse(Long id, Long turmaId, TurmaSummary turma, String turmaNome,
                                  Long disciplinaId, DisciplinaSummary disciplina, String disciplinaNome,
                                  Long professorId, ProfessorSummary professor, String professorNome,
                                  Integer diaSemana, String horaInicio, String horaFim) { }

    public record PresencaRequest(@NotNull Long alunoId, @NotNull Long turmaId, @NotNull Long disciplinaId,
                                  @NotNull LocalDate data, @NotBlank String status, String justificativa) { }
    public record PresencaResponse(Long id, Long alunoId, Long turmaId, Long disciplinaId, String disciplinaNome,
                                   LocalDate data, String status, String justificativa) { }

    public record NotaRequest(@NotNull Long alunoId, @NotNull Long disciplinaId, @NotNull Long turmaId,
                              @NotNull @Min(1) @Max(4) Integer bimestre, @NotBlank String tipo,
                              @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double valor,
                              @NotNull @Positive Double peso) { }
    public record NotaResponse(Long id, Long alunoId, Long disciplinaId, String disciplinaNome, Integer bimestre,
                               String tipo, Double peso, Double valor) { }

    public record OcorrenciaRequest(@NotNull Long alunoId, @NotBlank String tipo, @NotBlank String descricao) { }
    public record OcorrenciaResponse(Long id, Long alunoId, AlunoSummary aluno, String tipo, String descricao, String status) { }

    public record AchadoPerdidoRequest(@NotBlank String descricao, @NotBlank String categoria,
                                       @NotNull LocalDate data, @NotBlank String localEncontrado) { }
    public record AchadoPerdidoResponse(Long id, String descricao, String categoria, String localEncontrado,
                                        LocalDate data, String status) { }

    public record CalendarioRequest(@NotNull LocalDate data, @NotNull @Min(2000) Integer anoLetivo,
                                    @NotBlank String tipo, @NotBlank String titulo, String descricao) { }
    public record CalendarioResponse(Long id, LocalDate data, Integer anoLetivo, String tipo, String titulo, String descricao) { }

    public record GerarBoletimRequest(@NotNull @Min(2000) Integer anoLetivo, @NotNull @Min(1) @Max(4) Integer bimestre,
                                      Long turmaId) { }
    public record BoletimItemResponse(Long disciplinaId, String disciplinaNome, Double media, Integer frequencia, String situacao) { }
    public record BoletimResponse(List<BoletimItemResponse> disciplinas) { }
}
