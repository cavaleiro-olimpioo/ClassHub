package com.classhub.api.repository;

import com.classhub.api.domain.ApiTurma;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiTurma}.
 * Utiliza apenas as operações padrão de CRUD fornecidas pelo Spring Data.
 */
public interface ApiTurmaRepository extends JpaRepository<ApiTurma, Long> { }
