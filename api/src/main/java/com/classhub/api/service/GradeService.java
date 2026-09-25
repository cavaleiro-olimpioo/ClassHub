package com.classhub.api.service;

import com.classhub.api.domain.*;
import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.BusinessRuleException;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import com.classhub.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeService {
    private final ApiNotaRepository notas;
    private final ApiPresencaRepository presencas;
    private final ApiFechamentoBoletimRepository fechamentos;
    private final DirectoryService directory;

    public GradeService(ApiNotaRepository notas, ApiPresencaRepository presencas, ApiFechamentoBoletimRepository fechamentos,
                        DirectoryService directory) {
        this.notas = notas; this.presencas = presencas; this.fechamentos = fechamentos; this.directory = directory;
    }

    @Transactional(readOnly = true)
    public List<NotaResponse> notasAluno(Long alunoId) {
        directory.requireAluno(alunoId);
        return notas.findByAlunoId(alunoId).stream().sorted(Comparator.comparing(ApiNota::getBimestre).thenComparing(n -> n.getDisciplina().getNome())).map(this::nota).toList();
    }

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

    public NotaResponse createNota(NotaRequest request) {
        ApiNota entity = new ApiNota(); apply(entity, request); return nota(notas.save(entity));
    }

    public NotaResponse updateNota(Long id, NotaRequest request) {
        ApiNota entity = notas.findById(id).orElseThrow(() -> new NotFoundException("Nota não encontrada."));
        apply(entity, request); return nota(entity);
    }

    public void deleteNota(Long id) {
        ApiNota entity = notas.findById(id).orElseThrow(() -> new NotFoundException("Nota não encontrada."));
        notas.delete(entity);
    }

    public MessageResponse fecharBimestre(GerarBoletimRequest request) {
        ApiTurma turma = request.turmaId() == null ? null : directory.requireTurma(request.turmaId());
        boolean exists = turma == null
            ? fechamentos.findByAnoLetivoAndBimestreAndTurmaIsNull(request.anoLetivo(), request.bimestre()).isPresent()
            : fechamentos.findByAnoLetivoAndBimestreAndTurmaId(request.anoLetivo(), request.bimestre(), turma.getId()).isPresent();
        if (!exists) {
            ApiFechamentoBoletim fechamento = new ApiFechamentoBoletim();
            fechamento.setAnoLetivo(request.anoLetivo()); fechamento.setBimestre(request.bimestre()); fechamento.setTurma(turma);
            fechamentos.save(fechamento);
        }
        return new MessageResponse("Boletins gerados para o " + request.bimestre() + "º bimestre.");
    }

    @Transactional(readOnly = true)
    public BoletimResponse boletim(Long alunoId, int bimestre) {
        ApiAluno aluno = directory.requireAluno(alunoId);
        int ano = aluno.getTurma() == null ? LocalDate.now().getYear() : aluno.getTurma().getAnoLetivo();
        Long turmaId = aluno.getTurma() == null ? null : aluno.getTurma().getId();
        boolean fechado = fechamentos.findByAnoLetivoAndBimestreAndTurmaIsNull(ano, bimestre).isPresent()
            || turmaId != null && fechamentos.findByAnoLetivoAndBimestreAndTurmaId(ano, bimestre, turmaId).isPresent();
        if (!fechado) throw new BusinessRuleException("Bimestre ainda não fechado.");

        Map<Long, List<ApiNota>> porDisciplina = notas.findByAlunoIdAndBimestre(alunoId, bimestre).stream()
            .collect(Collectors.groupingBy(n -> n.getDisciplina().getId()));
        List<BoletimItemResponse> items = porDisciplina.values().stream().map(group -> {
            ApiDisciplina disciplina = group.get(0).getDisciplina();
            double peso = group.stream().mapToDouble(ApiNota::getPeso).sum();
            double media = peso == 0 ? 0 : group.stream().mapToDouble(n -> n.getValor() * n.getPeso()).sum() / peso;
            List<ApiPresenca> frequencias = presencas.findByAlunoId(alunoId).stream()
                .filter(p -> p.getDisciplina().getId().equals(disciplina.getId()))
                .toList();
            int frequencia = frequencias.isEmpty() ? 100 : (int) Math.round(100d * frequencias.stream().filter(p -> !"FALTA".equals(p.getStatus())).count() / frequencias.size());
            String situacao = media >= 6.0 && frequencia >= 75 ? "APROVADO" : "EM_RECUPERACAO";
            return new BoletimItemResponse(disciplina.getId(), disciplina.getNome(), round(media), frequencia, situacao);
        }).sorted(Comparator.comparing(BoletimItemResponse::disciplinaNome, String.CASE_INSENSITIVE_ORDER)).toList();
        return new BoletimResponse(items);
    }

    @Transactional(readOnly = true)
    public byte[] boletimPdf(Long alunoId, int bimestre) {
        BoletimResponse boletim = boletim(alunoId, bimestre);
        String lines = "Boletim - " + bimestre + "o Bimestre\\n" + boletim.disciplinas().stream()
            .map(item -> item.disciplinaNome() + " - Media: " + item.media() + " - " + item.situacao()).collect(Collectors.joining("\\n"));
        return minimalPdf(lines).getBytes(StandardCharsets.ISO_8859_1);
    }

    private void apply(ApiNota entity, NotaRequest request) {
        entity.setAluno(directory.requireAluno(request.alunoId())); entity.setDisciplina(directory.requireDisciplina(request.disciplinaId()));
        entity.setTurma(directory.requireTurma(request.turmaId())); entity.setBimestre(request.bimestre());
        entity.setTipo(request.tipo().trim().toUpperCase()); entity.setValor(request.valor()); entity.setPeso(request.peso());
    }
    private NotaResponse nota(ApiNota n) { return new NotaResponse(n.getId(), n.getAluno().getId(), n.getDisciplina().getId(), n.getDisciplina().getNome(), n.getBimestre(), n.getTipo(), n.getPeso(), n.getValor()); }
    private double round(double value) { return Math.round(value * 100d) / 100d; }

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
