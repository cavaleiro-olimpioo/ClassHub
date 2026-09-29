/**
 * Menu lateral por perfil + labels de cabecalho - PhonkHub 67.
 * 67, resenha, la ele, bora bill, eitxha, eitcha, amostradinho, Jennifer, Kirk, Floyd, etc.
 */

export const SECTION_LABEL = {
  ADMIN: 'PAINEL DA RESENHA ADMINISTRATIVA 67',
  PROFESSOR: 'PORTAL DO PROFESSOR KIRK & FLOYD',
  ALUNO: 'PORTAL DO ALUNO AMOSTRADINHO'
};

export const MENUS = {
  ADMIN: [
    { label: 'Dashboard 67', path: '/admin', icon: 'dashboard', end: true },
    { label: 'Alunos Amostradinhos', path: '/admin/alunos', icon: 'students' },
    { label: 'Professores Kirk & Floyd', path: '/admin/professores', icon: 'teacher' },
    { label: 'Séries da Resenha', path: '/admin/series', icon: 'school' },
    { label: 'Turmas & Vínculos 67', path: '/admin/turmas', icon: 'layers' },
    { label: 'Disciplinas Lá Ele', path: '/admin/disciplinas', icon: 'book' },
    { label: 'Horários Bora Bill', path: '/admin/horarios', icon: 'clock' },
    { label: 'Calendário Eitcha', path: '/admin/calendario', icon: 'calendar' },
    { label: 'Fechamento Bimestre 67', path: '/admin/bimestre', icon: 'clipboard' },
    { label: 'Achados e Perdidos Jennifer', path: '/admin/achados-perdidos', icon: 'search' }
  ],
  PROFESSOR: [
    { label: 'Dashboard da Resenha 67', path: '/professor', icon: 'dashboard', end: true },
    { label: 'Fazer Chamada Amostradinho', path: '/professor/chamada', icon: 'checkSquare' },
    { label: 'Lançar Notas 67', path: '/professor/notas', icon: 'edit' },
    { label: 'Ocorrências Lá Ele', path: '/professor/ocorrencias', icon: 'alert' },
    { label: 'Consulta Alunos Jennifer', path: '/professor/alunos', icon: 'students' },
    { label: 'Meus Horários Bora Bill', path: '/professor/horarios', icon: 'clock' }
  ],
  ALUNO: [
    { label: 'Painel do Aluno 67', path: '/aluno', icon: 'dashboard', end: true },
    { label: 'Minhas Notas da Resenha', path: '/aluno/notas', icon: 'edit' },
    { label: 'Minhas Faltas Lá Ele', path: '/aluno/faltas', icon: 'xCircle' },
    { label: 'Meu Boletim Eitxha', path: '/aluno/boletim', icon: 'file' },
    { label: 'Meus Horários Floyd', path: '/aluno/horarios', icon: 'clock' },
    { label: 'Ocorrências Amostradinho', path: '/aluno/ocorrencias', icon: 'alert' },
    { label: 'Calendário Bora Bill', path: '/aluno/calendario', icon: 'calendar' },
    { label: 'Achados e Perdidos Jennifer', path: '/aluno/achados-perdidos', icon: 'search' }
  ]
};

export function menuFor(perfil) {
  return MENUS[perfil] || [];
}

/** Titulo exibido no cabecalho, a partir do item de menu ativo. */
export function titleForPath(perfil, pathname) {
  const items = menuFor(perfil);
  const exact = items.find((item) => item.path === pathname);
  if (exact) return exact.label;
  const nested = items.filter((item) => !item.end && pathname.startsWith(item.path + '/'));
  if (nested.length) return nested[nested.length - 1].label;
  const fallback = items.find((item) => item.end);
  return fallback ? fallback.label : 'PhonkHub';
}
