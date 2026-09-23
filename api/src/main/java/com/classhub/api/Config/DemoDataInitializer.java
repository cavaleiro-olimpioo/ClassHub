package com.classhub.api.Config;

import com.classhub.api.domain.*;
import com.classhub.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Local credentials make the API usable on a new database; disable this outside development. */
@Configuration
public class DemoDataInitializer {
    @Bean
    @ConditionalOnProperty(name = "app.seed-demo-users", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedDemoUsers(ApiUserRepository users, ApiProfessorRepository professors,
                                    ApiAlunoRepository alunos, PasswordEncoder passwordEncoder) {
        return args -> {
            ApiProfessor professor = professors.findByEmailIgnoreCase("professor@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Professor Demo");
                created.setEmail("professor@classhub.local");
                return professors.save(created);
            });
            ApiAluno aluno = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseGet(() -> {
                ApiAluno created = new ApiAluno();
                created.setNome("Aluno Demo");
                created.setEmail("aluno@classhub.local");
                created.setMatricula("2026001");
                created.setDataNascimento(java.time.LocalDate.of(2012, 1, 1));
                return alunos.save(created);
            });
            seedUser(users, passwordEncoder, "admin@classhub.local", "admin123", "ADMIN", "Administrador", null);
            seedUser(users, passwordEncoder, "professor@classhub.local", "professor123", "PROFESSOR", professor.getNome(), professor.getId());
            seedUser(users, passwordEncoder, "aluno@classhub.local", "aluno123", "ALUNO", aluno.getNome(), aluno.getId());
        };
    }

    private void seedUser(ApiUserRepository users, PasswordEncoder encoder, String email, String password,
                          String perfil, String nome, Long vinculoId) {
        if (users.findByEmailIgnoreCase(email).isPresent()) return;
        ApiUser user = new ApiUser();
        user.setEmail(email);
        user.setSenhaHash(encoder.encode(password));
        user.setPerfil(perfil);
        user.setNome(nome);
        user.setVinculoId(vinculoId);
        users.save(user);
    }
}
