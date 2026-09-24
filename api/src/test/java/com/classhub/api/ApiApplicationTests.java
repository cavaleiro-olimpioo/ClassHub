package com.classhub.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    void contextLoads() {
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
                .content("{\"email\":\"admin@classhub.local\",\"senha\":\"admin123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.perfil").value("ADMIN"))
            .andReturn().getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(login);

        mockMvc.perform(get("/alunos").header("Authorization", "Bearer " + response.path("token").asText()))
            .andExpect(status().isOk());
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

}
