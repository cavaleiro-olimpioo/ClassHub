# API Routes

Este arquivo descreve a API ativa em `api/src/main/java/com/classhub/api/Controller`. O cliente React atual está em `frontend/src/lib/api.js`; a referência a `frontend/assets/js/api.js` da versão antiga foi removida.

## Base e integração

O navegador chama `/api/...`. O proxy do Vite (`frontend/vite.config.js`) e o Nginx (`frontend/nginx.conf`) removem `/api` antes de encaminhar a chamada para o Spring Boot. O backend também mapeia controladores com e sem o prefixo `/api`.

As requisições autenticadas usam `Authorization: Bearer <token>`. O login e a saúde da API são públicos. Listas são retornadas como arrays; erros usam `{ "mensagem": "..." }`.

## Rotas

| Grupo | Métodos e caminhos |
| --- | --- |
| Sessão | `POST /auth/login`; `POST /auth/recuperar-senha` (retorna `501` enquanto não houver serviço de recuperação) |
| Saúde | `GET /health` |
| Diretório | `GET/POST /alunos`; `GET/PUT/DELETE /alunos/{id}`; `PUT /alunos/{id}/turma`; CRUD de `/professores`, `/series`, `/turmas`, `/disciplinas`; `GET/POST /vinculos`; `DELETE /vinculos/{id}` |
| Horários | `GET /horarios/turma/{turmaId}`; `GET /horarios/professor/{professorId}`; `POST /horarios`; `DELETE /horarios/{id}` |
| Presenças | `GET /presencas`; `POST /presencas/lote` |
| Notas | `GET /notas`; `GET /notas/aluno/{alunoId}`; `POST /notas`; `PUT/DELETE /notas/{id}` |
| Boletins | `POST /boletins/gerar`; `GET /boletins/aluno/{alunoId}?bimestre={n}`; `GET /boletins/aluno/{alunoId}/pdf?bimestre={n}` |
| Comunidade | CRUD de `/ocorrencias`, `/achados-perdidos` e `/calendario`; ações de encerramento e devolução estão em `/ocorrencias/{id}/encerrar` e `/achados-perdidos/{id}/devolver` |

Os campos obrigatórios e validações dos corpos JSON estão definidos nos records de `api/src/main/java/com/classhub/api/dto/ApiDtos.java`. As regras de perfil ficam nos controllers e em `api/src/main/java/com/classhub/api/Config/SecurityConfig.java`.

O corpo de `POST /boletins/gerar` inclui `anoLetivo`, `bimestre`, `dataInicio`, `dataFim` e, opcionalmente, `turmaId`. As datas são necessárias para calcular a frequência do bimestre e precisam estar no ano letivo informado.
