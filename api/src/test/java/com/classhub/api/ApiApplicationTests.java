package com.classhub.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.classhub.api.domain.ApiAluno;
import com.classhub.api.repository.ApiAlunoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.servlet.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração de ponta a ponta da API do ClassHub, cobrindo o
 * carregamento do contexto Spring, autenticação via JWT, herança das
 * entidades de usuário, regras de autorização e o tratamento de erros
 * (CORS, validação e violações de integridade).
 */
@SpringBootTest
class ApiApplicationTests {
    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @Autowired ApiAlunoRepository alunos;
    @Autowired JdbcTemplate jdbcTemplate;

    /** Verifica se o contexto do Spring Boot sobe corretamente. */
    @Test
    void contextLoads() {
    }

    /** Verifica se o endpoint de health check é acessível sem autenticação. */
    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc().perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    /** Verifica se {@code ApiAluno} e {@code ApiProfessor} herdam corretamente de {@code ApiUser}. */
    @Test
    void alunoEProfessorHerdamDoUsuario() throws Exception {
        assertThat(com.classhub.api.domain.ApiAluno.class.getSuperclass()).isEqualTo(com.classhub.api.domain.ApiUser.class);
        assertThat(com.classhub.api.domain.ApiProfessor.class.getSuperclass()).isEqualTo(com.classhub.api.domain.ApiUser.class);
        assertThat(com.classhub.api.domain.ApiAluno.class.getDeclaredFields())
            .extracting(field -> field.getName())
            .doesNotContain("email", "nome");
        assertThat(com.classhub.api.domain.ApiProfessor.class.getDeclaredFields())
            .extracting(field -> field.getName())
            .doesNotContain("email", "nome");
    }

    /** Verifica se {@code ApiFuncionario} herda de {@code ApiUser} e mantém seus campos específicos (cargo, setor). */
    @Test
    void funcionarioHerdarDoUsuarioEManterDadosEspecificos() {
        assertThat(com.classhub.api.domain.ApiFuncionario.class.getSuperclass()).isEqualTo(com.classhub.api.domain.ApiUser.class);
        assertThat(com.classhub.api.domain.ApiFuncionario.class.getDeclaredFields())
            .extracting(field -> field.getName())
            .contains("cargo", "setor")
            .doesNotContain("email", "nome");
    }

    /** Verifica se o login retorna um token JWT válido e se ele permite acessar uma rota autenticada. */
    @Test
    void loginReturnsJwtAndAllowsAuthenticatedRequest() throws Exception {
        MockMvc mockMvc = mockMvc();
        String login = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"professor@classhub.local\",\"senha\":\"professor123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.perfil").value("PROFESSOR"))
            .andReturn().getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(login);

        mockMvc.perform(get("/alunos").header("Authorization", "Bearer " + response.path("token").asText()))
            .andExpect(status().isOk());
    }

    /** Professor e coordenador (o funcionário com autoridade ADMIN) podem listar alunos. */
    @Test
    void studentListReturnsOkForProfessorAndCoordinator() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/alunos").header("Authorization", "Bearer " + loginToken(mockMvc, "professor@classhub.local", "professor123")))
            .andExpect(status().isOk());
        mockMvc.perform(get("/alunos").header("Authorization", "Bearer " + loginToken(mockMvc, "funcionario@classhub.local", "funcionario123")))
            .andExpect(status().isOk());
    }

    /**
     * Exercita a sequence compartilhada depois de ultrapassar a faixa que a
     * migração reserva para os dados legados (ids até 140).
     */
    @Test
    void newStudentAndTeacherReceiveDistinctIdsAboveLegacyReservedRange() throws Exception {
        var padding = new ArrayList<ApiAluno>();
        for (int index = 0; index < 150; index++) {
            ApiAluno aluno = new ApiAluno();
            aluno.setNome("Reserva de sequence " + index);
            aluno.setEmail("sequence-padding-" + index + "@classhub.local");
            aluno.setSenhaHash("hash-de-teste");
            aluno.setMatricula("SEQ" + index);
            aluno.setDataNascimento(LocalDate.of(2012, 1, 1));
            padding.add(aluno);
        }
        alunos.saveAllAndFlush(padding);

        String suffix = UUID.randomUUID().toString();
        MockMvc mockMvc = mockMvc();
        String coordinatorToken = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");
        long alunoId = objectMapper.readTree(mockMvc.perform(post("/alunos")
                .header("Authorization", "Bearer " + coordinatorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Aluno Sequence\",\"email\":\"aluno-" + suffix + "@classhub.local\",\"matricula\":\"NEW" + suffix + "\",\"dataNascimento\":\"2012-01-01\"}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).path("id").asLong();
        long professorId = objectMapper.readTree(mockMvc.perform(post("/professores")
                .header("Authorization", "Bearer " + coordinatorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Professor Sequence\",\"email\":\"professor-" + suffix + "@classhub.local\"}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).path("id").asLong();

        assertThat(alunoId).isGreaterThan(140L);
        assertThat(professorId).isGreaterThan(140L).isNotEqualTo(alunoId);
        assertThat(userIdCollisions()).isZero();
    }

    /** O conjunto polimórfico ApiUser nunca pode ter o mesmo id em duas subclasses. */
    @Test
    void userIdsAreUniqueAcrossAllThreeUserTables() {
        assertThat(userIdCollisions()).isZero();
    }

    /** Verifica se listas de alunos toleram vínculos legados para turmas inexistentes. */
    @Test
    void studentListsHandleMissingLegacyClassForAdminAndProfessor() throws Exception {
        MockMvc mockMvc = mockMvc();
        var aluno = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseThrow();
        Long alunoId = aluno.getId();
        Long turmaId = aluno.getTurma().getId();
        Long turmaInexistente = turmaId + 999_999L;

        try {
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
            jdbcTemplate.update("UPDATE api_alunos SET turma_id = ? WHERE id = ?", turmaInexistente, alunoId);
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

            String tokenAdmin = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");
            String respostaAdmin = mockMvc.perform(get("/alunos").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            JsonNode alunosAdmin = objectMapper.readTree(respostaAdmin);
            JsonNode alunoLegado = null;
            for (JsonNode item : alunosAdmin) {
                if (item.path("id").asLong() == alunoId) {
                    alunoLegado = item;
                    break;
                }
            }
            assertThat(alunoLegado).isNotNull();
            assertThat(alunoLegado.path("turmaId").isNull()).isTrue();
            assertThat(alunoLegado.path("turma").isNull()).isTrue();

            String tokenProfessor = loginToken(mockMvc, "professor@classhub.local", "professor123");
            String respostaProfessor = mockMvc.perform(get("/alunos").header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            JsonNode alunosProfessor = objectMapper.readTree(respostaProfessor);
            assertThat(alunosProfessor).noneMatch(item -> item.path("id").asLong() == alunoId);
        } finally {
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
            jdbcTemplate.update("UPDATE api_alunos SET turma_id = ? WHERE id = ?", turmaId, alunoId);
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    /** Verifica se a rota de login também funciona com o prefixo "/api" usado pelo servidor remoto. */
    @Test
    void loginRouteAcceptsTheRemoteServerPrefixedPath() throws Exception {
        MockMvc mockMvc = mockMvc();
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"professor@classhub.local\",\"senha\":\"professor123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    /** Verifica se um professor autenticado consegue criar uma ocorrência para um aluno existente. */
    @Test
    void professorCanCreateOccurrenceWithAuthenticatedProfessorAndExistingStudent() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "professor@classhub.local", "professor123");
        Long alunoId = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseThrow().getId();

        mockMvc.perform(post("/ocorrencias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"alunoId\":" + alunoId + ",\"tipo\":\"OUTRO\",\"descricao\":\"Registro de teste\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.alunoId").value(alunoId))
            .andExpect(jsonPath("$.status").value("ABERTA"));
    }

    /** Verifica se a criação de ocorrência é rejeitada (HTTP 400) quando um campo obrigatório está ausente. */
    @Test
    void occurrenceRejectsMissingRequiredField() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "professor@classhub.local", "professor123");

        mockMvc.perform(post("/ocorrencias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"alunoId\":1,\"tipo\":\"OUTRO\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.containsString("descricao")));
    }

    /** Verifica se a criação de ocorrência retorna HTTP 404 quando o aluno informado não existe. */
    @Test
    void occurrenceReturnsNotFoundForMissingStudent() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "professor@classhub.local", "professor123");

        mockMvc.perform(post("/ocorrencias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"alunoId\":9223372036854775807,\"tipo\":\"OUTRO\",\"descricao\":\"Registro de teste\"}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensagem").value("Aluno não encontrado."));
    }

    /** A coordenação visualiza e pode reabrir um fechamento que esteja bloqueando lançamentos. */
    @Test
    void coordinatorCanListAndReopenClosedBimestre() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");
        int anoLetivo = LocalDate.now().getYear();

        mockMvc.perform(post("/boletins/gerar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"anoLetivo\":" + anoLetivo + ",\"bimestre\":4,\"dataInicio\":\"" + anoLetivo + "-10-01\",\"dataFim\":\"" + anoLetivo + "-12-20\"}"))
            .andExpect(status().isOk());

        JsonNode fechamentos = objectMapper.readTree(mockMvc.perform(get("/boletins/fechamentos")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString());
        JsonNode fechamento = null;
        for (JsonNode item : fechamentos) {
            if (item.path("anoLetivo").asInt() == anoLetivo && item.path("bimestre").asInt() == 4 && item.path("turmaId").isNull()) {
                fechamento = item;
                break;
            }
        }
        assertThat(fechamento).isNotNull();
        long fechamentoId = fechamento.path("id").asLong();

        mockMvc.perform(delete("/boletins/fechamentos/" + fechamentoId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        JsonNode aposReabrir = objectMapper.readTree(mockMvc.perform(get("/boletins/fechamentos")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString());
        assertThat(aposReabrir).noneMatch(item -> item.path("id").asLong() == fechamentoId);
    }

    /** Verifica se um funcionário autenticado consegue registrar um novo item de achados e perdidos. */
    /** Dados indevidos de um período podem ser removidos pela coordenação, liberando novos lançamentos. */
    @Test
    void coordinatorCanClearGradesAndReopenTheirPeriod() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");
        ApiAluno aluno = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseThrow();
        long turmaId = jdbcTemplate.queryForObject("SELECT turma_id FROM api_alunos WHERE id = ?", Long.class, aluno.getId());
        long disciplinaId = jdbcTemplate.queryForObject("SELECT id FROM api_disciplinas ORDER BY id LIMIT 1", Long.class);
        int anoLetivo = jdbcTemplate.queryForObject("SELECT ano_letivo FROM api_turmas WHERE id = ?", Integer.class, turmaId);

        mockMvc.perform(post("/notas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"alunoId\":" + aluno.getId() + ",\"turmaId\":" + turmaId + ",\"disciplinaId\":" + disciplinaId + ",\"bimestre\":3,\"tipo\":\"PROVA\",\"peso\":5,\"valor\":8}"))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/boletins/gerar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"anoLetivo\":" + anoLetivo + ",\"bimestre\":3,\"dataInicio\":\"" + anoLetivo + "-07-01\",\"dataFim\":\"" + anoLetivo + "-09-30\"}"))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/notas/periodo?anoLetivo=" + anoLetivo + "&bimestre=3")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.containsString("1 nota(s) removida(s)")));

        JsonNode notasRestantes = objectMapper.readTree(mockMvc.perform(get("/notas/aluno/" + aluno.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString());
        assertThat(notasRestantes).noneMatch(item -> item.path("bimestre").asInt() == 3);

        JsonNode fechamentosRestantes = objectMapper.readTree(mockMvc.perform(get("/boletins/fechamentos")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString());
        assertThat(fechamentosRestantes).noneMatch(item -> item.path("anoLetivo").asInt() == anoLetivo && item.path("bimestre").asInt() == 3);
    }

    /** A geração de demonstração cria prova e trabalho para cada disciplina do aluno. */
    @Test
    void coordinatorCanGenerateDemoGradesForEveryLinkedSubject() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");
        ApiAluno aluno = alunos.findByEmailIgnoreCase("aluno@classhub.local").orElseThrow();
        long turmaId = jdbcTemplate.queryForObject("SELECT turma_id FROM api_alunos WHERE id = ?", Long.class, aluno.getId());
        int anoLetivo = jdbcTemplate.queryForObject("SELECT ano_letivo FROM api_turmas WHERE id = ?", Integer.class, turmaId);

        mockMvc.perform(post("/notas/demonstracao")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"anoLetivo\":" + anoLetivo + ",\"bimestre\":2}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.containsString("nota(s) de demonstração gerada(s)")));

        JsonNode notasSegundoBimestre = objectMapper.readTree(mockMvc.perform(get("/notas/aluno/" + aluno.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString());
        int quantidade = 0;
        for (JsonNode nota : notasSegundoBimestre) if (nota.path("bimestre").asInt() == 2) quantidade++;
        assertThat(quantidade).isEqualTo(18);
    }

    @Test
    void funcionarioCanCreateFoundItem() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");

        String response = mockMvc.perform(post("/achados-perdidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\":\"Chave de teste\",\"categoria\":\"OUTRO\",\"data\":\"2026-09-29\",\"localEncontrado\":\"Biblioteca\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Chave de teste"))
            .andExpect(jsonPath("$.status").value("NAO_REIVINDICADO"))
            .andReturn().getResponse().getContentAsString();

        long itemId = objectMapper.readTree(response).path("id").asLong();
        Long responsavelId = jdbcTemplate.queryForObject(
            "SELECT funcionario_id FROM api_achados_perdidos WHERE id = ?", Long.class, itemId);
        Long funcionarioId = jdbcTemplate.queryForObject(
            "SELECT id FROM api_funcionarios WHERE email = ?", Long.class, "funcionario@classhub.local");
        assertThat(responsavelId).isEqualTo(funcionarioId);
    }

    /** Verifica se tentar criar um professor com e-mail já cadastrado retorna HTTP 409 com mensagem específica. */
    @Test
    void duplicateProfessorEmailReturnsSpecificConflict() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");

        mockMvc.perform(post("/professores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Professor Duplicado\",\"email\":\"professor@classhub.local\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensagem").value("Já existe um registro com esses dados."));
    }

    /** Verifica se a pré-checagem (preflight) de CORS permite a origem do frontend em desenvolvimento (Vite). */
    @Test
    void corsPreflightAllowsTheViteFrontend() throws Exception {
        MockMvc mockMvc = mockMvc();
        mockMvc.perform(options("/alunos")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    /**
     * Monta uma instância de {@link MockMvc} com a cadeia de filtros de
     * segurança da aplicação aplicada, para simular requisições HTTP reais nos testes.
     *
     * @return o {@code MockMvc} configurado
     */
    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
            .build();
    }

    /**
     * Realiza o login com as credenciais informadas e retorna o token JWT obtido.
     *
     * @param mockMvc instância de {@link MockMvc} usada para simular a requisição
     * @param email e-mail do usuário
     * @param senha senha do usuário
     * @return o token JWT retornado pelo login
     * @throws Exception se a requisição de login falhar
     */
    private String loginToken(MockMvc mockMvc, String email, String senha) throws Exception {
        String login = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(login).path("token").asText();
    }

    private long userIdCollisions() {
        Long result = jdbcTemplate.queryForObject("""
            SELECT count(*)
            FROM (
                SELECT id
                FROM (
                    SELECT id FROM api_alunos
                    UNION ALL SELECT id FROM api_professores
                    UNION ALL SELECT id FROM api_funcionarios
                ) all_user_ids
                GROUP BY id
                HAVING count(*) > 1
            ) collisions
            """, Long.class);
        return result == null ? 0L : result;
    }

}
