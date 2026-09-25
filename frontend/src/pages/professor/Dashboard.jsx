import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Icon from '../../components/Icon.jsx';
import { Card, Loading, PageHead, StatCard } from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import { getSession } from '../../lib/session.js';
import { firstName, todayISO, WEEKDAYS } from '../../lib/format.js';
import { useProfessorVinculos } from '../../lib/useProfessorVinculos.js';

const SHORTCUTS = [
  { label: 'Fazer Chamada', to: '/professor/chamada', icon: 'checkSquare' },
  { label: 'Lançar Notas', to: '/professor/notas', icon: 'edit' },
  { label: 'Ocorrências', to: '/professor/ocorrencias', icon: 'alert' },
  { label: 'Consulta de Alunos', to: '/professor/alunos', icon: 'students' },
  { label: 'Meus Horários', to: '/professor/horarios', icon: 'clock' }
];

export default function ProfessorDashboard() {
  const session = getSession();
  const { professorId, vinculos, loading } = useProfessorVinculos();

  const [horarios, setHorarios] = useState([]);
  const [aulasHoje, setAulasHoje] = useState([]);

  useEffect(() => {
    if (!professorId) return;
    let active = true;
    api
      .get(`/horarios/professor/${professorId}`)
      .then((data) => {
        if (!active) return;
        const list = Array.isArray(data) ? data : [];
        setHorarios(list);
        const hoje = new Date().getDay() || 7; // domingo(0) -> 7
        setAulasHoje(list.filter((h) => Number(h.diaSemana) === hoje));
      })
      .catch(() => {
        if (active) {
          setHorarios([]);
          setAulasHoje([]);
        }
      });
    return () => {
      active = false;
    };
  }, [professorId]);

  if (loading) return <Loading message="Carregando seu painel..." />;

  const hoje = new Date().getDay();

  return (
    <>
      <PageHead
        title={`Olá, ${firstName(session?.nome)}`}
        subtitle={`Você tem ${vinculos.length} vínculo(s) ativo(s) e ${aulasHoje.length} aula(s) programada(s) para hoje.`}
      />

      <div className="grid grid--stats">
        <StatCard icon="layers" tone="primary" label="Turmas Vinculadas" value={new Set(vinculos.map((v) => v.turma?.id ?? v.turmaId)).size} caption="Turmas no ano letivo" />
        <StatCard icon="book" tone="info" label="Disciplinas" value={new Set(vinculos.map((v) => v.disciplina?.id ?? v.disciplinaId)).size} caption="Componentes que você leciona" />
        <StatCard icon="clock" tone="success" label="Aulas na Semana" value={horarios.length} caption="Horários registrados" />
        <StatCard icon="calendar" tone="warning" label="Aulas Hoje" value={aulasHoje.length} caption={WEEKDAYS[hoje]} />
      </div>

      <div style={{ height: 16 }} />

      <div className="grid grid--2">
        <Card title="Aulas de Hoje" icon="clock" subtitle={WEEKDAYS[hoje]}>
          {aulasHoje.length === 0 ? (
            <p className="text-muted" style={{ fontSize: 13 }}>
              Nenhuma aula programada para hoje.
            </p>
          ) : (
            <div className="stack" style={{ gap: 8 }}>
              {aulasHoje.map((aula) => (
                <div
                  key={aula.id}
                  style={{ border: '1px solid var(--border)', borderLeft: '3px solid var(--primary)', borderRadius: 'var(--radius-sm)', padding: '10px 12px' }}
                >
                  <strong style={{ fontSize: 13.5 }}>{aula.disciplina?.nome || aula.disciplinaNome}</strong>
                  <div className="text-muted" style={{ fontSize: 12 }}>
                    {aula.horaInicio} – {aula.horaFim} · {aula.turma?.nome || aula.turmaNome}
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        <Card title="Atalhos" icon="dashboard" subtitle="Acesse rapidamente suas ferramentas">
          <div className="stack" style={{ gap: 8 }}>
            {SHORTCUTS.map((shortcut) => (
              <Link
                key={shortcut.to}
                to={shortcut.to}
                className="card"
                style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 12 }}
              >
                <span className="stat__icon">
                  <Icon name={shortcut.icon} size={17} />
                </span>
                <span style={{ fontWeight: 600, fontSize: 13.5 }}>{shortcut.label}</span>
                <span style={{ marginLeft: 'auto', color: 'var(--text-muted)' }}>
                  <Icon name="chevronRight" size={16} />
                </span>
              </Link>
            ))}
          </div>
        </Card>
      </div>

      <div style={{ height: 16 }} />
      <p className="text-muted" style={{ fontSize: 12 }}>
        Data de referência: {todayISO()}
      </p>
    </>
  );
}
