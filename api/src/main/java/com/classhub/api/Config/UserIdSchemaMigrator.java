package com.classhub.api.Config;

import java.sql.DatabaseMetaData;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class UserIdSchemaMigrator {
    private static final List<String> USER_TABLES = List.of(
        "api_alunos",
        "api_funcionarios",
        "api_professores"
    );

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
