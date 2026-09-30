package com.classhub.api.service;

import com.classhub.api.domain.*;
import com.classhub.api.dto.ApiDtos.*;
import com.classhub.api.exception.ApiExceptions.BadRequestException;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import com.classhub.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Serviço com a lógica de negócio das funcionalidades de "comunidade" da
 * escola: ocorrências disciplinares, achados e perdidos, e o calendário
 * escolar.
 * <p>
 * É responsável por validar os dados recebidos, aplicar as regras de
 * negócio e converter as entidades JPA em DTOs de resposta.
 */
@Service
@Transactional
public class CommunityService {
    /** Categorias válidas para itens de achados e perdidos. */
    private static final Set<String> CATEGORIAS = Set.of("UNIFORME", "MATERIAL", "ELETRONICO", "OUTRO");
    /** Tipos válidos de evento do calendário escolar. */
    private static final Set<String> TIPOS_CALENDARIO = Set.of("LETIVO", "FERIADO", "RECESSO", "EVENTO");
    private final ApiOcorrenciaRepository ocorrencias;
    private final ApiAchadoPerdidoRepository achados;
    private final ApiCalendarioRepository calendario;
    private final DirectoryService directory;

    /**
     * Cria o serviço de comunidade.
     *
     * @param ocorrencias repositório de ocorrências
     * @param achados repositório de itens de achados e perdidos
     * @param calendario repositório de eventos do calendário escolar
     * @param directory serviço de diretório, usado para buscar alunos e professores
     */
    public CommunityService(ApiOcorrenciaRepository ocorrencias, ApiAchadoPerdidoRepository achados,
                            ApiCalendarioRepository calendario, DirectoryService directory) {
        this.ocorrencias = ocorrencias; this.achados = achados; this.calendario = calendario; this.directory = directory;
    }

    /**
     * Lista ocorrências, opcionalmente filtradas por aluno e/ou turma,
     * ordenadas das mais recentes para as mais antigas.
     *
     * @param alunoId identificador do aluno (opcional)
     * @param turmaId identificador da turma (opcional)
     * @return lista de ocorrências convertidas em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<OcorrenciaResponse> listOcorrencias(Long alunoId, Long turmaId) {
        List<ApiOcorrencia> result = alunoId != null ? ocorrencias.findByAlunoId(alunoId) : ocorrencias.findAll();
        if (turmaId != null) {
            result = result.stream().filter(o -> o.getAluno() != null && o.getAluno().getTurma() != null && o.getAluno().getTurma().getId().equals(turmaId)).toList();
        }
        return result.stream().sorted(Comparator.comparing(ApiOcorrencia::getId).reversed()).map(this::ocorrencia).toList();
    }

    /**
     * Cria uma nova ocorrência para um aluno, registrada por um professor.
     * A data é definida automaticamente como a data atual e o status
     * inicial é sempre {@code "ABERTA"}.
     *
     * @param request dados da ocorrência
     * @param professorId identificador do professor que está registrando a ocorrência
     * @return a ocorrência recém-criada
     */
    public OcorrenciaResponse createOcorrencia(OcorrenciaRequest request, Long professorId) {
        ApiOcorrencia entity = new ApiOcorrencia();
        entity.setAluno(directory.requireAluno(request.alunoId()));
        entity.setProfessor(directory.requireProfessor(professorId));
        entity.setTipo(request.tipo().trim().toUpperCase());
        entity.setDescricao(request.descricao().trim());
        entity.setData(java.time.LocalDate.now());
        entity.setStatus("ABERTA");
        return ocorrencia(ocorrencias.save(entity));
    }

    /**
     * Atualiza os dados de uma ocorrência existente.
     *
     * @param id identificador da ocorrência
     * @param request novos dados da ocorrência
     * @return a ocorrência atualizada
     * @throws com.classhub.api.exception.ApiExceptions.NotFoundException se a ocorrência não existir
     */
    public OcorrenciaResponse updateOcorrencia(Long id, OcorrenciaRequest request) {
        ApiOcorrencia entity = requireOcorrencia(id);
        entity.setAluno(directory.requireAluno(request.alunoId()));
        entity.setTipo(request.tipo().trim().toUpperCase());
        entity.setDescricao(request.descricao().trim());
        return ocorrencia(entity);
    }

    /**
     * Marca uma ocorrência como encerrada (status {@code "ENCERRADA"}).
     *
     * @param id identificador da ocorrência
     * @return a ocorrência com o status atualizado
     */
    public OcorrenciaResponse encerrarOcorrencia(Long id) {
        ApiOcorrencia entity = requireOcorrencia(id);
        entity.setStatus("ENCERRADA");
        return ocorrencia(entity);
    }

    /**
     * Exclui uma ocorrência.
     *
     * @param id identificador da ocorrência a ser excluída
     */
    public void deleteOcorrencia(Long id) {
        ocorrencias.delete(requireOcorrencia(id));
    }

    /**
     * Busca uma ocorrência pelo identificador, lançando exceção caso não exista.
     *
     * @param id identificador da ocorrência
     * @return a entidade de ocorrência encontrada
     */
    private ApiOcorrencia requireOcorrencia(Long id) {
        return ocorrencias.findById(id).orElseThrow(() -> new NotFoundException("Ocorrência não encontrada."));
    }

    /**
     * Lista itens de achados e perdidos, com filtros opcionais por
     * categoria e/ou status, ordenados da data mais recente para a mais antiga.
     *
     * @param categoria categoria do item (opcional)
     * @param status status do item (opcional)
     * @return lista de itens convertidos em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<AchadoPerdidoResponse> listAchados(String categoria, String status) {
        List<ApiAchadoPerdido> result = categoria != null && status != null ? achados.findByCategoriaAndStatus(categoria, status)
            : categoria != null ? achados.findByCategoria(categoria) : status != null ? achados.findByStatus(status) : achados.findAll();
        return result.stream().sorted(Comparator.comparing(ApiAchadoPerdido::getData).reversed()).map(this::achado).toList();
    }

    /**
     * Registra um novo item de achados e perdidos. O status inicial é
     * sempre {@code "NAO_REIVINDICADO"}.
     *
     * @param request dados do item encontrado
     * @return o item recém-criado
     * @throws BadRequestException se a categoria informada não for válida
     */
    public AchadoPerdidoResponse createAchado(AchadoPerdidoRequest request) {
        ApiAchadoPerdido entity = new ApiAchadoPerdido(); applyAchado(entity, request); entity.setStatus("NAO_REIVINDICADO");
        return achado(achados.save(entity));
    }

    /**
     * Atualiza os dados de um item de achados e perdidos existente.
     *
     * @param id identificador do item
     * @param request novos dados do item
     * @return o item atualizado
     */
    public AchadoPerdidoResponse updateAchado(Long id, AchadoPerdidoRequest request) {
        ApiAchadoPerdido entity = requireAchado(id); applyAchado(entity, request); return achado(entity);
    }

    /**
     * Marca um item de achados e perdidos como devolvido (status {@code "DEVOLVIDO"}).
     *
     * @param id identificador do item
     * @return o item com o status atualizado
     */
    public AchadoPerdidoResponse devolverAchado(Long id) {
        ApiAchadoPerdido entity = requireAchado(id); entity.setStatus("DEVOLVIDO"); return achado(entity);
    }

    /**
     * Exclui um item de achados e perdidos.
     *
     * @param id identificador do item a ser excluído
     */
    public void deleteAchado(Long id) { achados.delete(requireAchado(id)); }

    /**
     * Lista eventos do calendário escolar, com filtros opcionais por ano
     * letivo e mês, ordenados por data crescente.
     *
     * @param anoLetivo ano letivo (opcional)
     * @param mes mês do evento, de 1 a 12 (opcional)
     * @return lista de eventos convertidos em DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<CalendarioResponse> listCalendario(Integer anoLetivo, Integer mes) {
        List<ApiCalendario> result = anoLetivo == null ? calendario.findAll() : calendario.findByAnoLetivo(anoLetivo);
        return result.stream().filter(c -> mes == null || c.getData().getMonthValue() == mes)
            .sorted(Comparator.comparing(ApiCalendario::getData)).map(this::calendario).toList();
    }

    /**
     * Cria um novo evento no calendário escolar.
     *
     * @param request dados do evento
     * @return o evento recém-criado
     * @throws BadRequestException se o tipo informado não for válido
     */
    public CalendarioResponse createCalendario(CalendarioRequest request) {
        ApiCalendario entity = new ApiCalendario(); applyCalendario(entity, request); return calendario(calendario.save(entity));
    }

    /**
     * Atualiza um evento existente do calendário escolar.
     *
     * @param id identificador do evento
     * @param request novos dados do evento
     * @return o evento atualizado
     * @throws com.classhub.api.exception.ApiExceptions.NotFoundException se o evento não existir
     */
    public CalendarioResponse updateCalendario(Long id, CalendarioRequest request) {
        ApiCalendario entity = calendario.findById(id).orElseThrow(() -> new NotFoundException("Evento do calendário não encontrado."));
        applyCalendario(entity, request); return calendario(entity);
    }

    /**
     * Exclui um evento do calendário escolar.
     *
     * @param id identificador do evento a ser excluído
     * @throws com.classhub.api.exception.ApiExceptions.NotFoundException se o evento não existir
     */
    public void deleteCalendario(Long id) {
        ApiCalendario entity = calendario.findById(id).orElseThrow(() -> new NotFoundException("Evento do calendário não encontrado."));
        calendario.delete(entity);
    }

    /**
     * Valida e aplica os dados de um {@link AchadoPerdidoRequest} sobre a entidade.
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     * @throws BadRequestException se a categoria informada não estiver na lista de categorias válidas
     */
    private void applyAchado(ApiAchadoPerdido entity, AchadoPerdidoRequest request) {
        String categoria = request.categoria().trim().toUpperCase();
        if (!CATEGORIAS.contains(categoria)) throw new BadRequestException("Categoria de achado e perdido inválida.");
        entity.setDescricao(request.descricao().trim()); entity.setCategoria(categoria); entity.setData(request.data()); entity.setLocalEncontrado(request.localEncontrado().trim());
    }
    /**
     * Valida e aplica os dados de um {@link CalendarioRequest} sobre a entidade.
     *
     * @param entity entidade a ser preenchida
     * @param request dados de entrada
     * @throws BadRequestException se o tipo informado não estiver na lista de tipos válidos
     */
    private void applyCalendario(ApiCalendario entity, CalendarioRequest request) {
        String tipo = request.tipo().trim().toUpperCase();
        if (!TIPOS_CALENDARIO.contains(tipo)) throw new BadRequestException("Tipo de calendário inválido.");
        entity.setData(request.data()); entity.setAnoLetivo(request.anoLetivo()); entity.setTipo(tipo);
        entity.setTitulo(request.titulo().trim()); entity.setDescricao(request.descricao() == null || request.descricao().isBlank() ? null : request.descricao().trim());
    }
    /**
     * Busca um item de achados e perdidos pelo identificador, lançando exceção caso não exista.
     *
     * @param id identificador do item
     * @return a entidade encontrada
     */
    private ApiAchadoPerdido requireAchado(Long id) { return achados.findById(id).orElseThrow(() -> new NotFoundException("Item não encontrado.")); }
    /** Converte uma entidade {@link ApiOcorrencia} para o DTO {@link OcorrenciaResponse}. */
    private OcorrenciaResponse ocorrencia(ApiOcorrencia o) { return new OcorrenciaResponse(o.getId(), o.getAluno().getId(), directory.alunoSummary(o.getAluno()), o.getTipo(), o.getDescricao(), o.getStatus(), o.getData()); }
    /** Converte uma entidade {@link ApiAchadoPerdido} para o DTO {@link AchadoPerdidoResponse}. */
    private AchadoPerdidoResponse achado(ApiAchadoPerdido item) { return new AchadoPerdidoResponse(item.getId(), item.getDescricao(), item.getCategoria(), item.getLocalEncontrado(), item.getData(), item.getStatus()); }
    /** Converte uma entidade {@link ApiCalendario} para o DTO {@link CalendarioResponse}. */
    private CalendarioResponse calendario(ApiCalendario item) { return new CalendarioResponse(item.getId(), item.getData(), item.getAnoLetivo(), item.getTipo(), item.getTitulo(), item.getDescricao()); }
}
