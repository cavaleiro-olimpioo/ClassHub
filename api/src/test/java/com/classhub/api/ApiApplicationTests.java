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

@SpringBootTest
class ApiApplicationTests {
    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @Autowired ApiAlunoRepository alunos;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc().perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

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

    @Test
    void funcionarioHerdarDoUsuarioEManterDadosEspecificos() {
        assertThat(com.classhub.api.domain.ApiFuncionario.class.getSuperclass()).isEqualTo(com.classhub.api.domain.ApiUser.class);
        assertThat(com.classhub.api.domain.ApiFuncionario.class.getDeclaredFields())
            .extracting(field -> field.getName())
            .contains("cargo", "setor")
            .doesNotContain("email", "nome");
    }

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

    @Test
    void loginRouteAcceptsTheRemoteServerPrefixedPath() throws Exception {
        MockMvc mockMvc = mockMvc();
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"professor@classhub.local\",\"senha\":\"professor123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

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

    @Test
    void corsPreflightAllowsTheViteFrontend() throws Exception {
        MockMvc mockMvc = mockMvc();
        mockMvc.perform(options("/alunos")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
            .build();
    }

    private String loginToken(MockMvc mockMvc, String email, String senha) throws Exception {
        String login = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(login).path("token").asText();
    }

}
