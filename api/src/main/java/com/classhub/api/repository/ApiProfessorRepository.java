package com.classhub.api.repository;

import com.classhub.api.domain.ApiProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiProfessor}, com
 * consulta para busca por e-mail (usada na autenticação).
 */
public interface ApiProfessorRepository extends JpaRepository<ApiProfessor, Long> {
    /**
     * Busca um professor pelo e-mail, ignorando maiúsculas/minúsculas.
     *
     * @param email e-mail do professor
     * @return o professor encontrado, ou vazio se não existir
     */
    Optional<ApiProfessor> findByEmailIgnoreCase(String email);
}
