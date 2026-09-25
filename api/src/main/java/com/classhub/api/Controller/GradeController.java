package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = {"", "/api"})
public class GradeController {
    private final GradeService service;
    public GradeController(GradeService service) { this.service = service; }

    @GetMapping("/notas/aluno/{alunoId}") public List<NotaResponse> notasAluno(@PathVariable Long alunoId) { return service.notasAluno(alunoId); }
    @GetMapping("/notas") public List<NotaResponse> notas(@RequestParam(required = false) Long turmaId, @RequestParam(required = false) Long disciplinaId, @RequestParam(required = false) Integer bimestre) { return service.listNotas(turmaId, disciplinaId, bimestre); }
    @PostMapping("/notas") @ResponseStatus(HttpStatus.CREATED) public NotaResponse criarNota(@Valid @RequestBody NotaRequest request) { return service.createNota(request); }
    @PutMapping("/notas/{id}") public NotaResponse editarNota(@PathVariable Long id, @Valid @RequestBody NotaRequest request) { return service.updateNota(id, request); }
    @DeleteMapping("/notas/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirNota(@PathVariable Long id) { service.deleteNota(id); }

    @PostMapping("/boletins/gerar") public MessageResponse gerarBoletim(@Valid @RequestBody GerarBoletimRequest request) { return service.fecharBimestre(request); }
    @GetMapping("/boletins/aluno/{alunoId}") public BoletimResponse boletim(@PathVariable Long alunoId, @RequestParam int bimestre) { return service.boletim(alunoId, bimestre); }
    @GetMapping(value = "/boletins/aluno/{alunoId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable Long alunoId, @RequestParam int bimestre) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boletim_" + bimestre + "bimestre.pdf").body(service.boletimPdf(alunoId, bimestre));
    }
}
