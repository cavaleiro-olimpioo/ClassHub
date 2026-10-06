package com.classhub.api.service;

import com.classhub.api.domain.*;
import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.BusinessRuleException;
import com.classhub.api.exception.ApiExceptions.BadRequestException;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import com.classhub.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço com a lógica de negócio de notas e boletins: lançamento de notas,
 * fechamento de bimestre e cálculo/geração de boletins (incluindo a
 * exportação em PDF).
 */
@Service
@Transactional
public class GradeService {
    private final ApiNotaRepository notas;
    private final ApiPresencaRepository presencas;
    private final ApiFechamentoBoletimRepository fechamentos;
    private final DirectoryService directory;

    /**
     * Cria o serviço de notas e boletins.
     *
     * @param notas repositório de notas
     * @param presencas repositório de presenças, usado no cálculo de frequência do boletim
     * @param fechamentos repositório de fechamentos de bimestre
     * @param directory serviço de diretório, usado para buscar alunos, turmas e disciplinas
     */
    public GradeService(ApiNotaRepository notas, ApiPresencaRepository presencas, ApiFechamentoBoletimRepository fechamentos,
                        DirectoryService directory) {
        this.notas = notas; this.presencas = presencas; this.fechamentos = fechamentos; this.directory = directory;
    }

    /**
     * Lista todas as notas de um aluno, ordenadas por bimestre e depois por
     * nome da disciplina.
     *
     * @param alunoId identificador do aluno
     * @return lista de notas do aluno convertidas em DTO de resposta
     * @throws NotFoundException se o aluno não existir
     */
    @Transactional(readOnly = true)
    public List<NotaResponse> notasAluno(Long alunoId) {
        directory.requireAluno(alunoId);
        return notas.findByAlunoId(alunoId).stream().sorted(Comparator.comparing(ApiNota::getBimestre).thenComparing(n -> n.getDisciplina().getNome())).map(this::nota).toList();
    }

    /**
     * Lista notas com filtros opcionais por turma, disciplina e bimestre.
     *
     * @param turmaId identificador da turma (opcional)
     * @param disciplinaId identificador da disciplina (opcional)
     * @param bimestre número do bimestre (opcional)
     * @return lista de notas que atendem aos filtros informados
     */
    @Transactional(readOnly = true)
    public List<NotaResponse> listNotas(Long turmaId, Long disciplinaId, Integer bimestre) {
        List<ApiNota> result = turmaId != null && disciplinaId != null && bimestre != null
            ? notas.findByTurmaIdAndDisciplinaIdAndBimestre(turmaId, disciplinaId, bimestre) : notas.findAll();
        return result.stream()
            .filter(n -> turmaId == null || n.getTurma().getId().equals(turmaId))
            .filter(n -> disciplinaId == null || n.getDisciplina().getId().equals(disciplinaId))
            .filter(n -> bimestre == null || n.getBimestre().equals(bimestre))
            .map(this::nota).toList();
    }

    /**
     * Lança uma nova nota para um aluno.
     *
     * @param request dados da nota a ser lançada
     * @return a nota recém-criada
     * @throws NotFoundException se o aluno, a turma ou a disciplina não existirem
     */
    public NotaResponse createNota(NotaRequest request) {
        ApiNota entity = new ApiNota(); apply(entity, request); return nota(notas.save(entity));
    }

    /**
     * Atualiza uma nota já lançada.
     *
     * @param id identificador da nota
     * @param request novos dados da nota
     * @return a nota atualizada
     * @throws NotFoundException se a nota não existir
     */
    public NotaResponse updateNota(Long id, NotaRequest request) {
        ApiNota entity = notas.findById(id).orElseThrow(() -> new NotFoundException("Nota não encontrada."));
        apply(entity, request); return nota(entity);
    }

    /**
     * Exclui uma nota lançada.
     *
     * @param id identificador da nota a ser excluída
     * @throws NotFoundException se a nota não existir
     */
    public void deleteNota(Long id) {
        ApiNota entity = notas.findById(id).orElseThrow(() -> new NotFoundException("Nota não encontrada."));
        assertBimestreAberto(entity.getTurma().getId(), entity.getBimestre());
        notas.delete(entity);
    }

    public boolean professorHasAccessToNota(Long notaId, Long professorId) {
        ApiNota nota = notas.findById(notaId).orElseThrow(() -> new NotFoundException("Nota não encontrada."));
        return directory.listVinculos(professorId).stream().anyMatch(v -> v.turmaId().equals(nota.getTurma().getId()) && v.disciplinaId().equals(nota.getDisciplina().getId()));
    }

    /**
     * Fecha o bimestre informado para uma turma específica (ou para todas
     * as turmas, se {@code turmaId} não for informado), registrando o
     * fechamento caso ainda não exista. Boletins só podem ser consultados
     * após o fechamento do bimestre correspondente.
     *
     * @param request ano letivo, bimestre e, opcionalmente, a turma a ser fechada
     * @return mensagem de confirmação do fechamento
     * @throws NotFoundException se a turma informada não existir
     */
    public MessageResponse fecharBimestre(GerarBoletimRequest request) {
        if (request.dataInicio().isAfter(request.dataFim()) || request.dataInicio().getYear() != request.anoLetivo() || request.dataFim().getYear() != request.anoLetivo()) {
            throw new BadRequestException("O intervalo do bimestre deve estar dentro do ano letivo informado.");
        }
        ApiTurma turma = request.turmaId() == null ? null : directory.requireTurma(request.turmaId());
        ApiFechamentoBoletim fechamento = (turma == null
            ? fechamentos.findByAnoLetivoAndBimestreAndTurmaIsNull(request.anoLetivo(), request.bimestre())
            : fechamentos.findByAnoLetivoAndBimestreAndTurmaId(request.anoLetivo(), request.bimestre(), turma.getId()))
            .orElseGet(ApiFechamentoBoletim::new);
        fechamento.setAnoLetivo(request.anoLetivo());
        fechamento.setBimestre(request.bimestre());
        fechamento.setTurma(turma);
        fechamento.setDataInicio(request.dataInicio());
        fechamento.setDataFim(request.dataFim());
        fechamentos.save(fechamento);
        return new MessageResponse("Boletins gerados para o " + request.bimestre() + "º bimestre.");
    }

    /**
     * Calcula e monta o boletim de um aluno para um bimestre específico:
     * agrupa as notas por disciplina, calcula a média ponderada pelo peso
     * de cada avaliação, calcula o percentual de frequência a partir dos
     * registros de presença e define a situação final ("APROVADO" se
     * média &gt;= 6.0 e frequência &gt;= 75%, senão "EM_RECUPERACAO").
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return boletim com um item por disciplina cursada
     * @throws NotFoundException se o aluno não existir
     * @throws BusinessRuleException se o bimestre ainda não tiver sido fechado
     */
    @Transactional(readOnly = true)
    public BoletimResponse boletim(Long alunoId, int bimestre) {
        ApiAluno aluno = directory.requireAluno(alunoId);
        int ano = aluno.getTurma() == null ? LocalDate.now().getYear() : aluno.getTurma().getAnoLetivo();
        Long turmaId = aluno.getTurma() == null ? null : aluno.getTurma().getId();
        ApiFechamentoBoletim fechamento = turmaId == null ? null : fechamentos.findByAnoLetivoAndBimestreAndTurmaId(ano, bimestre, turmaId).orElse(null);
        if (fechamento == null) fechamento = fechamentos.findByAnoLetivoAndBimestreAndTurmaIsNull(ano, bimestre).orElse(null);
        if (fechamento == null) throw new BusinessRuleException("Bimestre ainda não fechado.");
        if (fechamento.getDataInicio() == null || fechamento.getDataFim() == null) throw new BusinessRuleException("O fechamento precisa ser refeito com o intervalo de datas do bimestre.");
        final ApiFechamentoBoletim periodo = fechamento;

        Map<Long, List<ApiNota>> porDisciplina = notas.findByAlunoIdAndBimestre(alunoId, bimestre).stream()
            .collect(Collectors.groupingBy(n -> n.getDisciplina().getId()));
        List<BoletimItemResponse> items = porDisciplina.values().stream().map(group -> {
            ApiDisciplina disciplina = group.get(0).getDisciplina();
            double peso = group.stream().mapToDouble(ApiNota::getPeso).sum();
            double media = peso == 0 ? 0 : group.stream().mapToDouble(n -> n.getValor() * n.getPeso()).sum() / peso;
            List<ApiPresenca> frequencias = presencas.findByAlunoId(alunoId).stream()
                .filter(p -> p.getDisciplina().getId().equals(disciplina.getId()))
                .filter(p -> turmaId != null && p.getTurma().getId().equals(turmaId))
                .filter(p -> !p.getData().isBefore(periodo.getDataInicio()) && !p.getData().isAfter(periodo.getDataFim()))
                .toList();
            int frequencia = frequencias.isEmpty() ? 0 : (int) Math.round(100d * frequencias.stream().filter(p -> !"FALTA".equals(p.getStatus())).count() / frequencias.size());
            String situacao = media >= 6.0 && frequencia >= 75 ? "APROVADO" : "EM_RECUPERACAO";
            return new BoletimItemResponse(disciplina.getId(), disciplina.getNome(), round(media), frequencia, situacao);
        }).sorted(Comparator.comparing(BoletimItemResponse::disciplinaNome, String.CASE_INSENSITIVE_ORDER)).toList();
        return new BoletimResponse(items);
    }

    /**
     * Gera o boletim de um aluno (ver {@link #boletim}) e o converte em um
     * arquivo PDF simples, pronto para download.
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return os bytes do arquivo PDF gerado
     * @throws NotFoundException se o aluno não existir
     * @throws BusinessRuleException se o bimestre ainda não tiver sido fechado
     */
    @Transactional(readOnly = true)
    public byte[] boletimPdf(Long alunoId, int bimestre) {
        BoletimResponse boletim = boletim(alunoId, bimestre);
        String lines = "Boletim - " + bimestre + "o Bimestre\n" + boletim.disciplinas().stream()
            .map(item -> item.disciplinaNome() + " - Media: " + item.media() + " - " + item.situacao()).collect(Collectors.joining("\n"));
        return minimalPdf(lines).getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * Aplica os dados de um {@link NotaRequest} sobre a entidade,
     * resolvendo aluno, disciplina e turma associados.
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     */
    private void apply(ApiNota entity, NotaRequest request) {
        assertBimestreAberto(request.turmaId(), request.bimestre());
        entity.setAluno(directory.requireAluno(request.alunoId())); entity.setDisciplina(directory.requireDisciplina(request.disciplinaId()));
        entity.setTurma(directory.requireTurma(request.turmaId())); entity.setBimestre(request.bimestre());
        entity.setTipo(request.tipo().trim().toUpperCase()); entity.setValor(request.valor()); entity.setPeso(request.peso());
    }
    /** Converte uma entidade {@link ApiNota} para o DTO {@link NotaResponse}. */
    private NotaResponse nota(ApiNota n) { return new NotaResponse(n.getId(), n.getAluno().getId(), n.getDisciplina().getId(), n.getDisciplina().getNome(), n.getBimestre(), n.getTipo(), n.getPeso(), n.getValor()); }
    private void assertBimestreAberto(Long turmaId, int bimestre) {
        Integer anoLetivo = directory.requireTurma(turmaId).getAnoLetivo();
        boolean fechado = fechamentos.findByAnoLetivoAndBimestreAndTurmaIsNull(anoLetivo, bimestre).isPresent()
            || fechamentos.findByAnoLetivoAndBimestreAndTurmaId(anoLetivo, bimestre, turmaId).isPresent();
        if (fechado) throw new BusinessRuleException("O bimestre está fechado; não é possível alterar as notas.");
    }
    /**
     * Arredonda um valor para duas casas decimais.
     *
     * @param value valor a ser arredondado
     * @return valor arredondado com duas casas decimais
     */
    private double round(double value) { return Math.round(value * 100d) / 100d; }

    /**
     * Monta manualmente (sem bibliotecas externas) um arquivo PDF mínimo
     * de uma única página, contendo o texto informado.
     *
     * @param text texto (com quebras de linha "\n") a ser exibido no PDF
     * @return conteúdo textual do arquivo PDF gerado
     */
    private String minimalPdf(String text) {
        String escaped = text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        String content = "BT /F1 12 Tf 50 750 Td " + escaped.replace("\n", ") Tj 0 -18 Td (") + " Tj ET";
        List<String> objects = List.of(
            "<< /Type /Catalog /Pages 2 0 R >>",
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>",
            "<< /Length " + content.length() + " >>\nstream\n" + content + "\nendstream",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n"); List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) { offsets.add(pdf.length()); pdf.append(i + 1).append(" 0 obj\n").append(objects.get(i)).append("\nendobj\n"); }
        int xref = pdf.length(); pdf.append("xref\n0 ").append(objects.size() + 1).append("\n0000000000 65535 f \n");
        for (int offset : offsets) pdf.append(String.format("%010d 00000 n \n", offset));
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF");
        return pdf.toString();
    }
}
