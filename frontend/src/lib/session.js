/**
 * Sessao do usuario: login, persistencia, guarda de rotas e perfil.
 *
 * IMPORTANTE (correcao em relacao a versao estatica anterior):
 * o endpoint `POST /auth/login` devolve apenas `{ token, perfil, nome }`
 * — NAO devolve `vinculoId`. As telas de professor e aluno dependem desse
 * id para chamar `/vinculos`, `/notas/aluno/{id}`, `/presencas` etc.
 *
 * Como `ApiAluno` e `ApiProfessor` estendem `ApiUser` com
 * `TABLE_PER_CLASS`, o `sub` do JWT e exatamente o id do aluno/professor.
 * Portanto decodificamos o payload do token no cliente para recuperar
 * esse vinculo, sem alterar o backend.
 */

import { api, setToken, getToken } from './api.js';

const KEYS = {
  token: 'classhub.token',
  perfil: 'classhub.perfil',
  nome: 'classhub.nome',
  email: 'classhub.email',
  vinculoId: 'classhub.vinculoId'
};

/**
 * Decodifica a carga útil (payload) de um token JWT, sem validar a
 * assinatura (a validação de fato é feita pelo backend).
 *
 * @param {string} token token JWT completo (cabeçalho.payload.assinatura)
 * @returns {object|null} o payload decodificado, ou `null` se o token for inválido
 */
export function decodeJwtPayload(token) {
  try {
    const parts = String(token).split('.');
    if (parts.length < 2) return null;
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
    const json = decodeURIComponent(
      atob(padded)
        .split('')
        .map((c) => `%${c.charCodeAt(0).toString(16).padStart(2, '0')}`)
        .join('')
    );
    return JSON.parse(json);
  } catch {
    return null;
  }
}

/**
 * Lê as claims (dados) do token JWT armazenado na sessão atual.
 *
 * @returns {{sub: string, perfil: string, email: string, iat: number, exp: number}|null} as claims do token, ou `null` se não houver sessão
 */
export function readClaims() {
  const token = getToken();
  if (!token) return null;
  return decodeJwtPayload(token);
}

/**
 * Indica se o token informado (ou o da sessão atual) já expirou,
 * considerando uma pequena folga de segurança de 5 segundos.
 *
 * @param {string} [token] token a ser verificado (padrão: token da sessão atual)
 * @returns {boolean} `true` se o token estiver expirado (ou próximo de expirar)
 */
export function isTokenExpired(token = getToken()) {
  const claims = decodeJwtPayload(token);
  if (!claims || !claims.exp) return false;
  return claims.exp * 1000 <= Date.now() + 5000;
}

/**
 * Monta o objeto de sessão do usuário atual, combinando os dados
 * armazenados em `sessionStorage` com as claims do token JWT (que têm
 * prioridade, por serem a fonte da verdade).
 *
 * @returns {{token: string, perfil: string, vinculoId: string|null, email: string|null, nome: string}|null} a sessão atual, ou `null` se não houver usuário autenticado
 */
export function getSession() {
  const token = getToken();
  if (!token) return null;

  let perfil = null;
  let vinculoId = null;
  let email = null;
  try {
    perfil = sessionStorage.getItem(KEYS.perfil);
    vinculoId = sessionStorage.getItem(KEYS.vinculoId);
    email = sessionStorage.getItem(KEYS.email);
  } catch {
    /* storage bloqueado */
  }

  // O token e a fonte da verdade: claims tem prioridade sobre o storage.
  const claims = decodeJwtPayload(token);
  if (claims) {
    if (claims.perfil) perfil = claims.perfil;
    if (claims.sub) vinculoId = String(claims.sub);
    if (claims.email) email = claims.email;
  }

  if (!perfil) return null;

  let nome = null;
  try {
    nome = sessionStorage.getItem(KEYS.nome);
  } catch {
    /* ignore */
  }

  return { token, perfil, vinculoId, email, nome: nome || (email ? email.split('@')[0] : 'Usuario') };
}

/**
 * Persiste os dados de sessão do usuário (token, perfil, nome, e-mail) no
 * `sessionStorage`, extraindo dados complementares das claims do token
 * (como o identificador do vínculo, extraído de `sub`).
 *
 * @param {{token: string, perfil?: string, nome?: string, email?: string}} params dados de sessão a serem salvos
 * @returns {{perfil: string, nome: string, email: string, vinculoId: string|null}} os dados efetivamente salvos
 */
export function setSession({ token, perfil, nome, email }) {
  setToken(token);
  const claims = decodeJwtPayload(token) || {};

  const data = {
    perfil: perfil || claims.perfil,
    nome: nome || claims.nome,
    email: email || claims.email,
    vinculoId: claims.sub ? String(claims.sub) : null
  };

  try {
    if (data.perfil) sessionStorage.setItem(KEYS.perfil, data.perfil);
    if (data.nome) sessionStorage.setItem(KEYS.nome, data.nome);
    if (data.email) sessionStorage.setItem(KEYS.email, data.email);
    if (data.vinculoId) sessionStorage.setItem(KEYS.vinculoId, data.vinculoId);
  } catch {
    /* ignore */
  }

  return data;
}

/**
 * Remove o token e todos os dados de sessão armazenados, efetivamente
 * deslogando o usuário do navegador.
 */
export function clearSession() {
  setToken(null);
  try {
    sessionStorage.clear();
  } catch {
    /* ignore */
  }
}

/**
 * Autentica o usuário na API com e-mail e senha e, em caso de sucesso,
 * persiste a sessão resultante.
 *
 * @param {string} email e-mail do usuário
 * @param {string} senha senha do usuário
 * @returns {Promise<object>} os dados de sessão salvos (ver {@link setSession})
 * @throws {Error} se a resposta da API não contiver um token válido
 */
export async function login(email, senha) {
  const response = await api.post('/auth/login', { email, senha });
  if (!response || !response.token) {
    throw new Error('Resposta de login invalida do servidor.');
  }
  return setSession({ ...response, email });
}

/**
 * Determina a rota do painel inicial (dashboard) de acordo com o perfil do usuário.
 *
 * @param {string} perfil perfil do usuário (ex.: "ADMIN", "PROFESSOR", "ALUNO")
 * @returns {string} o caminho da rota correspondente, ou "/login" se o perfil for desconhecido
 */
export function dashboardPathFor(perfil) {
  switch (perfil) {
    case 'ADMIN':
      return '/admin';
    case 'PROFESSOR':
      return '/professor';
    case 'ALUNO':
      return '/aluno';
    default:
      return '/login';
  }
}
