package com.classhub.api.repository;

import com.classhub.api.domain.ApiProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiProfessorRepository extends JpaRepository<ApiProfessor, Long> {
    Optional<ApiProfessor> findByEmailIgnoreCase(String email);
}
