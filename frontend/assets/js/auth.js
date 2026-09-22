/**
 * Guarda de Rotas, Gerenciamento de Sessão e Renderização do Layout
 */

const Auth = {
  /**
   * Obtém a sessão atual
   */
  getSession() {
    const token = sessionStorage.getItem('token');
    const perfil = sessionStorage.getItem('perfil');
    const vinculoId = sessionStorage.getItem('vinculoId');
    const nome = sessionStorage.getItem('nome') || 'Usuário';

    if (!token || !perfil) {
      return null;
    }

    return { token, perfil, vinculoId, nome };
  },

  /**
   * Salva os dados de login no sessionStorage
   */
  setSession(data) {
    sessionStorage.setItem('token', data.token);
    sessionStorage.setItem('perfil', data.perfil);
    if (data.vinculoId) {
      sessionStorage.setItem('vinculoId', data.vinculoId);
    } else {
      sessionStorage.removeItem('vinculoId');
    }
    if (data.nome) {
      sessionStorage.setItem('nome', data.nome);
    }
  },

  /**
   * Efetua o Logout
   */
  logout() {
    sessionStorage.clear();
    window.location.href = '/index.html';
  },

  /**
   * Verifica permissões da página atual
   */
  initRouteGuard() {
    const path = window.location.pathname;

    // Páginas públicas
    if (path.endsWith('index.html') || path.endsWith('recuperar-senha.html') || path === '/') {
      const session = this.getSession();
      if (session) {
        this.redirectToDashboard(session.perfil);
      }
      return;
    }

    const session = this.getSession();
    if (!session) {
      window.location.href = '/index.html';
      return;
    }

    // Validação por perfil de acesso
    if (path.includes('/admin/') && session.perfil !== 'ADMIN') {
      this.redirectToDashboard(session.perfil);
      return;
    }

    if (path.includes('/professor/') && session.perfil !== 'PROFESSOR') {
      this.redirectToDashboard(session.perfil);
      return;
    }

    if (path.includes('/aluno/') && session.perfil !== 'ALUNO') {
      this.redirectToDashboard(session.perfil);
      return;
    }

    // Renderiza a Sidebar e Header se a estrutura do layout estiver presente
    document.addEventListener('DOMContentLoaded', () => {
      this.renderAppShell(session);
    });
  },

  /**
   * Redireciona para o Dashboard correto com base no perfil
   */
  redirectToDashboard(perfil) {
    switch (perfil) {
      case 'ADMIN':
        window.location.href = '/admin/dashboard.html';
        break;
      case 'PROFESSOR':
        window.location.href = '/professor/dashboard.html';
        break;
      case 'ALUNO':
        window.location.href = '/aluno/dashboard.html';
        break;
      default:
        this.logout();
    }
  },

  /**
   * Constrói dynamicamente o Sidebar e Header para telas internas
   */
  renderAppShell(session) {
    const sidebarEl = document.getElementById('app-sidebar');
    const headerEl = document.getElementById('app-header');

    if (!sidebarEl || !headerEl) return;

    // Menu de navegação dinâmico conforme perfil
    const menuItems = this.getMenuItems(session.perfil);
    const currentPath = window.location.pathname;

    sidebarEl.innerHTML = `
      <div class="sidebar-brand">
        🎓 <span>Escola</span>Gestão
      </div>
      <nav class="sidebar-nav">
        <ul>
          ${menuItems.map(item => `
            <li>
              <a href="${item.url}" class="${currentPath.endsWith(item.url) ? 'active' : ''}">
                <span>${item.icon}</span>
                <span>${item.label}</span>
              </a>
            </li>
          `).join('')}
        </ul>
      </nav>
      <div class="sidebar-user">
        <div class="sidebar-user-info">
          <div class="sidebar-user-name">${session.nome || 'Usuário'}</div>
          <div class="sidebar-user-role">${session.perfil}</div>
        </div>
        <button class="btn btn-sm btn-secondary btn-icon" title="Sair" onclick="Auth.logout()">
          🚪
        </button>
      </div>
    `;

    // Header Superior
    headerEl.innerHTML = `
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button class="menu-toggle" onclick="Auth.toggleMobileMenu()">☰</button>
        <h1 class="header-title">${document.title || 'Sistema Escolar'}</h1>
      </div>
      <div class="header-actions">
        <span class="badge badge-secondary">${session.perfil}</span>
        <button class="btn btn-sm btn-secondary" onclick="Auth.logout()">Sair</button>
      </div>
    `;

    // Overlay para menu mobile
    let overlay = document.getElementById('sidebar-overlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'sidebar-overlay';
      overlay.className = 'sidebar-overlay';
      overlay.onclick = () => Auth.toggleMobileMenu();
      document.body.appendChild(overlay);
    }
  },

  toggleMobileMenu() {
    const sidebar = document.getElementById('app-sidebar');
    const overlay = document.getElementById('sidebar-overlay');
    if (sidebar && overlay) {
      sidebar.classList.toggle('show');
      overlay.classList.toggle('show');
    }
  },

  getMenuItems(perfil) {
    switch (perfil) {
      case 'ADMIN':
        return [
          { label: 'Dashboard', url: '/admin/dashboard.html', icon: '📊' },
          { label: 'Alunos', url: '/admin/alunos.html', icon: '👨‍🎓' },
          { label: 'Professores', url: '/admin/professores.html', icon: '👩‍🏫' },
          { label: 'Séries', url: '/admin/series.html', icon: '🏫' },
          { label: 'Turmas & Vínculos', url: '/admin/turmas.html', icon: '👥' },
          { label: 'Disciplinas', url: '/admin/disciplinas.html', icon: '📚' },
          { label: 'Horários', url: '/admin/horarios.html', icon: '⏰' },
          { label: 'Calendário', url: '/admin/calendario.html', icon: '📅' },
          { label: 'Fechamento Bimestre', url: '/admin/bimestre.html', icon: '📋' },
          { label: 'Achados e Perdidos', url: '/admin/achados-perdidos.html', icon: '🔍' }
        ];

      case 'PROFESSOR':
        return [
          { label: 'Dashboard', url: '/professor/dashboard.html', icon: '📊' },
          { label: 'Fazer Chamada', url: '/professor/chamada.html', icon: '✅' },
          { label: 'Lançar Notas', url: '/professor/notas.html', icon: '📝' },
          { label: 'Ocorrências', url: '/professor/ocorrencias.html', icon: '⚠️' },
          { label: 'Consulta Alunos', url: '/professor/alunos.html', icon: '🔍' },
          { label: 'Meus Horários', url: '/professor/horarios.html', icon: '⏰' }
        ];

      case 'ALUNO':
        return [
          { label: 'Dashboard', url: '/aluno/dashboard.html', icon: '📊' },
          { label: 'Minhas Notas', url: '/aluno/notas.html', icon: '📝' },
          { label: 'Minhas Faltas', url: '/aluno/faltas.html', icon: '❌' },
          { label: 'Meu Boletim', url: '/aluno/boletim.html', icon: '📄' },
          { label: 'Meus Horários', url: '/aluno/horarios.html', icon: '⏰' },
          { label: 'Ocorrências', url: '/aluno/ocorrencias.html', icon: '⚠️' },
          { label: 'Calendário Escolar', url: '/aluno/calendario.html', icon: '📅' },
          { label: 'Achados e Perdidos', url: '/aluno/achados-perdidos.html', icon: '🔍' }
        ];

      default:
        return [];
    }
  }
};

// Executa guarda de rotas imediatamente
Auth.initRouteGuard();
window.Auth = Auth;
