package com.classhub.api.repository;

import com.classhub.api.domain.ApiVinculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApiVinculoRepository extends JpaRepository<ApiVinculo, Long> {
    List<ApiVinculo> findByProfessorId(Long professorId);
}
