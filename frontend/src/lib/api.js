/**
 * Camada de comunicacao com a API REST do ClassHub (Spring Boot).
 *
 * Substitui o antigo `assets/js/api.js` mantendo exatamente a mesma
 * semantica de endpoints, autenticacao e tratamento de erro, para que
 * o backend nao precise de nenhuma alteracao.
 */

const API_BASE_URL = (import.meta.env?.VITE_API_BASE_URL || '/api').replace(/\/+$/, '');

const TOKEN_KEY = 'classhub.token';

/** Permite sobrescrever o destino do redirect em 401 (ex.: testes). */
let unauthorizedHandler = null;

export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

export function getToken() {
  try {
    return sessionStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function setToken(token) {
  try {
    if (token) sessionStorage.setItem(TOKEN_KEY, token);
    else sessionStorage.removeItem(TOKEN_KEY);
  } catch {
    /* modo privado / storage bloqueado: ignora */
  }
}

export class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

/** Extrai uma mensagem legivel de qualquer formato de erro do backend. */
function extractMessage(data, status) {
  if (data && typeof data === 'object') {
    return data.mensagem || data.erro || data.error || `Erro HTTP ${status}`;
  }
  if (typeof data === 'string' && data.trim()) return data;
  return `Erro HTTP ${status}`;
}

async function parseJson(response) {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

async function request(endpoint, options = {}) {
  const token = getToken();

  const headers = {
    Accept: 'application/json',
    ...(options.headers || {})
  };

  // Nao define Content-Type em downloads de blob (GET sem body)
  if (!(options.responseType === 'blob' || options.body === undefined)) {
    headers['Content-Type'] = 'application/json';
  }

  if (token) headers.Authorization = `Bearer ${token}`;

  const config = { ...options, headers };

  if (options.body !== undefined && options.body !== null) {
    config.body = JSON.stringify(options.body);
  }

  let response;
  try {
    response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  } catch (err) {
    throw new ApiError('Erro de conexao com o servidor.', 0, null);
  }

  // Token invalido/expirado: limpa a sessao e manda para o login
  if (response.status === 401) {
    setToken(null);
    try {
      sessionStorage.clear();
    } catch {
      /* ignore */
    }
    if (unauthorizedHandler) unauthorizedHandler();
    throw new ApiError('Sessao expirada. Por favor, faca login novamente.', 401, null);
  }

  // Download de arquivo (ex.: PDF do boletim)
  if (options.responseType === 'blob') {
    if (!response.ok) {
      const data = await parseJson(response).catch(() => null);
      throw new ApiError(extractMessage(data, response.status), response.status, data);
    }
    return response.blob();
  }

  // 204 No Content (ex.: DELETE)
  if (response.status === 204) return null;

  const data = await parseJson(response).catch(() => null);

  if (!response.ok) {
    throw new ApiError(extractMessage(data, response.status), response.status, data);
  }

  return data;
}

function withQuery(endpoint, params) {
  if (!params) return endpoint;
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') search.append(key, value);
  });
  const qs = search.toString();
  return qs ? `${endpoint}?${qs}` : endpoint;
}

export const api = {
  get: (endpoint, params, options = {}) => request(withQuery(endpoint, params), { ...options, method: 'GET' }),
  post: (endpoint, body, options = {}) => request(endpoint, { ...options, method: 'POST', body }),
  put: (endpoint, body, options = {}) => request(endpoint, { ...options, method: 'PUT', body }),
  delete: (endpoint, options = {}) => request(endpoint, { ...options, method: 'DELETE' }),

  /** Faz download de um arquivo protegido usando o token Bearer. */
  async downloadFile(endpoint, filename = 'documento.pdf', params) {
    const blob = await request(withQuery(endpoint, params), { method: 'GET', responseType: 'blob' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
  }
};

export { API_BASE_URL };
