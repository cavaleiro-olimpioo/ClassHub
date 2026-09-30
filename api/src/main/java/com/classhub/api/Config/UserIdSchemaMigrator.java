package com.classhub.api.Config;

import java.sql.DatabaseMetaData;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuração que ajusta, em tempo de inicialização, a geração dos
 * identificadores (id) das tabelas de usuários (alunos, funcionários e
 * professores) quando o banco de dados é PostgreSQL.
 * <p>
 * Como as entidades {@link com.classhub.api.domain.ApiAluno},
 * {@link com.classhub.api.domain.ApiFuncionario} e
 * {@link com.classhub.api.domain.ApiProfessor} usam herança
 * {@code TABLE_PER_CLASS} com {@code GenerationType.AUTO}, é necessário
 * garantir que todas compartilhem a mesma sequência de ids
 * ({@code api_user_seq}) para evitar colisões entre elas.
 */
@Configuration
public class UserIdSchemaMigrator {
    /** Nomes das tabelas de usuários que devem compartilhar a mesma sequência de ids. */
    private static final List<String> USER_TABLES = List.of(
        "api_alunos",
        "api_funcionarios",
        "api_professores"
    );

    /**
     * Cria um {@link CommandLineRunner}, executado antes dos demais
     * inicializadores ({@code @Order(0)}), que garante a existência da
     * sequência {@code api_user_seq} e configura cada tabela de usuário
     * para usá-la como valor padrão da coluna {@code id}, sincronizando o
     * valor atual da sequência com o maior id já utilizado.
     * <p>
     * Não faz nada caso o banco de dados não seja PostgreSQL.
     *
     * @param jdbcTemplate template JDBC usado para executar os comandos SQL de migração
     * @return o runner que executa o ajuste do esquema
     */
    @Bean
    @Order(0)
    CommandLineRunner alignUserIdGeneration(JdbcTemplate jdbcTemplate) {
        return args -> {
            try (var connection = jdbcTemplate.getDataSource().getConnection()) {
                DatabaseMetaData metadata = connection.getMetaData();
                if (!"PostgreSQL".equalsIgnoreCase(metadata.getDatabaseProductName())) return;
            }

            jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS api_user_seq START WITH 1 INCREMENT BY 50");
            for (String table : USER_TABLES) {
                if (isIdentityColumn(jdbcTemplate, table)) {
                    jdbcTemplate.execute("ALTER TABLE " + table + " ALTER COLUMN id DROP IDENTITY IF EXISTS");
                }
                jdbcTemplate.execute("ALTER TABLE " + table + " ALTER COLUMN id SET DEFAULT nextval('api_user_seq')");
            }

            jdbcTemplate.execute("""
                SELECT setval(
                    'api_user_seq',
                    GREATEST(
                        1,
                        COALESCE((SELECT MAX(id) FROM api_alunos), 0),
                        COALESCE((SELECT MAX(id) FROM api_funcionarios), 0),
                        COALESCE((SELECT MAX(id) FROM api_professores), 0)
                    ),
                    true
                )
                """);
        };
    }

    /**
     * Verifica se a coluna {@code id} de uma tabela está configurada como
     * coluna de identidade (identity column) do PostgreSQL.
     *
     * @param jdbcTemplate template JDBC usado para consultar o catálogo do banco
     * @param table nome da tabela a ser verificada
     * @return {@code true} se a coluna {@code id} for uma coluna de identidade
     */
    private boolean isIdentityColumn(JdbcTemplate jdbcTemplate, String table) {
        Boolean isIdentity = jdbcTemplate.queryForObject("""
            SELECT is_identity = 'YES'
            FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = ?
              AND column_name = 'id'
            """, Boolean.class, table);
        return Boolean.TRUE.equals(isIdentity);
    }
}
