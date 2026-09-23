# Frontend - Sistema de Gerenciamento Escolar

Este é o frontend completo do Sistema de Gerenciamento Escolar para Ensino Fundamental (1º ao 9º ano), desenvolvido utilizando **HTML5, CSS3 e JavaScript ES6+ puros (Vanilla JS)**, sem dependências de frameworks ou bibliotecas externas.

## 🚀 Como Executar Localmente

### Pré-requisitos
- Um servidor estático para servir a pasta `frontend/`. Pode ser executado via Node.js (`npx serve`), Python, ou extensões de código como Live Server no VS Code.

### Passos para Execução
1. Abra um terminal e navegue até a raiz do projeto ou diretório `frontend/`.
2. Inicie o servidor estático. Exemplo com `npx serve`:
   ```bash
npx serve frontend -p 5173
```
   Ou com Python 3:
   ```bash
   python3 -m http.server 5173 --directory frontend
   ```
3. Abra o navegador no endereço: `http://localhost:5173`

## ⚙️ Configuração da URL da API Backend

Por padrão, a aplicação serve o frontend em `http://localhost:5173` e usa a API do backend em `http://localhost:5173/api` quando a aplicação é executada por proxy local.

Se precisar alterar a URL base da API, defina a variável `API_BASE_URL` no objeto global `window` antes de carregar o script `api.js` ou altere a constante em `frontend/assets/js/api.js`:

```javascript
const API_BASE_URL = window.API_BASE_URL || 'http://localhost:5173/api';

## 📁 Estrutura do Projeto

- `index.html`: Tela de Login e redirecionamento por perfil (`ADMIN`, `PROFESSOR`, `ALUNO`).
- `recuperar-senha.html`: Solicitação de recuperação de senha.
- `admin/`: Telas e funcionalidades administrativas (CRUD de Alunos, Professores, Séries, Turmas, Horários, Calendário, Bimestres, etc.).
- `professor/`: Telas do perfil Professor (Chamada, Lançamento de Notas, Ocorrências, Alunos e Horários).
- `aluno/`: Telas do perfil Aluno (Dashboard, Notas, Faltas, Boletim, Horários, Calendário, Ocorrências, Achados e Perdidos).
- `assets/css/styles.css`: Estilos globais responsivos, temas e componentes de UI baseados em variáveis CSS.
- `assets/js/`:
  - `api.js`: Wrapper de requisições HTTP (`fetch`) com autenticação JWT e tratamento centralizado de erros.
  - `auth.js`: Gerenciamento de sessão e guarda de rotas por perfil.
  - `ui.js`: Componentes genéricos de UI (Toasts, Modais, Tabelas, Badges e Estados).
