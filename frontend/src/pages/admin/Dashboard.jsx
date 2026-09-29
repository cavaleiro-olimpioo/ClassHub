import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Icon from '../../components/Icon.jsx';
import { Card, Loading, PageHead, StatCard } from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import { getSession } from '../../lib/session.js';
import { firstName } from '../../lib/format.js';

const SHORTCUTS = [
  { label: 'Alunos Amostradinhos', to: '/admin/alunos', icon: 'students' },
  { label: 'Professores Kirk & Floyd', to: '/admin/professores', icon: 'teacher' },
  { label: 'Turmas & Vínculos 67', to: '/admin/turmas', icon: 'layers' },
  { label: 'Disciplinas Lá Ele', to: '/admin/disciplinas', icon: 'book' },
  { label: 'Grade de Horários Bora Bill', to: '/admin/horarios', icon: 'clock' },
  { label: 'Calendário Eitcha', to: '/admin/calendario', icon: 'calendar' },
  { label: 'Fechamento de Bimestre 67', to: '/admin/bimestre', icon: 'clipboard' },
  { label: 'Achados e Perdidos Jennifer', to: '/admin/achados-perdidos', icon: 'search' }
];

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const session = getSession();

  useEffect(() => {
    let active = true;
    Promise.all([
      api.get('/alunos').catch(() => []),
      api.get('/professores').catch(() => []),
      api.get('/turmas').catch(() => []),
      api.get('/disciplinas').catch(() => []),
      api.get('/vinculos').catch(() => [])
    ]).then(([alunos, professores, turmas, disciplinas, vinculos]) => {
      if (!active) return;
      setStats({
        alunos: Array.isArray(alunos) ? alunos.length : 0,
        professores: Array.isArray(professores) ? professores.length : 0,
        turmas: Array.isArray(turmas) ? turmas.length : 0,
        disciplinas: Array.isArray(disciplinas) ? disciplinas.length : 0,
        vinculos: Array.isArray(vinculos) ? vinculos.length : 0
      });
      setLoading(false);
    });
    return () => {
      active = false;
    };
  }, []);

  if (loading) return <Loading message="Carregando indicadores da resenha 67..." />;

  return (
    <>
      <PageHead
        title={`Olá, ${firstName(session?.nome)}`}
        subtitle="Visão geral do PhonkHub 67: alunos amostradinhos, docentes Kirk e Floyd, turmas da resenha e bora bill!"
      />

      <div className="grid grid--stats">
        <StatCard icon="students" tone="primary" label="Alunos Amostradinhos 67" value={stats.alunos} caption="Alunos ativos na resenha" />
        <StatCard icon="teacher" tone="info" label="Professores Floyd & Kirk" value={stats.professores} caption="Corpo docente lá ele" />
        <StatCard icon="layers" tone="success" label="Turmas da Resenha" value={stats.turmas} caption="Turmas criadas no 67" />
        <StatCard icon="book" tone="warning" label="Disciplinas Lá Ele" value={stats.disciplinas} caption="Componentes curriculares eitxha" />
        <StatCard icon="link" tone="neutral" label="Vínculos Jennifer" value={stats.vinculos} caption="Professor ↔ disciplina na resenha" />
      </div>

      <div style={{ height: 16 }} />

      <Card title="Atalhos da Resenha 67" icon="dashboard" subtitle="Acesse rapidamente os módulos amostradinhos do PhonkHub">
        <div className="grid grid--3">
          {SHORTCUTS.map((shortcut) => (
            <Link
              key={shortcut.to}
              to={shortcut.to}
              className="card"
              style={{ padding: 14, display: 'flex', alignItems: 'center', gap: 12, transition: 'border-color .15s, box-shadow .15s' }}
            >
              <span className="stat__icon">
                <Icon name={shortcut.icon} size={18} />
              </span>
              <span style={{ fontWeight: 600, fontSize: 13.5 }}>{shortcut.label}</span>
              <span style={{ marginLeft: 'auto', color: 'var(--text-muted)' }}>
                <Icon name="chevronRight" size={16} />
              </span>
            </Link>
          ))}
        </div>
      </Card>
    </>
  );
}
