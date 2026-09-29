package com.classhub.api.repository;

import com.classhub.api.domain.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade base {@link ApiUser}, usado
 * principalmente para buscar qualquer tipo de usuário (aluno, professor ou
 * funcionário) pelo e-mail durante o login.
 */
public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {
    /**
     * Busca um usuário (de qualquer tipo) pelo e-mail, ignorando maiúsculas/minúsculas.
     *
     * @param email e-mail do usuário
     * @return o usuário encontrado, ou vazio se não existir
     */
    Optional<ApiUser> findByEmailIgnoreCase(String email);
}
