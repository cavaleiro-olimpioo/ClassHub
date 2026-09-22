/**
 * Componentes de Interface Reutilizáveis (Toast, Modal, Tabela, Badges, Loading, Empty State)
 */

const UI = {
  /**
   * Exibe notificação no canto inferior direito
   * @param {string} message
   * @param {'success'|'error'|'warning'|'info'} type
   * @param {number} duration em ms
   */
  toast(message, type = 'info', duration = 4000) {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    const icons = {
      success: '✓',
      error: '✕',
      warning: '⚠',
      info: 'ℹ'
    };

    toast.innerHTML = `
      <div style="font-weight: bold; margin-right: 4px;">${icons[type] || 'ℹ'}</div>
      <div class="toast-content">${message}</div>
      <button class="toast-close" onclick="this.parentElement.remove()">✕</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      if (toast.parentNode) {
        toast.remove();
      }
    }, duration);
  },

  /**
   * Abre um modal genérico
   * @param {Object} options
   * @param {string} options.title - Título do modal
   * @param {string|HTMLElement} options.content - Conteúdo HTML ou nó do DOM
   * @param {Array} [options.actions] - Botões do footer [{ label, class, onClick }]
   * @param {'normal'|'large'} [options.size] - Tamanho
   */
  modal({ title, content, actions = [], size = 'normal' }) {
    // Remove qualquer modal ativo anterior
    this.closeModal();

    const backdrop = document.createElement('div');
    backdrop.id = 'active-modal';
    backdrop.className = 'modal-backdrop';

    const modalDialog = document.createElement('div');
    modalDialog.className = `modal-dialog ${size === 'large' ? 'modal-lg' : ''}`;

    // Header
    const header = document.createElement('div');
    header.className = 'modal-header';
    header.innerHTML = `
      <h3 class="modal-title">${title}</h3>
      <button class="modal-close" onclick="UI.closeModal()">&times;</button>
    `;

    // Body
    const body = document.createElement('div');
    body.className = 'modal-body';
    if (typeof content === 'string') {
      body.innerHTML = content;
    } else if (content instanceof HTMLElement) {
      body.appendChild(content);
    }

    // Footer
    const footer = document.createElement('div');
    footer.className = 'modal-footer';

    if (actions.length === 0) {
      const closeBtn = document.createElement('button');
      closeBtn.className = 'btn btn-secondary';
      closeBtn.textContent = 'Fechar';
      closeBtn.onclick = () => this.closeModal();
      footer.appendChild(closeBtn);
    } else {
      actions.forEach(action => {
        const btn = document.createElement('button');
        btn.className = `btn ${action.class || 'btn-secondary'}`;
        btn.textContent = action.label;
        btn.onclick = (e) => {
          if (action.onClick) action.onClick(e, this);
        };
        footer.appendChild(btn);
      });
    }

    modalDialog.appendChild(header);
    modalDialog.appendChild(body);
    modalDialog.appendChild(footer);
    backdrop.appendChild(modalDialog);

    document.body.appendChild(backdrop);

    // Animação de entrada
    requestAnimationFrame(() => {
      backdrop.classList.add('show');
    });

    return backdrop;
  },

  /**
   * Fecha o modal ativo
   */
  closeModal() {
    const backdrop = document.getElementById('active-modal');
    if (backdrop) {
      backdrop.classList.remove('show');
      setTimeout(() => backdrop.remove(), 200);
    }
  },

  /**
   * Modal de confirmação padrão
   */
  confirm({ title = 'Confirmação', message = 'Tem certeza que deseja prosseguir?', confirmText = 'Confirmar', cancelText = 'Cancelar', onConfirm }) {
    this.modal({
      title,
      content: `<p style="font-size: 1rem; color: var(--text-main);">${message}</p>`,
      actions: [
        { label: cancelText, class: 'btn-secondary', onClick: () => this.closeModal() },
        {
          label: confirmText,
          class: 'btn-danger',
          onClick: async (e) => {
            this.closeModal();
            if (onConfirm) await onConfirm();
          }
        }
      ]
    });
  },

  /**
   * Gera HTML de Tabela a partir de dados
   * @param {Array<Object>} columns - [{ key: 'nome', label: 'Nome', render?: (val, row) => string }]
   * @param {Array<Object>} data - Lista de registros
   * @param {string} [emptyMsg] - Mensagem se data for vazio
   */
  renderTable(columns, data, emptyMsg = 'Nenhum registro encontrado.') {
    if (!data || data.length === 0) {
      return this.renderEmptyState('Sem registros', emptyMsg);
    }

    let html = `
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              ${columns.map(col => `<th>${col.label}</th>`).join('')}
            </tr>
          </thead>
          <tbody>
            ${data.map(row => `
              <tr>
                ${columns.map(col => {
                  let val = row[col.key];
                  if (col.render) {
                    val = col.render(val, row);
                  } else if (val === null || val === undefined) {
                    val = '-';
                  }
                  return `<td>${val}</td>`;
                }).join('')}
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
    return html;
  },

  /**
   * HTML de Badge formatada por tipo/status
   */
  badge(status) {
    if (!status) return '-';
    const cleanStatus = String(status).toUpperCase();
    const classMap = {
      // Presença
      'PRESENTE': 'badge-presente',
      'FALTA': 'badge-falta',
      'FALTA_JUSTIFICADA': 'badge-falta_justificada',
      // Ocorrência
      'ABERTA': 'badge-aberta',
      'ENCERRADA': 'badge-encerrada',
      // Achados e perdidos
      'NAO_REIVINDICADO': 'badge-nao_reivindicado',
      'DEVOLVIDO': 'badge-devolvido',
      // Boletim / Média
      'APROVADO': 'badge-aprovado',
      'RECUPERACAO': 'badge-recuperacao',
      'REPROVADO': 'badge-reprovado',
      // Calendário
      'LETIVO': 'badge-letivo',
      'FERIADO': 'badge-feriado',
      'RECESSO': 'badge-recesso',
      'EVENTO': 'badge-evento'
    };

    const labelMap = {
      'PRESENTE': 'Presente',
      'FALTA': 'Falta',
      'FALTA_JUSTIFICADA': 'Falta Justificada',
      'ABERTA': 'Aberta',
      'ENCERRADA': 'Encerrada',
      'NAO_REIVINDICADO': 'Não Reivindicado',
      'DEVOLVIDO': 'Devolvido',
      'APROVADO': 'Aprovado',
      'RECUPERACAO': 'Recuperação',
      'REPROVADO': 'Reprovado',
      'LETIVO': 'Dia Letivo',
      'FERIADO': 'Feriado',
      'RECESSO': 'Recesso',
      'EVENTO': 'Evento'
    };

    const cssClass = classMap[cleanStatus] || 'badge-secondary';
    const label = labelMap[cleanStatus] || cleanStatus;

    return `<span class="badge ${cssClass}">${label}</span>`;
  },

  /**
   * Exibe indicativo de carregamento em um elemento container
   */
  renderLoading(message = 'Carregando dados...') {
    return `
      <div class="loading-container">
        <div class="loading-spinner"></div>
        <span>${message}</span>
      </div>
    `;
  },

  /**
   * Exibe estado vazio em um elemento container
   */
  renderEmptyState(title = 'Nenhum item encontrado', subtitle = 'Não existem registros para exibir no momento.') {
    return `
      <div class="empty-state">
        <div class="empty-state-icon">📂</div>
        <div class="empty-state-title">${title}</div>
        <div class="empty-state-subtitle">${subtitle}</div>
      </div>
    `;
  },

  /**
   * Formata nota de 0 a 10 com 2 casas decimais e vírgula
   */
  formatGrade(value) {
    if (value === null || value === undefined || isNaN(value)) return '-';
    return Number(value).toFixed(2).replace('.', ',');
  },

  /**
   * Formata data YYYY-MM-DD para DD/MM/YYYY
   */
  formatDate(dateStr) {
    if (!dateStr) return '-';
    const parts = dateStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}/${parts[0]}`;
    }
    return dateStr;
  }
};

window.UI = UI;
