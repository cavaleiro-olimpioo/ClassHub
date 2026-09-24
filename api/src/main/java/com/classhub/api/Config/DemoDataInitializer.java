package com.classhub.api.Config;

import com.classhub.api.domain.*;
import com.classhub.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataInitializer {
    @Bean
    @ConditionalOnProperty(name = "app.seed-demo-users", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedDemoUsers(ApiUserRepository users, ApiProfessorRepository professors,
                                    ApiAlunoRepository alunos, ApiFuncionarioRepository funcionarios,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            ApiProfessor professor = professors.findByEmailIgnoreCase("professor@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Professor Demo");
                created.setEmail("professor@classhub.local");
                created.setTelefone("(11) 90000-0001");
                created.setDataNascimento(java.time.LocalDate.of(1985, 1, 15));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });
            ApiAluno aluno = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseGet(() -> {
                ApiAluno created = new ApiAluno();
                created.setNome("Aluno Demo");
                created.setEmail("aluno@classhub.local");
                created.setTelefone("(11) 90000-0002");
                created.setDataNascimento(java.time.LocalDate.of(2012, 1, 1));
                created.setSenhaHash(passwordEncoder.encode("aluno123"));
                created.setMatricula("2026001");
                return alunos.save(created);
            });
            ApiFuncionario funcionario = funcionarios.findByEmailIgnoreCase("funcionario@classhub.local").orElseGet(() -> {
                ApiFuncionario created = new ApiFuncionario();
                created.setNome("Funcionário Demo");
                created.setEmail("funcionario@classhub.local");
                created.setTelefone("(11) 90000-0003");
                created.setDataNascimento(java.time.LocalDate.of(1990, 5, 20));
                created.setSenhaHash(passwordEncoder.encode("funcionario123"));
                created.setCargo("Administrativo");
                created.setSetor("Secretaria");
                return funcionarios.save(created);
            });
        };
    }
}
