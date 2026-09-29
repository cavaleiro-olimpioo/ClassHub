/**
 * Menu lateral por perfil + labels de cabecalho.
 * Substitui o `Auth.getMenuItems()` do antigo `assets/js/auth.js`.
 */

/** Título da seção exibido no topo do menu lateral, por perfil de usuário. */
export const SECTION_LABEL = {
  ADMIN: 'PAINEL ADMINISTRATIVO',
  PROFESSOR: 'PORTAL DO PROFESSOR',
  ALUNO: 'PORTAL DO ALUNO'
};

/** Itens do menu lateral de navegação, agrupados por perfil de usuário. */
export const MENUS = {
  ADMIN: [
    { label: 'Dashboard', path: '/admin', icon: 'dashboard', end: true },
    { label: 'Alunos', path: '/admin/alunos', icon: 'students' },
    { label: 'Professores', path: '/admin/professores', icon: 'teacher' },
    { label: 'Séries', path: '/admin/series', icon: 'school' },
    { label: 'Turmas & Vínculos', path: '/admin/turmas', icon: 'layers' },
    { label: 'Disciplinas', path: '/admin/disciplinas', icon: 'book' },
    { label: 'Horários', path: '/admin/horarios', icon: 'clock' },
    { label: 'Calendário', path: '/admin/calendario', icon: 'calendar' },
    { label: 'Fechamento Bimestre', path: '/admin/bimestre', icon: 'clipboard' },
    { label: 'Achados e Perdidos', path: '/admin/achados-perdidos', icon: 'search' }
  ],
  PROFESSOR: [
    { label: 'Dashboard', path: '/professor', icon: 'dashboard', end: true },
    { label: 'Fazer Chamada', path: '/professor/chamada', icon: 'checkSquare' },
    { label: 'Lançar Notas', path: '/professor/notas', icon: 'edit' },
    { label: 'Ocorrências', path: '/professor/ocorrencias', icon: 'alert' },
    { label: 'Consulta Alunos', path: '/professor/alunos', icon: 'students' },
    { label: 'Meus Horários', path: '/professor/horarios', icon: 'clock' }
  ],
  ALUNO: [
    { label: 'Painel do Aluno', path: '/aluno', icon: 'dashboard', end: true },
    { label: 'Minhas Notas', path: '/aluno/notas', icon: 'edit' },
    { label: 'Minhas Faltas', path: '/aluno/faltas', icon: 'xCircle' },
    { label: 'Meu Boletim', path: '/aluno/boletim', icon: 'file' },
    { label: 'Meus Horários', path: '/aluno/horarios', icon: 'clock' },
    { label: 'Ocorrências', path: '/aluno/ocorrencias', icon: 'alert' },
    { label: 'Calendário Escolar', path: '/aluno/calendario', icon: 'calendar' },
    { label: 'Achados e Perdidos', path: '/aluno/achados-perdidos', icon: 'search' }
  ]
};

/**
 * Retorna a lista de itens de menu correspondente ao perfil informado.
 *
 * @param {string} perfil perfil do usuário logado (ex.: "ADMIN", "PROFESSOR", "ALUNO")
 * @returns {Array<object>} lista de itens de menu do perfil, ou lista vazia se o perfil for desconhecido
 */
export function menuFor(perfil) {
  return MENUS[perfil] || [];
}

/**
 * Determina o título a ser exibido no cabeçalho da página, com base no
 * item de menu correspondente à rota atual.
 *
 * @param {string} perfil perfil do usuário logado
 * @param {string} pathname caminho (pathname) atual da URL
 * @returns {string} o rótulo do item de menu correspondente, ou "ClassHub" como padrão
 */
export function titleForPath(perfil, pathname) {
  const items = menuFor(perfil);
  const exact = items.find((item) => item.path === pathname);
  if (exact) return exact.label;
  const nested = items.filter((item) => !item.end && pathname.startsWith(item.path + '/'));
  if (nested.length) return nested[nested.length - 1].label;
  const fallback = items.find((item) => item.end);
  return fallback ? fallback.label : 'ClassHub';
}
