package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.GradeService;
import com.classhub.api.service.DirectoryService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;

/**
 * Controlador REST responsável por notas e boletins: lançamento de notas,
 * fechamento de bimestre e geração de boletins (em JSON e em PDF).
 */
@RestController
@RequestMapping(path = {"", "/api"})
public class GradeController {
    private final GradeService service;
    private final DirectoryService directory;
    /**
     * Cria o controlador injetando o serviço de notas e boletins.
     *
     * @param service serviço com a lógica de negócio de notas e boletins
     */
    public GradeController(GradeService service, DirectoryService directory) { this.service = service; this.directory = directory; }

    /**
     * Lista todas as notas de um aluno específico.
     *
     * @param alunoId identificador do aluno
     * @return lista de notas do aluno
     */
    @GetMapping("/notas/aluno/{alunoId}") @PreAuthorize("hasRole('ADMIN') or (hasRole('ALUNO') and authentication.name == #p0.toString())") public List<NotaResponse> notasAluno(@PathVariable Long alunoId) { return service.notasAluno(alunoId); }
    /**
     * Lista notas com filtros opcionais por turma, disciplina e bimestre.
     *
     * @param turmaId identificador da turma (opcional)
     * @param disciplinaId identificador da disciplina (opcional)
     * @param bimestre número do bimestre (opcional)
     * @return lista de notas que atendem aos filtros informados
     */
    @GetMapping("/notas") @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')") public List<NotaResponse> notas(@RequestParam(required = false) Long turmaId, @RequestParam(required = false) Long disciplinaId, @RequestParam(required = false) Integer bimestre, Authentication authentication) {
        if (!isProfessor(authentication)) return service.listNotas(turmaId, disciplinaId, bimestre);
        var links = directory.listVinculos(Long.valueOf(authentication.getName()));
        return links.stream()
            .filter(v -> turmaId == null || v.turmaId().equals(turmaId))
            .filter(v -> disciplinaId == null || v.disciplinaId().equals(disciplinaId))
            .flatMap(v -> service.listNotas(v.turmaId(), v.disciplinaId(), bimestre).stream())
            .collect(java.util.stream.Collectors.toMap(NotaResponse::id, n -> n, (a, b) -> a))
            .values().stream().toList();
    }
    /**
     * Lança uma nova nota para um aluno.
     *
     * @param request dados da nota a ser lançada
     * @return a nota recém-criada
     */
    @PostMapping("/notas") @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')") @ResponseStatus(HttpStatus.CREATED) public NotaResponse criarNota(@Valid @RequestBody NotaRequest request, Authentication authentication) {
        assertProfessorHasLink(authentication, request.turmaId(), request.disciplinaId());
        return service.createNota(request);
    }
    /**
     * Atualiza uma nota já lançada.
     *
     * @param id identificador da nota
     * @param request novos dados da nota
     * @return a nota atualizada
     */
    @PutMapping("/notas/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')") public NotaResponse editarNota(@PathVariable Long id, @Valid @RequestBody NotaRequest request, Authentication authentication) {
        assertProfessorHasLink(authentication, request.turmaId(), request.disciplinaId());
        if (isProfessor(authentication) && !service.professorHasAccessToNota(id, Long.valueOf(authentication.getName()))) throw new com.classhub.api.exception.ApiExceptions.ForbiddenException("O professor não possui vínculo com essa nota.");
        return service.updateNota(id, request);
    }
    /**
     * Exclui uma nota lançada.
     *
     * @param id identificador da nota a ser excluída
     */
    @DeleteMapping("/notas/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirNota(@PathVariable Long id, Authentication authentication) {
        if (isProfessor(authentication) && !service.professorHasAccessToNota(id, Long.valueOf(authentication.getName()))) throw new com.classhub.api.exception.ApiExceptions.ForbiddenException("O professor não possui vínculo com essa nota.");
        service.deleteNota(id);
    }

    /**
     * Fecha o bimestre informado (calcula e consolida os resultados finais
     * do boletim), impedindo alterações posteriores de notas para o período.
     *
     * @param request ano letivo, bimestre e, opcionalmente, a turma a ser fechada
     * @return mensagem de confirmação do fechamento
     */
    @PostMapping("/boletins/gerar") @PreAuthorize("hasRole('ADMIN')") public MessageResponse gerarBoletim(@Valid @RequestBody GerarBoletimRequest request) { return service.fecharBimestre(request); }
    /**
     * Retorna o boletim de um aluno para um bimestre específico, em formato JSON.
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return boletim com a lista de disciplinas, médias, frequência e situação
     */
    @GetMapping("/boletins/aluno/{alunoId}") @PreAuthorize("hasRole('ADMIN') or (hasRole('ALUNO') and authentication.name == #p0.toString())") public BoletimResponse boletim(@PathVariable Long alunoId, @RequestParam int bimestre) { return service.boletim(alunoId, bimestre); }
    /**
     * Gera e retorna o boletim de um aluno em formato PDF, pronto para download.
     *
     * @param alunoId identificador do aluno
     * @param bimestre número do bimestre
     * @return resposta HTTP contendo o arquivo PDF do boletim como anexo
     */
    @GetMapping(value = "/boletins/aluno/{alunoId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ALUNO') and authentication.name == #p0.toString())")
    public ResponseEntity<byte[]> pdf(@PathVariable Long alunoId, @RequestParam int bimestre) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boletim_" + bimestre + "bimestre.pdf").body(service.boletimPdf(alunoId, bimestre));
    }

    private boolean isProfessor(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR"));
    }

    private void assertProfessorHasLink(Authentication authentication, Long turmaId, Long disciplinaId) {
        if (isProfessor(authentication) && directory.listVinculos(Long.valueOf(authentication.getName())).stream()
            .noneMatch(v -> v.turmaId().equals(turmaId) && v.disciplinaId().equals(disciplinaId))) {
            throw new com.classhub.api.exception.ApiExceptions.ForbiddenException("O professor não possui vínculo com a turma e disciplina informadas.");
        }
    }
}
