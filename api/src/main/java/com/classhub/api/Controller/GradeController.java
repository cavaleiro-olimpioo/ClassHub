package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST responsável por notas e boletins: lançamento de notas,
 * fechamento de bimestre e geração de boletins (em JSON e em PDF).
 */
@RestController
@RequestMapping(path = {"", "/api"})
public class GradeController {
    private final GradeService service;
    /**
     * Cria o controlador injetando o serviço de notas e boletins.
     *
     * @param service serviço com a lógica de negócio de notas e boletins
     */
    public GradeController(GradeService service) { this.service = service; }

    /**
     * Lista todas as notas de um aluno específico.
     *
     * @param alunoId identificador do aluno
     * @return lista de notas do aluno
     */
    @GetMapping("/notas/aluno/{alunoId}") public List<NotaResponse> notasAluno(@PathVariable Long alunoId) { return service.notasAluno(alunoId); }
    /**
     * Lista notas com filtros opcionais por turma, disciplina e bimestre.
     *
     * @param turmaId identificador da turma (opcional)
     * @param disciplinaId identificador da disciplina (opcional)
     * @param bimestre número do bimestre (opcional)
     * @return lista de notas que atendem aos filtros informados
     */
    @GetMapping("/notas") public List<NotaResponse> notas(@RequestParam(required = false) Long turmaId, @RequestParam(required = false) Long disciplinaId, @RequestParam(required = false) Integer bimestre) { return service.listNotas(turmaId, disciplinaId, bimestre); }
    /**
     * Lança uma nova nota para um aluno.
     *
     * @param request dados da nota a ser lançada
     * @return a nota recém-criada
     */
    @PostMapping("/notas") @ResponseStatus(HttpStatus.CREATED) public NotaResponse criarNota(@Valid @RequestBody NotaRequest request) { return service.createNota(request); }
    /**
     * Atualiza uma nota já lançada.
     *
     * @param id identificador da nota
     * @param request novos dados da nota
     * @return a nota atualizada
     */
    @PutMapping("/notas/{id}") public NotaResponse editarNota(@PathVariable Long id, @Valid @RequestBody NotaRequest request) { return service.updateNota(id, request); }
    /**
     * Exclui uma nota lançada.
     *
     * @param id identificador da nota a ser excluída
     */
    @DeleteMapping("/notas/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirNota(@PathVariable Long id) { service.deleteNota(id); }

    /**
     * Fecha o bimestre informado (calcula e consolida os resultados finais
     * do boletim), impedindo alterações posteriores de notas para o período.
     *
     * @param request ano letivo, bimestre e, opcionalmente, a turma a ser fechada
     * @return mensagem de confirmação do fechamento
     */
    @PostMapping("/boletins/gerar") public MessageResponse gerarBoletim(@Valid @RequestBody GerarBoletimRequest request) { return service.fecharBimestre(request); }
    /**
     * Retorna o boletim de um aluno para um bimestre específico, em formato JSON.
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return boletim com a lista de disciplinas, médias, frequência e situação
     */
    @GetMapping("/boletins/aluno/{alunoId}") public BoletimResponse boletim(@PathVariable Long alunoId, @RequestParam int bimestre) { return service.boletim(alunoId, bimestre); }
    /**
     * Gera e retorna o boletim de um aluno em formato PDF, pronto para download.
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return resposta HTTP contendo o arquivo PDF do boletim como anexo
     */
    @GetMapping(value = "/boletins/aluno/{alunoId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable Long alunoId, @RequestParam int bimestre) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boletim_" + bimestre + "bimestre.pdf").body(service.boletimPdf(alunoId, bimestre));
    }
}
