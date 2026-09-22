package com.classhub.api.repository;

import com.classhub.api.domain.ApiCalendario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApiCalendarioRepository extends JpaRepository<ApiCalendario, Long> {
    List<ApiCalendario> findByAnoLetivo(Integer anoLetivo);
}
