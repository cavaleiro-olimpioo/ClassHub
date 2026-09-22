# API Routes Used by Frontend

This document lists all API endpoints the frontend consumes, with HTTP methods, query parameters, request payloads and expected response fields inferred from the frontend code.

Base URL: `http://localhost:8080/api` (defined in `frontend/assets/js/api.js`)

---

## Authentication

- POST `/auth/login`
  - Purpose: Authenticate user and receive token + profile
  - Payload:
    - `email` (string)
    - `senha` (string)
  - Response (inferred):
    - `token` (string)
    - `perfil` (string) e.g. `ADMIN`, `PROFESSOR`, `ALUNO`
    - `vinculoId` (string|number) optional — identifier used as session `vinculoId`
    - `nome` (string) optional

- POST `/auth/recuperar-senha`
  - Purpose: Trigger password recovery email
  - Payload:
    - `email` (string)
  - Response: generic success message

---

## Users / People

- GET `/alunos` or `/alunos?turmaId={id}` or `/alunos?nome={term}`
  - Purpose: list or filter students
  - Query params: `turmaId`, `nome`
  - Response item fields used in frontend:
    - `id`, `nome`, `email`, `matricula`, `dataNascimento`, `turmaId`, `turma` (object), `turmaNome`

- GET `/alunos/{id}`
  - Purpose: fetch single student details
  - Response fields: at least `id`, `nome`, `email`, `matricula`, `dataNascimento`, `turmaId`, `turma`

- POST `/alunos`
  - Purpose: create student
  - Payload:
    - `nome` (string)
    - `email` (string)
    - `matricula` (string)
    - `dataNascimento` (YYYY-MM-DD)
    - `turmaId` (string|number|null)

- PUT `/alunos/{id}`
  - Purpose: update student
  - Payload: same as POST `/alunos`

- PUT `/alunos/{id}/turma`
  - Purpose: assign/unassign student to a turma
  - Payload:
    - `turmaId` (string|number|null)

- GET `/professores`
  - Purpose: list teachers
  - Response item fields: `id`, `nome`, `email`

- POST `/professores`
  - Payload:
    - `nome` (string)
    - `email` (string)

- PUT `/professores/{id}`
  - Payload: same as POST `/professores`

---

## Academic Structure

- GET `/series`
  - Purpose: list series/grades
  - Response items: `id`, `nome` (used in turma presentation)

- GET `/turmas`
  - Purpose: list turmas
  - Response items: `id`, `nome`, `serie` or `serieId`, `anoLetivo`

- POST `/turmas`
  - Payload:
    - `nome` (string)
    - `serieId` (string|number)
    - `anoLetivo` (number)

- PUT `/turmas/{id}`
  - Payload: same as POST `/turmas`

- GET `/disciplinas`
  - Purpose: list subjects
  - Response items: `id`, `nome`, `cargaHoraria`

- POST `/disciplinas`
  - Payload:
    - `nome` (string)
    - `cargaHoraria` (number)

- PUT `/disciplinas/{id}`
  - Payload: same as POST `/disciplinas`

---

## Vínculos (Teacher assignment)

- GET `/vinculos` or `/vinculos?professorId={id}`
  - Purpose: list assignments between professor-disciplina-turma
  - Response items used: `id`, `professorId`, `professor` (object), `turmaId`, `turma` (object), `disciplinaId`, `disciplina` (object), `anoLetivo`

- POST `/vinculos`
  - Payload:
    - `professorId` (string|number)
    - `turmaId` (string|number)
    - `disciplinaId` (string|number)
    - `anoLetivo` (number)

- DELETE `/vinculos/{id}`
  - Purpose: remove vínculo

---

## Horários

- GET `/horarios/turma/{turmaId}`
  - Purpose: list schedule entries for a turma
  - Response item fields: `id`, `turmaId`, `disciplinaId`, `disciplinaNome` or `disciplina` object, `professorId`, `professorNome` or `professor` object, `diaSemana` (number 1-5), `horaInicio` (HH:mm), `horaFim` (HH:mm)

- GET `/horarios/professor/{professorId}`
  - Purpose: list schedule for professor

- POST `/horarios`
  - Payload:
    - `turmaId` (string|number)
    - `diaSemana` (number 1-5)
    - `horaInicio` (string HH:mm)
    - `horaFim` (string HH:mm)
    - `disciplinaId` (string|number)
    - `professorId` (string|number)
  - Notes: frontend handles 409 conflict when server responds with `err.status === 409`.

- DELETE `/horarios/{id}`

---

## Presenças / Frequência

- GET `/presencas` with query params:
  - `?alunoId={id}` — used in aluno dashboard & faltas
  - `?turmaId={id}&disciplinaId={id}&data={YYYY-MM-DD}` — used by chamada to fetch presenças do dia
  - `?turmaId={id}` — possibility used

  - Response item fields: `id`, `alunoId`, `turmaId`, `disciplinaId`, `data` (YYYY-MM-DD), `status` (`PRESENTE`|`FALTA`|`FALTA_JUSTIFICADA`), `justificativa`

- POST `/presencas/lote`
  - Purpose: save batch of presenças
  - Payload: array of entries, each:
    - `alunoId`, `turmaId`, `disciplinaId`, `data`, `status`, `justificativa` (nullable)

---

## Notas & Boletins

- GET `/notas/aluno/{alunoId}`
  - Purpose: list student's grades
  - Response fields: `id`, `alunoId`, `disciplinaId`, `disciplinaNome`, `bimestre` (number), `tipo` (string), `peso` (number), `valor` (number)

- GET `/notas?turmaId={id}&disciplinaId={id}&bimestre={n}`
  - Purpose: get notes for turma/disciplina/bimestre (used in professor's screen)

- GET `/notas?turmaId={id}` or other filters (general)

- POST `/notas`
  - Payload:
    - `alunoId`, `disciplinaId`, `turmaId`, `bimestre` (number), `tipo` (string e.g. `PROVA`), `valor` (number), `peso` (number)

- PUT `/notas/{id}`
  - Payload: same as POST `/notas`

- POST `/boletins/gerar`
  - Purpose: close bimestre and generate boletins
  - Payload:
    - `anoLetivo` (number)
    - `bimestre` (number)
    - `turmaId` (string|number) optional
  - Response: `mensagem` and/or summary object

- GET `/boletins/aluno/{alunoId}?bimestre={n}`
  - Purpose: fetch consolidated boletim for an aluno and bimestre

- GET `/boletins/aluno/{alunoId}/pdf?bimestre={n}` (download)
  - Used via `api.downloadPdf(...)` to fetch a PDF blob

---

## Ocorrências

- GET `/ocorrencias` or filtered lists
  - Response item fields used: `id`, `alunoId`, `aluno` (object), `tipo`, `descricao`, `status`

- POST `/ocorrencias`
  - Payload:
    - `alunoId` (string|number)
    - `tipo` (string) e.g. `DISCIPLINAR`, `PEDAGOGICO`, `OUTRO`
    - `descricao` (string)

---

## Achados e Perdidos

- GET `/achados-perdidos` and filters like `/achados-perdidos?categoria={C}&status={S}`
  - Response fields: `id`, `descricao`, `categoria`, `localEncontrado`, `data`, `status`

- POST `/achados-perdidos`
  - Payload:
    - `descricao` (string)
    - `categoria` (string: `UNIFORME|MATERIAL|ELETRONICO|OUTRO`)
    - `data` (YYYY-MM-DD)
    - `localEncontrado` (string)

- PUT `/achados-perdidos/{id}`
  - Payload: same as POST

- PUT `/achados-perdidos/{id}/devolver`
  - Purpose: mark as devolvido; payload: `{}` (empty)

- DELETE `/achados-perdidos/{id}`

---

## Calendário Escolar

- GET `/calendario?anoLetivo={ano}&mes={m}`
  - Response fields: `id`, `data` (YYYY-MM-DD), `anoLetivo`, `tipo` (`LETIVO`|`FERIADO`|`RECESSO`|`EVENTO`), `titulo`, `descricao`

- POST `/calendario`
  - Payload:
    - `data` (YYYY-MM-DD)
    - `anoLetivo` (number)
    - `tipo` (string)
    - `titulo` (string)
    - `descricao` (string|null)

- PUT `/calendario/{id}`
  - Payload: same as POST `/calendario`

- DELETE `/calendario/{id}`

---

## Export / Files

- GET `/boletins/aluno/{alunoId}/pdf?bimestre={n}` returns PDF blob

---

## Notes / Observations

- The frontend expects JSON responses and sends JSON payloads by default (unless FormData)
- Authorization: Bearer token in `Authorization` header; token saved in `sessionStorage` under `token`
- Several endpoints accept pagination-style responses (some pages access `response.content || response`), but the frontend often handles either arrays or `{ content: [...] }` shapes.
- Error handling: frontend inspects `err.status` in some places (e.g. `409`), and expects `mensagem` or `erro` string fields in error payloads.

---

If you want, I can also:
- Add this file to `README.md` or append a short summary there.
- Produce a JSON version `api_routes.json` for programmatic use.

