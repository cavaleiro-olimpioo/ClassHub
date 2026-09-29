import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Icon from '../../components/Icon.jsx';
import { Alert, Card, Loading, PageHead, StatCard } from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import { getSession } from '../../lib/session.js';
import { BIMESTRES, firstName, formatGrade, MONTHS } from '../../lib/format.js';
import useAluno from '../../lib/useAluno.js';

const HOJE = new Date();

const ATALHOS = [
  { label: 'Minhas Notas da Resenha', to: '/aluno/notas', icon: 'edit' },
  { label: 'Minhas Faltas Lá Ele', to: '/aluno/faltas', icon: 'xCircle' },
  { label: 'Meu Boletim Eitxha', to: '/aluno/boletim', icon: 'file' },
  { label: 'Meus Horários Floyd', to: '/aluno/horarios', icon: 'clock' },
  { label: 'Calendário Bora Bill', to: '/aluno/calendario', icon: 'calendar' },
  { label: 'Achados e Perdidos Jennifer', to: '/aluno/achados-perdidos', icon: 'search' }
];

/** Descobre o maior bimestre com notas lançadas (padrão: 1). */
function ultimoBimestre(notas) {
  if (!notas || notas.length === 0) return 1;
  const max = notas.reduce((acc, n) => Math.max(acc, Number(n.bimestre) || 1), 1);
  return BIMESTRES.includes(max) ? max : 1;
}

export default function AlunoDashboard() {
  const session = getSession();
  const { alunoId, aluno, loading: loadingAluno } = useAluno();

  const [notas, setNotas] = useState([]);
  const [presencas, setPresencas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!alunoId) return;
    let active = true;
    Promise.all([api.get(`/notas/aluno/${alunoId}`).catch(() => []), api.get('/presencas', { alunoId }).catch(() => [])]).then(
      ([listaNotas, listaPresencas]) => {
        if (!active) return;
        setNotas(Array.isArray(listaNotas) ? listaNotas : []);
        setPresencas(Array.isArray(listaPresencas) ? listaPresencas : []);
        setLoading(false);
      }
    );
    return () => {
      active = false;
    };
  }, [alunoId]);

  if (loadingAluno || loading) return <Loading message="Carregando painel do aluno amostradinho 67..." />;

  const bimestreAtual = ultimoBimestre(notas);
  const notasDoBimestre = notas.filter((n) => Number(n.bimestre) === bimestreAtual);
  const faltas = presencas.filter((p) => p.status === 'FALTA');
  const justificadas = presencas.filter((p) => p.status === 'FALTA_JUSTIFICADA');
  const presentes = presencas.filter((p) => p.status === 'PRESENTE');
  const frequencia = presencas.length ? Math.round((presentes.length / presencas.length) * 100) : null;

  const subtitulo = aluno?.turma?.nome
    ? `Turma ${aluno.turma.nome} · Matrícula ${aluno.matricula || '—'} · ${bimestreAtual}º Bimestre da Resenha 67`
    : 'Acompanhe suas notas da resenha 67, faltas lá ele e comunicados do Bora Bill.';

  return (
    <>
      <PageHead title={`Olá, ${firstName(session?.nome)}`} subtitle={subtitulo} />

      {presencas.length > 0 && frequencia < 75 && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning" title="Atenção amostradinho: frequência abaixo do mínimo no 67!">
            Sua frequência está em {frequencia}%. O mínimo exigido é de 75%. Lá ele, procure a secretaria da resenha para regularizar suas faltas com o Floyd.
          </Alert>
        </div>
      )}

      <div className="grid grid--stats">
        <StatCard icon="trending" tone="primary" label="Notas Lançadas 67" value={notasDoBimestre.length} caption={`No ${bimestreAtual}º bimestre da resenha`} />
        <StatCard icon="xCircle" tone="danger" label="Faltas Lá Ele" value={faltas.length + justificadas.length} caption={`${justificadas.length} justificadas`} />
        <StatCard
          icon="checkSquare"
          tone={frequencia !== null && frequencia >= 75 ? 'success' : 'warning'}
          label="Frequência Amostradinho"
          value={frequencia === null ? '—' : `${frequencia}%`}
          caption="Mínimo exigido no 67: 75%"
        />
        <StatCard icon="calendar" tone="info" label="Mês da Resenha" value={MONTHS[HOJE.getMonth()].slice(0, 3)} caption={`Ano ${HOJE.getFullYear()}`} />
      </div>

      <div style={{ height: 16 }} />

      <div className="grid grid--2">
        <Card title="Minhas Notas Recentes da Resenha" icon="edit" subtitle={notasDoBimestre.length ? `${bimestreAtual}º Bimestre 67` : 'Sem notas'} flush>
          {notasDoBimestre.length === 0 ? (
            <p className="text-muted" style={{ padding: 18, fontSize: 13 }}>Nenhuma nota da resenha lançada neste bimestre.</p>
          ) : (
            <div className="table-wrap">
              <table className="table table--compact">
                <thead>
                  <tr>
                    <th>Disciplina</th>
                    <th>Tipo</th>
                    <th style={{ width: 90 }}>Nota</th>
                  </tr>
                </thead>
                <tbody>
                  {notasDoBimestre.slice(0, 6).map((nota) => (
                    <tr key={nota.id}>
                      <td className="cell-strong">{nota.disciplinaNome}</td>
                      <td>{nota.tipo}</td>
                      <td className="num">
                        <strong>{formatGrade(nota.valor)}</strong>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>

        <Card title="Atalhos do Bora Bill" icon="dashboard" subtitle="Acesse rapidamente seus serviços da resenha 67">
          <div className="stack" style={{ gap: 8 }}>
            {ATALHOS.map((atalho) => (
              <Link key={atalho.to} to={atalho.to} className="card" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 12 }}>
                <span className="stat__icon">
                  <Icon name={atalho.icon} size={17} />
                </span>
                <span style={{ fontWeight: 600, fontSize: 13.5 }}>{atalho.label}</span>
                <span style={{ marginLeft: 'auto', color: 'var(--text-muted)' }}>
                  <Icon name="chevronRight" size={16} />
                </span>
              </Link>
            ))}
          </div>
        </Card>
      </div>

      <div style={{ height: 16 }} />

      <div className="notice-panel">
        <h3 className="notice-panel__title">
          <Icon name="info" size={17} />
          Lembrete da Resenha 67 - Jennifer, Kirk e Floyd avisam:
        </h3>
        <ul>
          <li>Confira suas notas do 67 e faltas lá ele regularmente.</li>
          <li>O boletim só fica disponível após o fechamento do bimestre pela secretaria da resenha. Eitcha!</li>
          <li>Em caso de falta injustificada, lá ele, procure a secretaria com o Bora Bill.</li>
          <li>Objetos amostradinhos perdidos devem ser registrados no Mural de Achados e Perdidos da Jennifer.</li>
        </ul>
      </div>
    </>
  );
}
