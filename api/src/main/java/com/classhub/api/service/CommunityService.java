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

@Service
@Transactional
public class CommunityService {
    private static final Set<String> CATEGORIAS = Set.of("UNIFORME", "MATERIAL", "ELETRONICO", "OUTRO");
    private static final Set<String> TIPOS_CALENDARIO = Set.of("LETIVO", "FERIADO", "RECESSO", "EVENTO");
    private final ApiOcorrenciaRepository ocorrencias;
    private final ApiAchadoPerdidoRepository achados;
    private final ApiCalendarioRepository calendario;
    private final DirectoryService directory;

    public CommunityService(ApiOcorrenciaRepository ocorrencias, ApiAchadoPerdidoRepository achados,
                            ApiCalendarioRepository calendario, DirectoryService directory) {
        this.ocorrencias = ocorrencias; this.achados = achados; this.calendario = calendario; this.directory = directory;
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaResponse> listOcorrencias(Long alunoId) {
        List<ApiOcorrencia> result = alunoId == null ? ocorrencias.findAll() : ocorrencias.findByAlunoId(alunoId);
        return result.stream().sorted(Comparator.comparing(ApiOcorrencia::getId).reversed()).map(this::ocorrencia).toList();
    }

    public OcorrenciaResponse createOcorrencia(OcorrenciaRequest request) {
        ApiOcorrencia entity = new ApiOcorrencia();
        entity.setAluno(directory.requireAluno(request.alunoId())); entity.setTipo(request.tipo().trim().toUpperCase());
        entity.setDescricao(request.descricao().trim()); entity.setStatus("ABERTA");
        return ocorrencia(ocorrencias.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AchadoPerdidoResponse> listAchados(String categoria, String status) {
        List<ApiAchadoPerdido> result = categoria != null && status != null ? achados.findByCategoriaAndStatus(categoria, status)
            : categoria != null ? achados.findByCategoria(categoria) : status != null ? achados.findByStatus(status) : achados.findAll();
        return result.stream().sorted(Comparator.comparing(ApiAchadoPerdido::getData).reversed()).map(this::achado).toList();
    }

    public AchadoPerdidoResponse createAchado(AchadoPerdidoRequest request) {
        ApiAchadoPerdido entity = new ApiAchadoPerdido(); applyAchado(entity, request); entity.setStatus("NAO_REIVINDICADO");
        return achado(achados.save(entity));
    }

    public AchadoPerdidoResponse updateAchado(Long id, AchadoPerdidoRequest request) {
        ApiAchadoPerdido entity = requireAchado(id); applyAchado(entity, request); return achado(entity);
    }

    public AchadoPerdidoResponse devolverAchado(Long id) {
        ApiAchadoPerdido entity = requireAchado(id); entity.setStatus("DEVOLVIDO"); return achado(entity);
    }

    public void deleteAchado(Long id) { achados.delete(requireAchado(id)); }

    @Transactional(readOnly = true)
    public List<CalendarioResponse> listCalendario(Integer anoLetivo, Integer mes) {
        List<ApiCalendario> result = anoLetivo == null ? calendario.findAll() : calendario.findByAnoLetivo(anoLetivo);
        return result.stream().filter(c -> mes == null || c.getData().getMonthValue() == mes)
            .sorted(Comparator.comparing(ApiCalendario::getData)).map(this::calendario).toList();
    }

    public CalendarioResponse createCalendario(CalendarioRequest request) {
        ApiCalendario entity = new ApiCalendario(); applyCalendario(entity, request); return calendario(calendario.save(entity));
    }

    public CalendarioResponse updateCalendario(Long id, CalendarioRequest request) {
        ApiCalendario entity = calendario.findById(id).orElseThrow(() -> new NotFoundException("Evento do calendário não encontrado."));
        applyCalendario(entity, request); return calendario(entity);
    }

    public void deleteCalendario(Long id) {
        ApiCalendario entity = calendario.findById(id).orElseThrow(() -> new NotFoundException("Evento do calendário não encontrado."));
        calendario.delete(entity);
    }

    private void applyAchado(ApiAchadoPerdido entity, AchadoPerdidoRequest request) {
        String categoria = request.categoria().trim().toUpperCase();
        if (!CATEGORIAS.contains(categoria)) throw new BadRequestException("Categoria de achado e perdido inválida.");
        entity.setDescricao(request.descricao().trim()); entity.setCategoria(categoria); entity.setData(request.data()); entity.setLocalEncontrado(request.localEncontrado().trim());
    }
    private void applyCalendario(ApiCalendario entity, CalendarioRequest request) {
        String tipo = request.tipo().trim().toUpperCase();
        if (!TIPOS_CALENDARIO.contains(tipo)) throw new BadRequestException("Tipo de calendário inválido.");
        entity.setData(request.data()); entity.setAnoLetivo(request.anoLetivo()); entity.setTipo(tipo);
        entity.setTitulo(request.titulo().trim()); entity.setDescricao(request.descricao() == null || request.descricao().isBlank() ? null : request.descricao().trim());
    }
    private ApiAchadoPerdido requireAchado(Long id) { return achados.findById(id).orElseThrow(() -> new NotFoundException("Item não encontrado.")); }
    private OcorrenciaResponse ocorrencia(ApiOcorrencia o) { return new OcorrenciaResponse(o.getId(), o.getAluno().getId(), directory.alunoSummary(o.getAluno()), o.getTipo(), o.getDescricao(), o.getStatus()); }
    private AchadoPerdidoResponse achado(ApiAchadoPerdido item) { return new AchadoPerdidoResponse(item.getId(), item.getDescricao(), item.getCategoria(), item.getLocalEncontrado(), item.getData(), item.getStatus()); }
    private CalendarioResponse calendario(ApiCalendario item) { return new CalendarioResponse(item.getId(), item.getData(), item.getAnoLetivo(), item.getTipo(), item.getTitulo(), item.getDescricao()); }
}
