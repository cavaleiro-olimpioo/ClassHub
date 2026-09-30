package com.classhub.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.classhub.api.repository.ApiAlunoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import jakarta.servlet.Filter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    /** Verifica se um funcionário autenticado consegue registrar um novo item de achados e perdidos. */
    @Test
    void funcionarioCanCreateFoundItem() throws Exception {
        MockMvc mockMvc = mockMvc();
        String token = loginToken(mockMvc, "funcionario@classhub.local", "funcionario123");

        mockMvc.perform(post("/achados-perdidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\":\"Chave de teste\",\"categoria\":\"OUTRO\",\"data\":\"2026-09-29\",\"localEncontrado\":\"Biblioteca\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Chave de teste"))
            .andExpect(jsonPath("$.status").value("NAO_REIVINDICADO"));
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

}
