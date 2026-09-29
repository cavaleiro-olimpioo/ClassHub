package com.classhub.api.repository;

import com.classhub.api.domain.ApiAchadoPerdido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiAchadoPerdido},
 * com consultas para filtrar itens de achados e perdidos por categoria e/ou status.
 */
public interface ApiAchadoPerdidoRepository extends JpaRepository<ApiAchadoPerdido, Long> {
    /**
     * Busca itens de achados e perdidos por categoria.
     *
     * @param categoria categoria a ser filtrada
     * @return lista de itens da categoria informada
     */
    List<ApiAchadoPerdido> findByCategoria(String categoria);
    /**
     * Busca itens de achados e perdidos por status.
     *
     * @param status status a ser filtrado (ex.: "GUARDADO", "DEVOLVIDO")
     * @return lista de itens com o status informado
     */
    List<ApiAchadoPerdido> findByStatus(String status);
    /**
     * Busca itens de achados e perdidos por categoria e status simultaneamente.
     *
     * @param categoria categoria a ser filtrada
     * @param status status a ser filtrado
     * @return lista de itens que atendem a ambos os filtros
     */
    List<ApiAchadoPerdido> findByCategoriaAndStatus(String categoria, String status);
}
