# Frontend - ClassHub

SPA do Sistema de Gerenciamento Escolar (Ensino Fundamental, 1º ao 9º ano), construída com **React 18 + Vite** e **CSS puro** com design tokens. Substitui a versão anterior em HTML/JS vanilla.

---

## 🚀 Como Executar Localmente

### Pré-requisitos
- **Node.js 18+** e **npm** instalados.

## 🚀 Como Executar Localmente

### Pré-requisitos
- Um servidor estático para servir a pasta `frontend/`. Pode ser executado via Node.js (`npx serve`), Python, ou extensões de código como Live Server no VS Code.

### Passos

```bash
cd frontend
npm install
npm run dev
```
A aplicação sobe em `http://localhost:5173`. O servidor de desenvolvimento do Vite faz proxy de `/api` para `http://localhost:8080` (mesmo comportamento do Nginx em produção); ajuste com `VITE_DEV_API_TARGET`.

### Scripts

| Comando           | Descrição                                   |
| ----------------- | ------------------------------------------- |
| `npm run dev`     | Servidor de desenvolvimento com HMR         |
| `npm run build`   | Gera os arquivos estáticos em `dist/`       |
| `npm run preview` | Serve o `dist/` localmente para conferência |

## 🐳 Docker

```bash
docker build -t classhub-frontend .
docker run -p 8080:80 classhub-frontend
```

Imagem multi-stage: build com `node:20-alpine` e serving com `nginx:alpine`.

## ⚙️ Configuração da URL da API Backend

A URL base vem da variável de ambiente `VITE_API_BASE_URL` (padrão: `/api`). Copie `.env.example` para `.env` e ajuste se necessário:

```bash
VITE_API_BASE_URL=/api
VITE_DEV_API_TARGET=http://localhost:8080
```

Em produção o próprio Nginx faz o proxy de `/api` para `http://backend:8080/`, removendo o prefixo — por isso o padrão `/api` funciona sem configuração extra.

## 📁 Estrutura do Projeto

```
src/
  main.jsx                  Ponto de entrada
  App.jsx                   Rotas + guards por perfil
  styles/index.css          Design tokens e componentes globais
  components/
    AppShell.jsx            Sidebar + topbar (layout interno)
    RequireAuth.jsx         Guarda de rotas (sessão + perfil)
    CrudPage.jsx            Página CRUD genérica (lista/modal/filtros)
    ScheduleGrid.jsx        Grade de horários somente-leitura
    ui.jsx                  Design system (Card, Table, Modal, Toast...)
    Icon.jsx                Conjunto de ícones SVG
    ToastProvider.jsx       Notificações (substitui `UI.toast`)
  lib/
    api.js                  Cliente HTTP (substitui `assets/js/api.js`)
    session.js              Sessão, JWT e resolução do vinculoId
    format.js               Formatadores, status e regras de negócio
    menu.js                 Menu lateral por perfil
    useAluno.js             Contexto do aluno logado
    useProfessorVinculos.js Vínculos do professor logado
    useReference.js         Listas de apoio (turmas, séries...)
  pages/
    Login.jsx  RecuperarSenha.jsx
    admin/       (10 telas)     professor/  (6 telas)     aluno/  (8 telas)
```

## 🔐 Sobre o `vinculoId`

O endpoint `POST /auth/login` devolve apenas `{ token, perfil, nome }` — **sem** `vinculoId`. Como `ApiAluno` e `ApiProfessor` estendem `ApiUser` com `TABLE_PER_CLASS`, o claim `sub` do JWT **é** o id do aluno/professor. Por isso `src/lib/session.js` decodifica o token no cliente para recuperar esse vínculo, necessário para `/vinculos`, `/notas/aluno/{id}` e `/presencas`. Nenhuma alteração no backend é necessária.

## 🔑 Usuários de demonstração

| Perfil      | E-mail                       | Senha             |
| ----------- | ---------------------------- | ----------------- |
| Professor   | `professor@classhub.local`   | `professor123`    |
| Aluno       | `aluno@classhub.local`       | `aluno123`        |
| Funcionário | `funcionario@classhub.local` | `funcionario123`  |
