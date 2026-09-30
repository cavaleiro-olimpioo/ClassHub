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

/**
 * Define a função chamada sempre que a API responder com HTTP 401
 * (sessão expirada/token inválido), permitindo customizar o comportamento
 * (ex.: redirecionar para a tela de login). Útil também em testes.
 *
 * @param {Function} handler função sem argumentos a ser chamada em caso de 401
 */
export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

/**
 * Lê o token JWT armazenado na sessão do navegador.
 *
 * @returns {string|null} o token armazenado, ou `null` se não houver token ou o storage estiver indisponível
 */
export function getToken() {
  try {
    return sessionStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

/**
 * Salva (ou remove) o token JWT na sessão do navegador.
 *
 * @param {string|null|undefined} token token a ser armazenado; se falsy, remove o token salvo
 */
export function setToken(token) {
  try {
    if (token) sessionStorage.setItem(TOKEN_KEY, token);
    else sessionStorage.removeItem(TOKEN_KEY);
  } catch {
    /* modo privado / storage bloqueado: ignora */
  }
}

/**
 * Erro lançado quando uma requisição à API falha, contendo o status HTTP e
 * o corpo da resposta de erro (quando disponível).
 */
export class ApiError extends Error {
  /**
   * Cria um erro de API.
   *
   * @param {string} message mensagem legível do erro
   * @param {number} status código de status HTTP retornado pela API
   * @param {*} data corpo da resposta de erro (JSON ou texto), quando disponível
   */
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

/**
 * Lê o corpo textual de uma resposta HTTP e tenta interpretá-lo como JSON.
 *
 * @param {Response} response resposta HTTP a ser lida
 * @returns {Promise<*>} o objeto JSON decodificado, o texto bruto (se não for JSON válido), ou `null` se o corpo estiver vazio
 */
async function parseJson(response) {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

/**
 * Executa uma requisição HTTP à API do ClassHub, anexando automaticamente
 * o token de autenticação (quando existente), serializando o corpo em
 * JSON e tratando os casos especiais de resposta: 401 (sessão expirada),
 * download de arquivo (`responseType: 'blob'`) e 204 (sem conteúdo).
 *
 * @param {string} endpoint caminho do endpoint, relativo à base da API (ex.: "/alunos")
 * @param {object} [options] opções da requisição (método, corpo, cabeçalhos, `responseType`)
 * @returns {Promise<*>} o corpo da resposta já decodificado (objeto, blob ou `null`)
 * @throws {ApiError} se a requisição falhar ou a API retornar um status de erro
 */
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

/**
 * Monta a query string de uma URL a partir de um objeto de parâmetros,
 * ignorando valores `undefined`, `null` ou string vazia.
 *
 * @param {string} endpoint caminho base do endpoint
 * @param {object|undefined} params parâmetros a serem incluídos na query string
 * @returns {string} o endpoint com a query string anexada (se houver parâmetros válidos)
 */
function withQuery(endpoint, params) {
  if (!params) return endpoint;
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') search.append(key, value);
  });
  const qs = search.toString();
  return qs ? `${endpoint}?${qs}` : endpoint;
}

/**
 * Cliente HTTP simplificado usado em toda a aplicação para se comunicar
 * com a API do ClassHub, expondo os métodos HTTP básicos (GET, POST, PUT,
 * DELETE) e o utilitário de download de arquivos.
 */
export const api = {
  /**
   * Executa uma requisição GET.
   *
   * @param {string} endpoint caminho do endpoint
   * @param {object} [params] parâmetros de query string
   * @param {object} [options] opções adicionais da requisição
   * @returns {Promise<*>} corpo da resposta decodificado
   */
  get: (endpoint, params, options = {}) => request(withQuery(endpoint, params), { ...options, method: 'GET' }),
  /**
   * Executa uma requisição POST, enviando `body` como JSON.
   *
   * @param {string} endpoint caminho do endpoint
   * @param {*} body corpo da requisição
   * @param {object} [options] opções adicionais da requisição
   * @returns {Promise<*>} corpo da resposta decodificado
   */
  post: (endpoint, body, options = {}) => request(endpoint, { ...options, method: 'POST', body }),
  /**
   * Executa uma requisição PUT, enviando `body` como JSON.
   *
   * @param {string} endpoint caminho do endpoint
   * @param {*} body corpo da requisição
   * @param {object} [options] opções adicionais da requisição
   * @returns {Promise<*>} corpo da resposta decodificado
   */
  put: (endpoint, body, options = {}) => request(endpoint, { ...options, method: 'PUT', body }),
  /**
   * Executa uma requisição DELETE.
   *
   * @param {string} endpoint caminho do endpoint
   * @param {object} [options] opções adicionais da requisição
   * @returns {Promise<*>} corpo da resposta decodificado (geralmente `null`)
   */
  delete: (endpoint, options = {}) => request(endpoint, { ...options, method: 'DELETE' }),

  /**
   * Faz o download de um arquivo protegido (autenticado com o token
   * Bearer) e dispara o download no navegador do usuário.
   *
   * @param {string} endpoint caminho do endpoint que retorna o arquivo
   * @param {string} [filename] nome sugerido para o arquivo baixado
   * @param {object} [params] parâmetros de query string
   * @returns {Promise<void>}
   */
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
