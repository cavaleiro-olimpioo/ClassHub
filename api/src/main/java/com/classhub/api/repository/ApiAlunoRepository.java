package com.classhub.api.repository;

import com.classhub.api.domain.ApiAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApiAlunoRepository extends JpaRepository<ApiAluno, Long> {
    List<ApiAluno> findByTurmaId(Long turmaId);
    List<ApiAluno> findByNomeContainingIgnoreCase(String nome);
    Optional<ApiAluno> findByEmailIgnoreCase(String email);
}
