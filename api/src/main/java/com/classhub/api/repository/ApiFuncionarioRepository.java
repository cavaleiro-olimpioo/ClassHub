package com.classhub.api.repository;

import com.classhub.api.domain.ApiFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiFuncionarioRepository extends JpaRepository<ApiFuncionario, Long> {
    Optional<ApiFuncionario> findByEmailIgnoreCase(String email);
}
