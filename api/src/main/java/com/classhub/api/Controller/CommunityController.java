package com.classhub.api.controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CommunityController {
    private final CommunityService service;
    public CommunityController(CommunityService service) { this.service = service; }

    @GetMapping("/ocorrencias") public List<OcorrenciaResponse> ocorrencias(@RequestParam(required = false) Long alunoId) { return service.listOcorrencias(alunoId); }
    @PostMapping("/ocorrencias") @ResponseStatus(HttpStatus.CREATED) public OcorrenciaResponse criarOcorrencia(@Valid @RequestBody OcorrenciaRequest request) { return service.createOcorrencia(request); }

    @GetMapping("/achados-perdidos") public List<AchadoPerdidoResponse> achados(@RequestParam(required = false) String categoria, @RequestParam(required = false) String status) { return service.listAchados(categoria, status); }
    @PostMapping("/achados-perdidos") @ResponseStatus(HttpStatus.CREATED) public AchadoPerdidoResponse criarAchado(@Valid @RequestBody AchadoPerdidoRequest request) { return service.createAchado(request); }
    @PutMapping("/achados-perdidos/{id}") public AchadoPerdidoResponse editarAchado(@PathVariable Long id, @Valid @RequestBody AchadoPerdidoRequest request) { return service.updateAchado(id, request); }
    @PutMapping("/achados-perdidos/{id}/devolver") public AchadoPerdidoResponse devolverAchado(@PathVariable Long id) { return service.devolverAchado(id); }
    @DeleteMapping("/achados-perdidos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirAchado(@PathVariable Long id) { service.deleteAchado(id); }

    @GetMapping("/calendario") public List<CalendarioResponse> calendario(@RequestParam(required = false) Integer anoLetivo, @RequestParam(required = false) Integer mes) { return service.listCalendario(anoLetivo, mes); }
    @PostMapping("/calendario") @ResponseStatus(HttpStatus.CREATED) public CalendarioResponse criarCalendario(@Valid @RequestBody CalendarioRequest request) { return service.createCalendario(request); }
    @PutMapping("/calendario/{id}") public CalendarioResponse editarCalendario(@PathVariable Long id, @Valid @RequestBody CalendarioRequest request) { return service.updateCalendario(id, request); }
    @DeleteMapping("/calendario/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirCalendario(@PathVariable Long id) { service.deleteCalendario(id); }
}
