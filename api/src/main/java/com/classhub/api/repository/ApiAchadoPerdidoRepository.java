package com.classhub.api.repository;

import com.classhub.api.domain.ApiAchadoPerdido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApiAchadoPerdidoRepository extends JpaRepository<ApiAchadoPerdido, Long> {
    List<ApiAchadoPerdido> findByCategoria(String categoria);
    List<ApiAchadoPerdido> findByStatus(String status);
    List<ApiAchadoPerdido> findByCategoriaAndStatus(String categoria, String status);
}
