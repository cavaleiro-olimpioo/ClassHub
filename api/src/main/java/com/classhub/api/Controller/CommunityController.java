package com.classhub.api.Controller;

import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.ForbiddenException;
import com.classhub.api.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST responsável pelas funcionalidades de "comunidade" da
 * escola: ocorrências disciplinares, achados e perdidos, e o calendário
 * escolar.
 */
@RestController
@RequestMapping(path = {"", "/api"})
public class CommunityController {
    private final CommunityService service;
    /**
     * Cria o controlador injetando o serviço de comunidade.
     *
     * @param service serviço com a lógica de negócio de ocorrências, achados e perdidos e calendário
     */
    public CommunityController(CommunityService service) { this.service = service; }

    /**
     * Lista as ocorrências registradas, com filtros opcionais por aluno e/ou turma.
     *
     * @param alunoId identificador do aluno (opcional)
     * @param turmaId identificador da turma (opcional)
     * @return lista de ocorrências que atendem aos filtros informados
     */
    @GetMapping("/ocorrencias")
    public List<OcorrenciaResponse> ocorrencias(@RequestParam(required = false) Long alunoId, @RequestParam(required = false) Long turmaId) {
        return service.listOcorrencias(alunoId, turmaId);
    }
    /**
     * Cria uma nova ocorrência para um aluno. Somente usuários com papel de
     * professor podem registrar ocorrências.
     *
     * @param request dados da ocorrência a ser criada
     * @param authentication contexto de autenticação da requisição, usado para identificar o professor logado
     * @return a ocorrência recém-criada
     * @throws ForbiddenException se o usuário autenticado não for um professor
     */
    @PostMapping("/ocorrencias") @ResponseStatus(HttpStatus.CREATED)
    public OcorrenciaResponse criarOcorrencia(@Valid @RequestBody OcorrenciaRequest request, Authentication authentication) {
        Long professorId = Long.valueOf(authentication.getName());
        if (authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_PROFESSOR"))) {
            throw new ForbiddenException("Apenas professores podem registrar ocorrências.");
        }
        return service.createOcorrencia(request, professorId);
    }
    /**
     * Atualiza os dados de uma ocorrência existente.
     *
     * @param id identificador da ocorrência
     * @param request novos dados da ocorrência
     * @return a ocorrência atualizada
     */
    @PutMapping("/ocorrencias/{id}") public OcorrenciaResponse editarOcorrencia(@PathVariable Long id, @Valid @RequestBody OcorrenciaRequest request) { return service.updateOcorrencia(id, request); }
    /**
     * Marca uma ocorrência como encerrada/resolvida.
     *
     * @param id identificador da ocorrência
     * @return a ocorrência com o status atualizado
     */
    @PutMapping("/ocorrencias/{id}/encerrar") public OcorrenciaResponse encerrarOcorrencia(@PathVariable Long id) { return service.encerrarOcorrencia(id); }
    /**
     * Exclui uma ocorrência.
     *
     * @param id identificador da ocorrência a ser excluída
     */
    @DeleteMapping("/ocorrencias/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirOcorrencia(@PathVariable Long id) { service.deleteOcorrencia(id); }

    /**
     * Lista os itens de achados e perdidos, com filtros opcionais por categoria e status.
     *
     * @param categoria categoria do item (opcional)
     * @param status status do item, ex.: "GUARDADO"/"DEVOLVIDO" (opcional)
     * @return lista de itens que atendem aos filtros informados
     */
    @GetMapping("/achados-perdidos") public List<AchadoPerdidoResponse> achados(@RequestParam(required = false) String categoria, @RequestParam(required = false) String status) { return service.listAchados(categoria, status); }
    /**
     * Registra um novo item encontrado.
     *
     * @param request dados do item encontrado
     * @return o item recém-criado
     */
    @PostMapping("/achados-perdidos") @ResponseStatus(HttpStatus.CREATED) public AchadoPerdidoResponse criarAchado(@Valid @RequestBody AchadoPerdidoRequest request) { return service.createAchado(request); }
    /**
     * Atualiza os dados de um item de achados e perdidos.
     *
     * @param id identificador do item
     * @param request novos dados do item
     * @return o item atualizado
     */
    @PutMapping("/achados-perdidos/{id}") public AchadoPerdidoResponse editarAchado(@PathVariable Long id, @Valid @RequestBody AchadoPerdidoRequest request) { return service.updateAchado(id, request); }
    /**
     * Marca um item de achados e perdidos como devolvido ao dono.
     *
     * @param id identificador do item
     * @return o item com o status atualizado
     */
    @PutMapping("/achados-perdidos/{id}/devolver") public AchadoPerdidoResponse devolverAchado(@PathVariable Long id) { return service.devolverAchado(id); }
    /**
     * Exclui um item de achados e perdidos.
     *
     * @param id identificador do item a ser excluído
     */
    @DeleteMapping("/achados-perdidos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirAchado(@PathVariable Long id) { service.deleteAchado(id); }

    /**
     * Lista os eventos do calendário escolar, com filtros opcionais por ano letivo e mês.
     *
     * @param anoLetivo ano letivo (opcional)
     * @param mes mês do evento (opcional)
     * @return lista de eventos que atendem aos filtros informados
     */
    @GetMapping("/calendario") public List<CalendarioResponse> calendario(@RequestParam(required = false) Integer anoLetivo, @RequestParam(required = false) Integer mes) { return service.listCalendario(anoLetivo, mes); }
    /**
     * Cria um novo evento no calendário escolar.
     *
     * @param request dados do evento
     * @return o evento recém-criado
     */
    @PostMapping("/calendario") @ResponseStatus(HttpStatus.CREATED) public CalendarioResponse criarCalendario(@Valid @RequestBody CalendarioRequest request) { return service.createCalendario(request); }
    /**
     * Atualiza um evento existente do calendário escolar.
     *
     * @param id identificador do evento
     * @param request novos dados do evento
     * @return o evento atualizado
     */
    @PutMapping("/calendario/{id}") public CalendarioResponse editarCalendario(@PathVariable Long id, @Valid @RequestBody CalendarioRequest request) { return service.updateCalendario(id, request); }
    /**
     * Exclui um evento do calendário escolar.
     *
     * @param id identificador do evento a ser excluído
     */
    @DeleteMapping("/calendario/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluirCalendario(@PathVariable Long id) { service.deleteCalendario(id); }
}
