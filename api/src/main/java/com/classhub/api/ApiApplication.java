package com.classhub.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe principal da aplicação ClassHub.
 * <p>
 * É o ponto de entrada do backend (API REST em Spring Boot). Configura o
 * escaneamento das entidades JPA no pacote {@code com.classhub.api.domain}
 * e habilita os repositórios Spring Data localizados em
 * {@code com.classhub.api.repository}. A auto-configuração padrão de
 * autenticação de usuários do Spring é desativada porque o ClassHub possui
 * sua própria implementação de autenticação (ver {@code security} e
 * {@code service.AuthService}).
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EntityScan(basePackages = "com.classhub.api.domain")
@EnableJpaRepositories(basePackages = "com.classhub.api.repository")
public class ApiApplication {

    /**
     * Método de entrada (main) do backend.
     * Inicializa o contexto do Spring Boot e sobe o servidor da API.
     *
     * @param args argumentos de linha de comando repassados ao Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
