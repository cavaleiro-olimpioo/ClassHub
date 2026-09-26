package com.classhub.api.Config;

import com.classhub.api.domain.*;
import com.classhub.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DemoDataInitializer {

    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "app.seed-demo-users", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedDemoUsers(ApiUserRepository users,
                                    ApiProfessorRepository professors,
                                    ApiAlunoRepository alunos,
                                    ApiFuncionarioRepository funcionarios,
                                    ApiSerieRepository series,
                                    ApiTurmaRepository turmas,
                                    ApiDisciplinaRepository disciplinas,
                                    ApiVinculoRepository vinculos,
                                    ApiHorarioRepository horarios,
                                    ApiCalendarioRepository calendario,
                                    ApiNotaRepository notas,
                                    ApiOcorrenciaRepository ocorrencias,
                                    ApiAchadoPerdidoRepository achadosPerdidos,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Professores
            ApiProfessor professor = professors.findByEmailIgnoreCase("professor@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Professor Demo");
                created.setEmail("professor@classhub.local");
                created.setTelefone("(11) 90000-0001");
                created.setDataNascimento(LocalDate.of(1985, 1, 15));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });

            // 2. Séries
            ApiSerie serie9 = series.findAll().stream()
                    .filter(s -> "9º Ano".equalsIgnoreCase(s.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiSerie s = new ApiSerie();
                        s.setNome("9º Ano");
                        s.setNivel("Ensino Fundamental II");
                        return series.save(s);
                    });

            if (series.findAll().stream().noneMatch(s -> "8º Ano".equalsIgnoreCase(s.getNome()))) {
                ApiSerie s8 = new ApiSerie();
                s8.setNome("8º Ano");
                s8.setNivel("Ensino Fundamental II");
                series.save(s8);
            }

            // 3. Turmas
            ApiTurma turma9A = turmas.findAll().stream()
                    .filter(t -> "9º Ano A".equalsIgnoreCase(t.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiTurma t = new ApiTurma();
                        t.setNome("9º Ano A");
                        t.setSerie(serie9);
                        t.setAnoLetivo(LocalDate.now().getYear());
                        t.setTurno("MATUTINO");
                        t.setProfessorResponsavel(professor);
                        return turmas.save(t);
                    });

            if (turmas.findAll().stream().noneMatch(t -> "9º Ano B".equalsIgnoreCase(t.getNome()))) {
                ApiTurma t9B = new ApiTurma();
                t9B.setNome("9º Ano B");
                t9B.setSerie(serie9);
                t9B.setAnoLetivo(LocalDate.now().getYear());
                t9B.setTurno("VESPERTINO");
                turmas.save(t9B);
            }

            // 4. Aluno Demo & Colegas de Turma
            ApiAluno alunoDemo = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseGet(() -> {
                ApiAluno created = new ApiAluno();
                created.setNome("Aluno Demo");
                created.setEmail("aluno@classhub.local");
                created.setTelefone("(11) 90000-0002");
                created.setDataNascimento(LocalDate.of(2012, 1, 1));
                created.setSenhaHash(passwordEncoder.encode("aluno123"));
                created.setMatricula("2026001");
                created.setTurma(turma9A);
                return alunos.save(created);
            });

            if (alunoDemo.getTurma() == null) {
                alunoDemo.setTurma(turma9A);
                alunos.save(alunoDemo);
            }

            alunos.findByEmailIgnoreCase("beatriz@classhub.local").orElseGet(() -> {
                ApiAluno created = new ApiAluno();
                created.setNome("Beatriz Santos");
                created.setEmail("beatriz@classhub.local");
                created.setTelefone("(11) 90000-0004");
                created.setDataNascimento(LocalDate.of(2012, 3, 14));
                created.setSenhaHash(passwordEncoder.encode("aluno123"));
                created.setMatricula("2026002");
                created.setTurma(turma9A);
                return alunos.save(created);
            });

            alunos.findByEmailIgnoreCase("carlos@classhub.local").orElseGet(() -> {
                ApiAluno created = new ApiAluno();
                created.setNome("Carlos Silva");
                created.setEmail("carlos@classhub.local");
                created.setTelefone("(11) 90000-0005");
                created.setDataNascimento(LocalDate.of(2012, 7, 22));
                created.setSenhaHash(passwordEncoder.encode("aluno123"));
                created.setMatricula("2026003");
                created.setTurma(turma9A);
                return alunos.save(created);
            });

            // 5. Funcionário
            ApiFuncionario funcionario = funcionarios.findByEmailIgnoreCase("funcionario@classhub.local").orElseGet(() -> {
                ApiFuncionario created = new ApiFuncionario();
                created.setNome("Funcionário Demo");
                created.setEmail("funcionario@classhub.local");
                created.setTelefone("(11) 90000-0003");
                created.setDataNascimento(LocalDate.of(1990, 5, 20));
                created.setSenhaHash(passwordEncoder.encode("funcionario123"));
                created.setCargo("Administrativo");
                created.setSetor("Secretaria");
                return funcionarios.save(created);
            });

            // 6. Disciplinas
            ApiDisciplina mat = disciplinas.findAll().stream()
                    .filter(d -> "Matemática".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Matemática");
                        d.setCargaHoraria(80);
                        return disciplinas.save(d);
                    });

            ApiDisciplina port = disciplinas.findAll().stream()
                    .filter(d -> "Língua Portuguesa".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Língua Portuguesa");
                        d.setCargaHoraria(80);
                        return disciplinas.save(d);
                    });

            ApiDisciplina cien = disciplinas.findAll().stream()
                    .filter(d -> "Ciências da Natureza".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Ciências da Natureza");
                        d.setCargaHoraria(60);
                        return disciplinas.save(d);
                    });

            if (disciplinas.findAll().stream().noneMatch(d -> "História".equalsIgnoreCase(d.getNome()))) {
                ApiDisciplina d = new ApiDisciplina();
                d.setNome("História");
                d.setCargaHoraria(60);
                disciplinas.save(d);
            }

            // 7. Vínculos do Professor Demo
            if (vinculos.findAll().stream().noneMatch(v -> v.getProfessor().getId().equals(professor.getId()) &&
                    v.getTurma().getId().equals(turma9A.getId()) &&
                    v.getDisciplina().getId().equals(mat.getId()))) {
                ApiVinculo v = new ApiVinculo();
                v.setProfessor(professor);
                v.setTurma(turma9A);
                v.setDisciplina(mat);
                v.setAnoLetivo(LocalDate.now().getYear());
                vinculos.save(v);
            }

            if (vinculos.findAll().stream().noneMatch(v -> v.getProfessor().getId().equals(professor.getId()) &&
                    v.getTurma().getId().equals(turma9A.getId()) &&
                    v.getDisciplina().getId().equals(cien.getId()))) {
                ApiVinculo v = new ApiVinculo();
                v.setProfessor(professor);
                v.setTurma(turma9A);
                v.setDisciplina(cien);
                v.setAnoLetivo(LocalDate.now().getYear());
                vinculos.save(v);
            }

            // 8. Horários da Semana
            if (horarios.count() == 0) {
                // Segunda (1): Mat 07:30 - 08:20
                ApiHorario h1 = new ApiHorario();
                h1.setTurma(turma9A);
                h1.setDisciplina(mat);
                h1.setProfessor(professor);
                h1.setDiaSemana(1);
                h1.setHoraInicio(LocalTime.of(7, 30));
                h1.setHoraFim(LocalTime.of(8, 20));
                horarios.save(h1);

                // Segunda (1): Mat 08:20 - 09:10
                ApiHorario h2 = new ApiHorario();
                h2.setTurma(turma9A);
                h2.setDisciplina(mat);
                h2.setProfessor(professor);
                h2.setDiaSemana(1);
                h2.setHoraInicio(LocalTime.of(8, 20));
                h2.setHoraFim(LocalTime.of(9, 10));
                horarios.save(h2);

                // Quarta (3): Ciências 09:30 - 10:20
                ApiHorario h3 = new ApiHorario();
                h3.setTurma(turma9A);
                h3.setDisciplina(cien);
                h3.setProfessor(professor);
                h3.setDiaSemana(3);
                h3.setHoraInicio(LocalTime.of(9, 30));
                h3.setHoraFim(LocalTime.of(10, 20));
                horarios.save(h3);

                // Sexta (5): Mat 10:20 - 11:10
                ApiHorario h4 = new ApiHorario();
                h4.setTurma(turma9A);
                h4.setDisciplina(mat);
                h4.setProfessor(professor);
                h4.setDiaSemana(5);
                h4.setHoraInicio(LocalTime.of(10, 20));
                h4.setHoraFim(LocalTime.of(11, 10));
                horarios.save(h4);
            }

            // 9. Calendário Escolar
            if (calendario.count() == 0) {
                int currentYear = LocalDate.now().getYear();
                ApiCalendario e1 = new ApiCalendario();
                e1.setAnoLetivo(currentYear);
                e1.setData(LocalDate.of(currentYear, 2, 10));
                e1.setTipo("LETIVO");
                e1.setTitulo("Início do 1º Bimestre Letivo");
                e1.setDescricao("Acolhimento aos alunos e apresentação do corpo docente.");
                calendario.save(e1);

                ApiCalendario e2 = new ApiCalendario();
                e2.setAnoLetivo(currentYear);
                e2.setData(LocalDate.of(currentYear, 4, 15));
                e2.setTipo("AVALIACAO");
                e2.setTitulo("Semana de Avaliações Bimestrais");
                e2.setDescricao("Aplicação das provas oficiais do 1º bimestre.");
                calendario.save(e2);

                ApiCalendario e3 = new ApiCalendario();
                e3.setAnoLetivo(currentYear);
                e3.setData(LocalDate.of(currentYear, 4, 25));
                e3.setTipo("REUNIAO");
                e3.setTitulo("Reunião de Pais e Mestres");
                e3.setDescricao("Entrega de boletins e alinhamento pedagógico.");
                calendario.save(e3);
            }

            // 10. Notas Demonstrativas
            if (notas.count() == 0) {
                ApiNota n1 = new ApiNota();
                n1.setAluno(alunoDemo);
                n1.setDisciplina(mat);
                n1.setTurma(turma9A);
                n1.setBimestre(1);
                n1.setTipo("P1");
                n1.setPeso(1.0);
                n1.setValor(8.5);
                notas.save(n1);

                ApiNota n2 = new ApiNota();
                n2.setAluno(alunoDemo);
                n2.setDisciplina(mat);
                n2.setTurma(turma9A);
                n2.setBimestre(1);
                n2.setTipo("TRABALHO");
                n2.setPeso(1.0);
                n2.setValor(9.0);
                notas.save(n2);
            }

            // 11. Ocorrência Exemplo
            if (ocorrencias.count() == 0) {
                ApiOcorrencia oc = new ApiOcorrencia();
                oc.setAluno(alunoDemo);
                oc.setProfessor(professor);
                oc.setTipo("INFORMATIVA");
                oc.setDescricao("Aluno esqueceu o material didático de ciências.");
                oc.setStatus("ABERTA");
                oc.setData(LocalDate.now());
                ocorrencias.save(oc);
            }

            // 12. Achados e Perdidos Exemplo
            if (achadosPerdidos.count() == 0) {
                ApiAchadoPerdido ap = new ApiAchadoPerdido();
                ap.setDescricao("Casaco de moletom azul escuro com capuz");
                ap.setLocalEncontrado("Quadra Poliesportiva");
                ap.setCategoria("Vestuário");
                ap.setStatus("DISPONIVEL");
                ap.setData(LocalDate.now());
                ap.setFuncionarioRegistrou(funcionario);
                achadosPerdidos.save(ap);
            }
        };
    }
}
