package com.classhub.api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe utilitária que reúne todos os DTOs (Data Transfer Objects) usados
 * pela API do ClassHub para trocar dados com o frontend via JSON.
 * <p>
 * Cada DTO é implementado como um {@code record} imutável e os nomes dos
 * campos são propositalmente iguais aos nomes usados no JSON consumido pelo
 * frontend (mesma convenção de nomenclatura em ambos os lados), para evitar
 * a necessidade de mapeamentos adicionais.
 * <p>
 * Os DTOs "Request" representam os dados recebidos em requisições de
 * criação/atualização (com anotações de validação do Bean Validation) e os
 * DTOs "Response"/"Summary" representam os dados retornados pela API.
 */
public final class ApiDtos {
    /** Construtor privado: classe utilitária, não deve ser instanciada. */
    private ApiDtos() { }

    /** Resposta genérica contendo apenas uma mensagem textual (ex.: confirmações e erros simples). */
    public record MessageResponse(String mensagem) { }
    /** Dados enviados no login: e-mail e senha em texto puro. */
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) { }
    /** Resposta do login bem-sucedido: token JWT, perfil do usuário (papel/role) e nome de exibição. */
    public record LoginResponse(String token, String perfil, String nome) { }

    /** Dados para criar/atualizar uma série (ano/etapa escolar). */
    public record SerieRequest(@NotBlank String nome, @NotBlank String nivel) { }
    /** Representação completa de uma série retornada pela API. */
    public record SerieResponse(Long id, String nome, String nivel) { }
    /** Versão resumida de uma série, usada como referência em outras respostas. */
    public record SerieSummary(Long id, String nome) { }

    /** Dados para criar/atualizar uma turma. */
    public record TurmaRequest(@NotBlank String nome, @NotNull Long serieId, @NotNull @Min(2000) Integer anoLetivo) { }
    /** Representação completa de uma turma, incluindo a série associada. */
    public record TurmaResponse(Long id, String nome, SerieSummary serie, Long serieId, Integer anoLetivo) { }
    /** Versão resumida de uma turma, usada como referência em outras respostas. */
    public record TurmaSummary(Long id, String nome, Integer anoLetivo) { }

    /** Dados para criar/atualizar um aluno. */
    public record AlunoRequest(@NotBlank String nome, @NotBlank @Email String email,
                               @NotBlank String matricula, @NotNull LocalDate dataNascimento, Long turmaId) { }
    /** Dados usados para (re)associar um aluno a uma turma. */
    public record AlunoTurmaRequest(Long turmaId) { }
    /** Representação completa de um aluno, incluindo a turma em que está matriculado. */
    public record AlunoResponse(Long id, String nome, String email, String matricula, LocalDate dataNascimento,
                                Long turmaId, TurmaSummary turma, String turmaNome) { }
    /** Versão resumida de um aluno, usada como referência em outras respostas. */
    public record AlunoSummary(Long id, String nome, String matricula) { }

    /** Dados para criar/atualizar um professor. */
    public record ProfessorRequest(@NotBlank String nome, @NotBlank @Email String email) { }
    /** Representação completa de um professor. */
    public record ProfessorResponse(Long id, String nome, String email) { }
    /** Versão resumida de um professor, usada como referência em outras respostas. */
    public record ProfessorSummary(Long id, String nome) { }

    /** Dados para criar/atualizar uma disciplina. */
    public record DisciplinaRequest(@NotBlank String nome, @NotNull @Positive Integer cargaHoraria) { }
    /** Representação completa de uma disciplina. */
    public record DisciplinaResponse(Long id, String nome, Integer cargaHoraria) { }
    /** Versão resumida de uma disciplina, usada como referência em outras respostas. */
    public record DisciplinaSummary(Long id, String nome) { }

    /** Dados para vincular um professor a uma disciplina dentro de uma turma, em um ano letivo. */
    public record VinculoRequest(@NotNull Long professorId, @NotNull Long turmaId, @NotNull Long disciplinaId,
                                 @NotNull @Min(2000) Integer anoLetivo) { }
    /** Representação completa de um vínculo professor/turma/disciplina/ano letivo. */
    public record VinculoResponse(Long id, Long professorId, ProfessorSummary professor, Long turmaId,
                                  TurmaSummary turma, Long disciplinaId, DisciplinaSummary disciplina, Integer anoLetivo) { }

    /** Dados para criar/atualizar um horário de aula (dia da semana e faixa de horário) de uma turma. */
    public record HorarioRequest(@NotNull Long turmaId, @NotNull @Min(1) @Max(5) Integer diaSemana,
                                 @NotBlank String horaInicio, @NotBlank String horaFim,
                                 @NotNull Long disciplinaId, @NotNull Long professorId) { }
    /** Representação completa de um horário de aula, com os dados resumidos de turma, disciplina e professor. */
    public record HorarioResponse(Long id, Long turmaId, TurmaSummary turma, String turmaNome,
                                  Long disciplinaId, DisciplinaSummary disciplina, String disciplinaNome,
                                  Long professorId, ProfessorSummary professor, String professorNome,
                                  Integer diaSemana, String horaInicio, String horaFim) { }

    /** Dados de registro de presença/falta de um aluno em uma data específica. */
    public record PresencaRequest(@NotNull Long alunoId, @NotNull Long turmaId, @NotNull Long disciplinaId,
                                  @NotNull LocalDate data, @NotBlank String status, String justificativa) { }
    /** Representação completa de um registro de presença/falta. */
    public record PresencaResponse(Long id, Long alunoId, Long turmaId, Long disciplinaId, String disciplinaNome,
                                   LocalDate data, String status, String justificativa) { }

    /** Dados de lançamento de uma nota (avaliação) de um aluno em uma disciplina/bimestre. */
    public record NotaRequest(@NotNull Long alunoId, @NotNull Long disciplinaId, @NotNull Long turmaId,
                              @NotNull @Min(1) @Max(4) Integer bimestre, @NotBlank String tipo,
                              @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double valor,
                              @NotNull @Positive Double peso) { }
    /** Representação completa de uma nota lançada. */
    public record NotaResponse(Long id, Long alunoId, Long disciplinaId, String disciplinaNome, Integer bimestre,
                               String tipo, Double peso, Double valor) { }

    /** Dados para registrar uma ocorrência disciplinar/administrativa de um aluno. */
    public record OcorrenciaRequest(@NotNull Long alunoId, @NotBlank String tipo, @NotBlank String descricao) { }
    /** Representação completa de uma ocorrência registrada. */
    public record OcorrenciaResponse(Long id, Long alunoId, AlunoSummary aluno, String tipo, String descricao, String status, LocalDate data) { }

    /** Dados para registrar um item encontrado no setor de achados e perdidos. */
    public record AchadoPerdidoRequest(@NotBlank String descricao, @NotBlank String categoria,
                                       @NotNull LocalDate data, @NotBlank String localEncontrado) { }
    /** Representação completa de um item de achados e perdidos. */
    public record AchadoPerdidoResponse(Long id, String descricao, String categoria, String localEncontrado,
                                        LocalDate data, String status) { }

    /** Dados para criar/atualizar um evento no calendário escolar. */
    public record CalendarioRequest(@NotNull LocalDate data, @NotNull @Min(2000) Integer anoLetivo,
                                    @NotBlank String tipo, @NotBlank String titulo, String descricao) { }
    /** Representação completa de um evento do calendário escolar. */
    public record CalendarioResponse(Long id, LocalDate data, Integer anoLetivo, String tipo, String titulo, String descricao) { }

    /** Parâmetros usados para solicitar a geração do boletim de um aluno/turma em um bimestre. */
    public record GerarBoletimRequest(@NotNull @Min(2000) Integer anoLetivo, @NotNull @Min(1) @Max(4) Integer bimestre,
                                      Long turmaId) { }
    /** Item do boletim referente a uma única disciplina: média, frequência e situação final. */
    public record BoletimItemResponse(Long disciplinaId, String disciplinaNome, Double media, Integer frequencia, String situacao) { }
    /** Boletim completo de um aluno, composto pela lista de itens (um por disciplina). */
    public record BoletimResponse(List<BoletimItemResponse> disciplinas) { }
}
