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

/** Decodifica a carga util (payload) de um JWT sem validar a assinatura. */
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

/** Le claims do token: { sub, perfil, email, iat, exp } */
export function readClaims() {
  const token = getToken();
  if (!token) return null;
  return decodeJwtPayload(token);
}

/** Indica se o token expirou (com pequena folga de segurança). */
export function isTokenExpired(token = getToken()) {
  const claims = decodeJwtPayload(token);
  if (!claims || !claims.exp) return false;
  return claims.exp * 1000 <= Date.now() + 5000;
}

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

export function clearSession() {
  setToken(null);
  try {
    sessionStorage.clear();
  } catch {
    /* ignore */
  }
}

export async function login(email, senha) {
  const response = await api.post('/auth/login', { email, senha });
  if (!response || !response.token) {
    throw new Error('Resposta de login invalida do servidor.');
  }
  return setSession({ ...response, email });
}

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
