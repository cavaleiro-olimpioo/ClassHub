/**
 * Wrapper de Fetch para comunicação com a API REST do Sistema Escolar
 */

const API_BASE_URL = window.API_BASE_URL || 'http://localhost:5173/api';

const api = {
  /**
   * Executa uma requisição HTTP
   * @param {string} endpoint - Ex: '/alunos'
   * @param {Object} options - Opções do fetch (method, body, headers, etc)
   */
  async request(endpoint, options = {}) {
    const token = sessionStorage.getItem('token');

    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...options.headers
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
      config.body = JSON.stringify(options.body);
    }

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

      // Trata erro 401 (Não autorizado ou token expirado)
      if (response.status === 401) {
        sessionStorage.clear();
        if (!window.location.pathname.endsWith('index.html') && !window.location.pathname.endsWith('recuperar-senha.html') && window.location.pathname !== '/') {
          window.location.href = '/index.html?expired=true';
        }
        throw new Error('Sessão expirada. Por favor, faça login novamente.');
      }

      // Download de arquivos ou blob (ex: PDF de boletim)
      if (options.responseType === 'blob') {
        if (!response.ok) {
          const errorJson = await response.json().catch(() => ({}));
          const msg = errorJson.mensagem || `Erro na requisição: ${response.status}`;
          if (window.UI) window.UI.toast(msg, 'error');
          throw new Error(msg);
        }
        return await response.blob();
      }

      // No content response (ex: 204)
      if (response.status === 204) {
        return null;
      }

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        const errorMsg = data?.mensagem || data?.erro || `Erro HTTP ${response.status}`;
        if (window.UI) {
          window.UI.toast(errorMsg, 'error');
        }
        const errorObj = new Error(errorMsg);
        errorObj.status = response.status;
        errorObj.data = data;
        throw errorObj;
      }

      return data;
    } catch (err) {
      // Se não houver tratamento prévio ou erro de rede
      if (!err.status && window.UI) {
        window.UI.toast(err.message || 'Erro de conexão com o servidor.', 'error');
      }
      throw err;
    }
  },

  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  },

  post(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'POST', body });
  },

  put(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'PUT', body });
  },

  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  },

  /**
   * Baixa um arquivo PDF diretamente usando token Bearer
   */
  async downloadPdf(endpoint, filename = 'documento.pdf') {
    try {
      const blob = await this.request(endpoint, { method: 'GET', responseType: 'blob' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Erro ao baixar arquivo:', err);
    }
  }
};

window.api = api;
