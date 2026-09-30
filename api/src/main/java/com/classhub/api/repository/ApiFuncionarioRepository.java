package com.classhub.api.repository;

import com.classhub.api.domain.ApiFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link ApiFuncionario}, com
 * consulta para busca por e-mail (usada na autenticação).
 */
public interface ApiFuncionarioRepository extends JpaRepository<ApiFuncionario, Long> {
    /**
     * Busca um funcionário pelo e-mail, ignorando maiúsculas/minúsculas.
     *
     * @param email e-mail do funcionário
     * @return o funcionário encontrado, ou vazio se não existir
     */
    Optional<ApiFuncionario> findByEmailIgnoreCase(String email);
}
