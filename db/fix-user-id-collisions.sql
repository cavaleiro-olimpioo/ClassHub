-- Execute somente durante janela de manutenção, conectado ao banco-alvo.
-- O script é atômico: qualquer falha faz rollback da renumeração.
BEGIN;

-- Evita inserções concorrentes nas três tabelas enquanto os ids e a sequence são alinhados.
LOCK TABLE api_alunos, api_professores, api_funcionarios IN ACCESS EXCLUSIVE MODE;

CREATE SEQUENCE IF NOT EXISTS api_user_seq START WITH 1 INCREMENT BY 50;

DO $$
BEGIN
    IF (SELECT increment_by FROM pg_sequences
        WHERE schemaname = current_schema() AND sequencename = 'api_user_seq') <> 50 THEN
        RAISE EXCEPTION 'api_user_seq precisa ter INCREMENT BY 50';
    END IF;

    IF (SELECT count(*) FROM api_professores) <> 9
       OR EXISTS (SELECT 1 FROM api_professores WHERE id NOT BETWEEN 1 AND 9) THEN
        RAISE EXCEPTION 'Pré-condição inválida: api_professores deve conter exatamente ids 1..9';
    END IF;

    IF (SELECT count(*) FROM api_funcionarios) <> 5
       OR EXISTS (SELECT 1 FROM api_funcionarios WHERE id NOT BETWEEN 1 AND 5) THEN
        RAISE EXCEPTION 'Pré-condição inválida: api_funcionarios deve conter exatamente ids 1..5';
    END IF;

    IF EXISTS (SELECT 1 FROM api_alunos WHERE id NOT BETWEEN 1 AND 76)
       OR (SELECT count(*) FROM api_alunos) <> 76 THEN
        RAISE EXCEPTION 'Pré-condição inválida: api_alunos deve permanecer exatamente com ids 1..76';
    END IF;

    -- O setval final é intencionalmente o valor exato max(id) + 50 (= 140).
    -- Não permita que ele faça a sequence já avançada retroceder.
    IF COALESCE((SELECT last_value FROM pg_sequences
                 WHERE schemaname = current_schema() AND sequencename = 'api_user_seq'), 1) > 140 THEN
        RAISE EXCEPTION 'api_user_seq já está acima de 140; revise a faixa antes de executar o setval planejado';
    END IF;
END
$$;

-- Verificação antes da alteração: as três consultas devem evidenciar as colisões atuais.
SELECT 'alunos_professores' AS par, count(*) AS ids_colidindo
FROM (SELECT id FROM api_alunos INTERSECT SELECT id FROM api_professores) ids
UNION ALL
SELECT 'alunos_funcionarios', count(*)
FROM (SELECT id FROM api_alunos INTERSECT SELECT id FROM api_funcionarios) ids
UNION ALL
SELECT 'professores_funcionarios', count(*)
FROM (SELECT id FROM api_professores INTERSECT SELECT id FROM api_funcionarios) ids;

CREATE TEMP TABLE tmp_professor_ids (
    old_id bigint PRIMARY KEY,
    new_id bigint NOT NULL UNIQUE
) ON COMMIT DROP;
INSERT INTO tmp_professor_ids (old_id, new_id) VALUES
    (1, 77), (2, 78), (3, 79), (4, 80), (5, 81),
    (6, 82), (7, 83), (8, 84), (9, 85);

CREATE TEMP TABLE tmp_funcionario_ids (
    old_id bigint PRIMARY KEY,
    new_id bigint NOT NULL UNIQUE
) ON COMMIT DROP;
INSERT INTO tmp_funcionario_ids (old_id, new_id) VALUES
    (1, 86), (2, 87), (3, 88), (4, 89), (5, 90);

-- Catálogo das FKs reais que referenciam professor ou funcionário.
-- O script não depende de nomes de constraints criados pelo Hibernate.
CREATE TEMP TABLE tmp_user_id_fks ON COMMIT DROP AS
SELECT
    child_ns.nspname AS child_schema,
    child.relname AS child_table,
    child_att.attname AS child_column,
    parent_ns.nspname AS parent_schema,
    parent.relname AS parent_table,
    c.conname AS constraint_name,
    c.condeferrable AS was_deferrable,
    c.condeferred AS was_initially_deferred,
    cardinality(c.conkey) AS column_count
FROM pg_constraint c
JOIN pg_class child ON child.oid = c.conrelid
JOIN pg_namespace child_ns ON child_ns.oid = child.relnamespace
JOIN pg_class parent ON parent.oid = c.confrelid
JOIN pg_namespace parent_ns ON parent_ns.oid = parent.relnamespace
JOIN LATERAL unnest(c.conkey) AS key_attnum(attnum) ON true
JOIN pg_attribute child_att
  ON child_att.attrelid = child.oid AND child_att.attnum = key_attnum.attnum
WHERE c.contype = 'f'
  AND parent_ns.nspname = current_schema()
  AND parent.relname IN ('api_professores', 'api_funcionarios');

SELECT child_schema, child_table, child_column, parent_schema, parent_table, constraint_name
FROM tmp_user_id_fks
ORDER BY parent_table, child_schema, child_table, child_column;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM tmp_user_id_fks WHERE column_count <> 1) THEN
        RAISE EXCEPTION 'FK composta para ApiProfessor/ApiFuncionario não é suportada por esta migração';
    END IF;
END
$$;

-- Auditoria das colunas de ids de usuário conhecidas no mapeamento JPA.
-- Para aluno não há renumeração; as demais precisam ser atualizadas se não
-- houver FK declarada. O SELECT abaixo lista ambos os casos explicitamente.
CREATE TEMP TABLE tmp_expected_user_id_columns (
    parent_table name,
    child_table name,
    child_column name,
    needs_remap boolean,
    justification text
) ON COMMIT DROP;
INSERT INTO tmp_expected_user_id_columns VALUES
    ('api_alunos',       'api_aluno_responsaveis', 'aluno_id',                    false, 'Aluno não é renumerado; auditar integridade do vínculo com responsável.'),
    ('api_alunos',       'api_documentos',         'aluno_id',                    false, 'Aluno não é renumerado; auditar integridade dos documentos.'),
    ('api_alunos',       'api_matriculas',         'aluno_id',                    false, 'Aluno não é renumerado; auditar integridade das matrículas.'),
    ('api_alunos',       'api_notas',              'aluno_id',                    false, 'Aluno não é renumerado; auditar integridade das notas.'),
    ('api_alunos',       'api_ocorrencias',        'aluno_id',                    false, 'Aluno não é renumerado; auditar integridade das ocorrências.'),
    ('api_alunos',       'api_presencas',          'aluno_id',                    false, 'Aluno não é renumerado; auditar integridade das presenças.'),
    ('api_professores',  'api_horarios',           'professor_id',                true,  'Professor é renumerado; horário precisa acompanhar o novo id.'),
    ('api_professores',  'api_ocorrencias',        'professor_id',                true,  'Professor é renumerado; ocorrência precisa acompanhar o novo id.'),
    ('api_professores',  'api_turmas',             'professor_responsavel_id',    true,  'Professor responsável é renumerado; turma precisa acompanhar o novo id.'),
    ('api_professores',  'api_vinculos',           'professor_id',                true,  'Professor é renumerado; vínculo precisa acompanhar o novo id.'),
    ('api_funcionarios', 'api_achados_perdidos',   'funcionario_id',              true,  'Funcionário é renumerado; registro de achados e perdidos precisa acompanhar o novo id.');

CREATE TEMP TABLE tmp_user_id_columns_without_fk ON COMMIT DROP AS
SELECT expected.*
FROM tmp_expected_user_id_columns expected
JOIN pg_class child ON child.relname = expected.child_table
JOIN pg_namespace child_ns ON child_ns.oid = child.relnamespace
JOIN pg_attribute child_att
  ON child_att.attrelid = child.oid
 AND child_att.attname = expected.child_column
 AND child_att.attnum > 0
 AND NOT child_att.attisdropped
WHERE child_ns.nspname = current_schema()
  AND NOT EXISTS (
      SELECT 1
      FROM pg_constraint c
      JOIN pg_class parent ON parent.oid = c.confrelid
      JOIN pg_namespace parent_ns ON parent_ns.oid = parent.relnamespace
      WHERE c.contype = 'f'
        AND c.conrelid = child.oid
        AND child_att.attnum = ANY (c.conkey)
        AND parent_ns.nspname = current_schema()
        AND parent.relname = expected.parent_table
  );

SELECT parent_table, child_table, child_column, needs_remap, justification
FROM tmp_user_id_columns_without_fk
ORDER BY parent_table, child_table, child_column;

-- Não adivine semântica de uma coluna de usuário que não esteja no modelo acima.
-- O operador deve classificá-la antes de executar uma alteração de ids.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_attribute a
        JOIN pg_class c ON c.oid = a.attrelid
        JOIN pg_namespace ns ON ns.oid = c.relnamespace
        WHERE ns.nspname = current_schema()
          AND c.relkind IN ('r', 'p')
          AND a.attnum > 0
          AND NOT a.attisdropped
          AND a.attname IN ('aluno_id', 'professor_id', 'professor_responsavel_id', 'funcionario_id')
          AND NOT EXISTS (
              SELECT 1 FROM tmp_expected_user_id_columns known
              WHERE known.child_table = c.relname AND known.child_column = a.attname
          )
    ) THEN
        RAISE EXCEPTION 'Há coluna de id de usuário não classificada; revise o SELECT de auditoria antes de continuar';
    END IF;
END
$$;

-- FKs do Hibernate são normalmente NOT DEFERRABLE. Tornamo-las adiáveis só
-- durante esta transação para poder alterar filhos e pais sem quebrar a RI,
-- e restauramos exatamente a configuração original ao final.
DO $$
DECLARE fk record;
BEGIN
    FOR fk IN SELECT * FROM tmp_user_id_fks LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I ALTER CONSTRAINT %I DEFERRABLE INITIALLY IMMEDIATE',
            fk.child_schema, fk.child_table, fk.constraint_name
        );
    END LOOP;
END
$$;

SET CONSTRAINTS ALL DEFERRED;

-- Atualiza todas as FKs encontradas por pg_constraint.
DO $$
DECLARE fk record;
DECLARE mapping_table text;
BEGIN
    FOR fk IN SELECT * FROM tmp_user_id_fks LOOP
        mapping_table := CASE fk.parent_table
            WHEN 'api_professores' THEN 'tmp_professor_ids'
            WHEN 'api_funcionarios' THEN 'tmp_funcionario_ids'
        END;
        EXECUTE format(
            'UPDATE %I.%I AS child SET %I = map.new_id FROM %I AS map WHERE child.%I = map.old_id',
            fk.child_schema, fk.child_table, fk.child_column, mapping_table, fk.child_column
        );
    END LOOP;
END
$$;

-- Atualiza também as referências conhecidas que existam sem FK declarada.
DO $$
DECLARE ref record;
DECLARE mapping_table text;
BEGIN
    FOR ref IN SELECT * FROM tmp_user_id_columns_without_fk WHERE needs_remap LOOP
        mapping_table := CASE ref.parent_table
            WHEN 'api_professores' THEN 'tmp_professor_ids'
            WHEN 'api_funcionarios' THEN 'tmp_funcionario_ids'
        END;
        EXECUTE format(
            'UPDATE %I.%I AS child SET %I = map.new_id FROM %I AS map WHERE child.%I = map.old_id',
            current_schema(), ref.child_table, ref.child_column, mapping_table, ref.child_column
        );
    END LOOP;
END
$$;

UPDATE api_professores AS professor
SET id = map.new_id
FROM tmp_professor_ids AS map
WHERE professor.id = map.old_id;

UPDATE api_funcionarios AS funcionario
SET id = map.new_id
FROM tmp_funcionario_ids AS map
WHERE funcionario.id = map.old_id;

SET CONSTRAINTS ALL IMMEDIATE;

-- Restaura a propriedade de cada FK tal como era antes da transação.
DO $$
DECLARE fk record;
BEGIN
    FOR fk IN SELECT * FROM tmp_user_id_fks LOOP
        IF fk.was_deferrable THEN
            EXECUTE format(
                'ALTER TABLE %I.%I ALTER CONSTRAINT %I DEFERRABLE INITIALLY %s',
                fk.child_schema, fk.child_table, fk.constraint_name,
                CASE WHEN fk.was_initially_deferred THEN 'DEFERRED' ELSE 'IMMEDIATE' END
            );
        ELSE
            EXECUTE format(
                'ALTER TABLE %I.%I ALTER CONSTRAINT %I NOT DEFERRABLE',
                fk.child_schema, fk.child_table, fk.constraint_name
            );
        END IF;
    END LOOP;
END
$$;

-- Hibernate pooled (allocationSize=50) usará a faixa [valor - 49, valor].
-- Com max(id)=90, setval em 140 faz a próxima faixa começar em 141.
SELECT setval('api_user_seq', (SELECT max(id) FROM (
    SELECT id FROM api_alunos
    UNION ALL SELECT id FROM api_professores
    UNION ALL SELECT id FROM api_funcionarios
) t) + 50);

-- Verificações finais: nenhuma colisão entre subclasses e nenhuma FK órfã.
SELECT 'alunos_professores' AS par, count(*) AS ids_colidindo
FROM (SELECT id FROM api_alunos INTERSECT SELECT id FROM api_professores) ids
UNION ALL
SELECT 'alunos_funcionarios', count(*)
FROM (SELECT id FROM api_alunos INTERSECT SELECT id FROM api_funcionarios) ids
UNION ALL
SELECT 'professores_funcionarios', count(*)
FROM (SELECT id FROM api_professores INTERSECT SELECT id FROM api_funcionarios) ids;

CREATE TEMP TABLE tmp_fk_orphan_checks (
    child_table text,
    child_column text,
    parent_table text,
    orphan_count bigint
) ON COMMIT DROP;
DO $$
DECLARE fk record;
BEGIN
    FOR fk IN SELECT * FROM tmp_user_id_fks LOOP
        EXECUTE format(
            'INSERT INTO tmp_fk_orphan_checks '
            || 'SELECT %L, %L, %L, count(*) FROM %I.%I child '
            || 'LEFT JOIN %I.%I parent ON parent.id = child.%I '
            || 'WHERE child.%I IS NOT NULL AND parent.id IS NULL',
            fk.child_table, fk.child_column, fk.parent_table,
            fk.child_schema, fk.child_table, fk.parent_schema, fk.parent_table,
            fk.child_column, fk.child_column
        );
    END LOOP;
END
$$;

SELECT * FROM tmp_fk_orphan_checks ORDER BY parent_table, child_table, child_column;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM tmp_fk_orphan_checks WHERE orphan_count <> 0) THEN
        RAISE EXCEPTION 'Foram encontradas FKs órfãs após a renumeração';
    END IF;
END
$$;

COMMIT;
