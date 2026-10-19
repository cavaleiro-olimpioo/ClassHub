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

/**
 * Configuração responsável por popular o banco de dados com dados de
 * demonstração (usuários, turmas, disciplinas, horários, notas etc.) ao
 * iniciar a aplicação, facilitando testes manuais e apresentações do sistema.
 */
@Configuration
public class DemoDataInitializer {

    /**
     * Cria um {@link CommandLineRunner} executado na inicialização da
     * aplicação para popular o banco com dados de demonstração, caso ainda
     * não existam (cada etapa verifica a existência do registro antes de
     * criá-lo, evitando duplicidade em reinicializações).
     * <p>
     * Só é executado se a propriedade {@code app.seed-demo-users} for
     * {@code true} (valor padrão quando a propriedade não é definida).
     *
     * @param users repositório genérico de usuários
     * @param professors repositório de professores
     * @param alunos repositório de alunos
     * @param funcionarios repositório de funcionários
     * @param series repositório de séries
     * @param turmas repositório de turmas
     * @param disciplinas repositório de disciplinas
     * @param vinculos repositório de vínculos professor/turma/disciplina
     * @param horarios repositório de horários de aula
     * @param calendario repositório de eventos do calendário escolar
     * @param notas repositório de notas
     * @param ocorrencias repositório de ocorrências
     * @param achadosPerdidos repositório de itens de achados e perdidos
     * @param passwordEncoder codificador usado para gerar os hashes de senha dos usuários de demonstração
     * @return o runner que executa a criação dos dados de demonstração
     */
    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "app.seed-demo-users", havingValue = "true", matchIfMissing = false)
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
                created.setNome("Prof. Marcelo Souza (Matemática)");
                created.setEmail("professor@classhub.local");
                created.setTelefone("(11) 90000-0001");
                created.setDataNascimento(LocalDate.of(1985, 1, 15));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });

            ApiProfessor profPort = professors.findByEmailIgnoreCase("ana.portugues@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Profa. Ana Santos (Língua Portuguesa)");
                created.setEmail("ana.portugues@classhub.local");
                created.setTelefone("(11) 90000-0010");
                created.setDataNascimento(LocalDate.of(1988, 4, 12));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });

            ApiProfessor profCien = professors.findByEmailIgnoreCase("carlos.ciencias@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Prof. Carlos Oliveira (Ciências)");
                created.setEmail("carlos.ciencias@classhub.local");
                created.setTelefone("(11) 90000-0011");
                created.setDataNascimento(LocalDate.of(1982, 8, 25));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });

            ApiProfessor profHumanas = professors.findByEmailIgnoreCase("juliana.humanas@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Profa. Juliana Lima (História e Geografia)");
                created.setEmail("juliana.humanas@classhub.local");
                created.setTelefone("(11) 90000-0012");
                created.setDataNascimento(LocalDate.of(1991, 11, 3));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });

            ApiProfessor profLinguagens = professors.findByEmailIgnoreCase("roberto.linguagens@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Prof. Roberto Costa (Inglês e Arte)");
                created.setEmail("roberto.linguagens@classhub.local");
                created.setTelefone("(11) 90000-0013");
                created.setDataNascimento(LocalDate.of(1986, 6, 18));
                created.setSenhaHash(passwordEncoder.encode("professor123"));
                return professors.save(created);
            });

            ApiProfessor profEdFisica = professors.findByEmailIgnoreCase("fernanda.edfisica@classhub.local").orElseGet(() -> {
                ApiProfessor created = new ApiProfessor();
                created.setNome("Profa. Fernanda Rocha (Educação Física)");
                created.setEmail("fernanda.edfisica@classhub.local");
                created.setTelefone("(11) 90000-0014");
                created.setDataNascimento(LocalDate.of(1993, 2, 9));
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

            ApiTurma turma9B = turmas.findAll().stream()
                    .filter(t -> "9º Ano B".equalsIgnoreCase(t.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiTurma t9B = new ApiTurma();
                        t9B.setNome("9º Ano B");
                        t9B.setSerie(serie9);
                        t9B.setAnoLetivo(LocalDate.now().getYear());
                        t9B.setTurno("VESPERTINO");
                        t9B.setProfessorResponsavel(profPort);
                        return turmas.save(t9B);
                    });

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

            ApiDisciplina hist = disciplinas.findAll().stream()
                    .filter(d -> "História".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("História");
                        d.setCargaHoraria(60);
                        return disciplinas.save(d);
                    });

            ApiDisciplina geo = disciplinas.findAll().stream()
                    .filter(d -> "Geografia".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Geografia");
                        d.setCargaHoraria(60);
                        return disciplinas.save(d);
                    });

            ApiDisciplina ing = disciplinas.findAll().stream()
                    .filter(d -> "Inglês".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Inglês");
                        d.setCargaHoraria(40);
                        return disciplinas.save(d);
                    });

            ApiDisciplina arte = disciplinas.findAll().stream()
                    .filter(d -> "Arte".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Arte");
                        d.setCargaHoraria(40);
                        return disciplinas.save(d);
                    });

            ApiDisciplina edfis = disciplinas.findAll().stream()
                    .filter(d -> "Educação Física".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Educação Física");
                        d.setCargaHoraria(40);
                        return disciplinas.save(d);
                    });

            ApiDisciplina prog = disciplinas.findAll().stream()
                    .filter(d -> "Programação".equalsIgnoreCase(d.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        ApiDisciplina d = new ApiDisciplina();
                        d.setNome("Programação");
                        d.setCargaHoraria(60);
                        return disciplinas.save(d);
                    });
            // 7. Vínculos dos Professores com as Turmas (9º Ano A e 9º Ano B)
            java.util.List<Object[]> corpoDocente = java.util.List.of(
                    new Object[]{professor, mat},
                    new Object[]{profPort, port},
                    new Object[]{profCien, cien},
                    new Object[]{profHumanas, hist},
                    new Object[]{profHumanas, geo},
                    new Object[]{profLinguagens, ing},
                    new Object[]{profLinguagens, arte},
                    new Object[]{profEdFisica, edfis},
                    new Object[]{profLinguagens, prog}
            );

            for (ApiTurma targetTurma : java.util.List.of(turma9A, turma9B)) {
                for (Object[] par : corpoDocente) {
                    ApiProfessor p = (ApiProfessor) par[0];
                    ApiDisciplina d = (ApiDisciplina) par[1];
                    if (vinculos.findAll().stream().noneMatch(v -> v.getProfessor().getId().equals(p.getId()) &&
                            v.getTurma().getId().equals(targetTurma.getId()) &&
                            v.getDisciplina().getId().equals(d.getId()))) {
                        ApiVinculo v = new ApiVinculo();
                        v.setProfessor(p);
                        v.setTurma(targetTurma);
                        v.setDisciplina(d);
                        v.setAnoLetivo(LocalDate.now().getYear());
                        vinculos.save(v);
                    }
                }
            }

            // 8. Grade semanal: 30 aulas por turma, com seis períodos por dia.
            turma9A.setTurno("VESPERTINO");
            turmas.save(turma9A);
            turma9B.setTurno("VESPERTINO");
            turmas.save(turma9B);
            LocalTime[][] slotsVespertino = {
                    {LocalTime.of(13, 15), LocalTime.of(14, 5)},
                    {LocalTime.of(14, 5), LocalTime.of(14, 55)},
                    {LocalTime.of(14, 55), LocalTime.of(15, 45)},
                    {LocalTime.of(16, 0), LocalTime.of(16, 50)},
                    {LocalTime.of(16, 50), LocalTime.of(17, 40)},
                    {LocalTime.of(17, 40), LocalTime.of(18, 30)}
            };

            Object[][][] gradeSemanal9A = {
                    {{port, profPort}, {mat, professor}, {cien, profCien}, {hist, profHumanas}, {ing, profLinguagens}, {geo, profHumanas}},
                    {{mat, professor}, {port, profPort}, {prog, profLinguagens}, {edfis, profEdFisica}, {cien, profCien}, {geo, profHumanas}},
                    {{port, profPort}, {mat, professor}, {hist, profHumanas}, {prog, profLinguagens}, {arte, profLinguagens}, {ing, profLinguagens}},
                    {{mat, professor}, {cien, profCien}, {port, profPort}, {geo, profHumanas}, {prog, profLinguagens}, {edfis, profEdFisica}},
                    {{port, profPort}, {mat, professor}, {hist, profHumanas}, {arte, profLinguagens}, {ing, profLinguagens}, {edfis, profEdFisica}}
            };
            Object[][][] gradeSemanal9B = {
                    {{mat, professor}, {cien, profCien}, {hist, profHumanas}, {ing, profLinguagens}, {geo, profHumanas}, {port, profPort}},
                    {{port, profPort}, {prog, profLinguagens}, {edfis, profEdFisica}, {cien, profCien}, {geo, profHumanas}, {mat, professor}},
                    {{prog, profLinguagens}, {arte, profLinguagens}, {ing, profLinguagens}, {port, profPort}, {mat, professor}, {hist, profHumanas}},
                    {{cien, profCien}, {port, profPort}, {geo, profHumanas}, {prog, profLinguagens}, {edfis, profEdFisica}, {mat, professor}},
                    {{arte, profLinguagens}, {ing, profLinguagens}, {edfis, profEdFisica}, {port, profPort}, {mat, professor}, {hist, profHumanas}}
            };

            var grade9A = horarios.findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(turma9A.getId());
            var grade9B = horarios.findByTurmaIdOrderByDiaSemanaAscHoraInicioAsc(turma9B.getId());
            java.util.Set<Long> disciplinasPermitidas = java.util.Set.of(mat.getId(), port.getId(), cien.getId(),
                    hist.getId(), geo.getId(), ing.getId(), edfis.getId(), arte.getId(), prog.getId());
            boolean conflitoProfessor = grade9A.stream().anyMatch(aulaA -> grade9B.stream().anyMatch(aulaB ->
                    aulaA.getDiaSemana().equals(aulaB.getDiaSemana())
                            && aulaA.getProfessor().getId().equals(aulaB.getProfessor().getId())
                            && aulaA.getHoraInicio().isBefore(aulaB.getHoraFim())
                            && aulaB.getHoraInicio().isBefore(aulaA.getHoraFim())));
            boolean horariosCorretos = grade9A.size() == 30 && grade9B.size() == 30 && !conflitoProfessor
                    && java.util.stream.Stream.concat(grade9A.stream(), grade9B.stream()).allMatch(h -> {
                        if (h.getDiaSemana() == null || h.getDiaSemana() < 1 || h.getDiaSemana() > 5
                                || !disciplinasPermitidas.contains(h.getDisciplina().getId())) return false;
                        return java.util.Arrays.stream(slotsVespertino).anyMatch(faixa ->
                                faixa[0].equals(h.getHoraInicio()) && faixa[1].equals(h.getHoraFim()));
                    });
            if (!horariosCorretos) {
                horarios.deleteAll(grade9A);
                horarios.deleteAll(grade9B);
                for (int d = 0; d < 5; d++) {
                    int diaSemana = d + 1;
                    for (int slot = 0; slot < 6; slot++) {
                        ApiHorario hA = new ApiHorario();
                        hA.setTurma(turma9A);
                        hA.setDisciplina((ApiDisciplina) gradeSemanal9A[d][slot][0]);
                        hA.setProfessor((ApiProfessor) gradeSemanal9A[d][slot][1]);
                        hA.setDiaSemana(diaSemana);
                        hA.setHoraInicio(slotsVespertino[slot][0]);
                        hA.setHoraFim(slotsVespertino[slot][1]);
                        horarios.save(hA);

                        ApiHorario hB = new ApiHorario();
                        hB.setTurma(turma9B);
                        hB.setDisciplina((ApiDisciplina) gradeSemanal9B[d][slot][0]);
                        hB.setProfessor((ApiProfessor) gradeSemanal9B[d][slot][1]);
                        hB.setDiaSemana(diaSemana);
                        hB.setHoraInicio(slotsVespertino[slot][0]);
                        hB.setHoraFim(slotsVespertino[slot][1]);
                        horarios.save(hB);
                    }
                }
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
                e2.setTipo("EVENTO");
                e2.setTitulo("Semana de Avaliações Bimestrais");
                e2.setDescricao("Aplicação das provas oficiais do 1º bimestre.");
                calendario.save(e2);

                ApiCalendario e3 = new ApiCalendario();
                e3.setAnoLetivo(currentYear);
                e3.setData(LocalDate.of(currentYear, 4, 25));
                e3.setTipo("EVENTO");
                e3.setTitulo("Reunião de Pais e Mestres");
                e3.setDescricao("Entrega de boletins e alinhamento pedagógico.");
                calendario.save(e3);
            }

            // 10. Ocorrência Exemplo
            if (ocorrencias.count() == 0) {
                ApiOcorrencia oc = new ApiOcorrencia();
                oc.setAluno(alunoDemo);
                oc.setProfessor(professor);
                oc.setTipo("PEDAGOGICO");
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
                ap.setCategoria("UNIFORME");
                ap.setStatus("NAO_REIVINDICADO");
                ap.setData(LocalDate.now());
                ap.setFuncionarioRegistrou(funcionario);
                achadosPerdidos.save(ap);
            }

            // Normalize values written by older demo seeds to the current API enums.
            calendario.findAll().forEach(evento -> {
                if ("AVALIACAO".equals(evento.getTipo()) || "REUNIAO".equals(evento.getTipo())) {
                    evento.setTipo("EVENTO");
                    calendario.save(evento);
                }
            });
            notas.findAll().forEach(nota -> {
                if ("P1".equals(nota.getTipo())) {
                    nota.setTipo("PROVA");
                    notas.save(nota);
                }
            });
            ocorrencias.findAll().forEach(ocorrencia -> {
                if ("INFORMATIVA".equals(ocorrencia.getTipo())) {
                    ocorrencia.setTipo("PEDAGOGICO");
                    ocorrencias.save(ocorrencia);
                }
            });
            achadosPerdidos.findAll().forEach(item -> {
                boolean changed = false;
                if ("Vestuário".equalsIgnoreCase(item.getCategoria())) {
                    item.setCategoria("UNIFORME");
                    changed = true;
                }
                if ("DISPONIVEL".equals(item.getStatus())) {
                    item.setStatus("NAO_REIVINDICADO");
                    changed = true;
                }
                if (changed) achadosPerdidos.save(item);
            });
        };
    }
}
