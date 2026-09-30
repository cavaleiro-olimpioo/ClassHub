package com.classhub.api.repository;

import com.classhub.api.domain.ApiCalendario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiCalendario}, com
 * consulta para filtrar eventos por ano letivo.
 */
public interface ApiCalendarioRepository extends JpaRepository<ApiCalendario, Long> {
    /**
     * Busca eventos do calendário escolar de um ano letivo específico.
     *
     * @param anoLetivo ano letivo a ser filtrado
     * @return lista de eventos do ano letivo informado
     */
    List<ApiCalendario> findByAnoLetivo(Integer anoLetivo);
}
