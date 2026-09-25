import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Card,
  DataTable,
  FilterChips,
  Loading,
  PageHead,
  Pagination,
  StatCard,
  StatusBadge,
  paginate
} from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import useAluno from '../../lib/useAluno.js';
import { formatDate } from '../../lib/format.js';

const PAGE_SIZE = 10;

/** Frequencia = presencas / (presencas + faltas), em %. */
function calcularFrequencia(presencas) {
  if (presencas.length === 0) return null;
  const presentes = presencas.filter((p) => p.status === 'PRESENTE').length;
  const total = presencas.length;
  return Math.round((presentes / total) * 100);
}

export default function AlunoFaltas() {
  const { alunoId, loading: loadingAluno, error } = useAluno();

  const [presencas, setPresencas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtro, setFiltro] = useState('FALTAS');
  const [page, setPage] = useState(1);

  useEffect(() => {
    if (!alunoId) return;
    let active = true;
    setLoading(true);
    api
      .get('/presencas', { alunoId })
      .then((data) => {
        if (active) setPresencas(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setPresencas([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [alunoId]);

  const faltas = useMemo(() => presencas.filter((p) => p.status === 'FALTA'), [presencas]);
  const justificadas = useMemo(() => presencas.filter((p) => p.status === 'FALTA_JUSTIFICADA'), [presencas]);
  const presentes = useMemo(() => presencas.filter((p) => p.status === 'PRESENTE'), [presencas]);
  const frequencia = useMemo(() => calcularFrequencia(presencas), [presencas]);

  const CHIP_OPCOES = useMemo(
    () => [
      { value: 'TODAS', label: 'Todas', count: presencas.length },
      { value: 'FALTAS', label: 'Faltas', count: faltas.length },
      { value: 'JUSTIFICADAS', label: 'Justificadas', count: justificadas.length },
      { value: 'PRESENTES', label: 'Presenças', count: presentes.length }
    ],
    [presencas.length, faltas.length, justificadas.length, presentes.length]
  );

  const filtradas = useMemo(() => {
    switch (filtro) {
      case 'FALTAS':
        return faltas;
      case 'JUSTIFICADAS':
        return justificadas;
      case 'PRESENTES':
        return presentes;
      default:
        return presencas;
    }
  }, [filtro, faltas, justificadas, presentes, presencas]);

  const safePage = Math.min(page, Math.max(1, Math.ceil(filtradas.length / PAGE_SIZE)));

  if (loadingAluno) return <Loading message="Carregando sua frequência..." />;

  return (
    <>
      <PageHead title="Minhas Faltas" subtitle="Acompanhe sua frequência, faltas e justificativas registradas pelos professores." />

      {error && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning">{error}</Alert>
        </div>
      )}

      <div className="grid grid--stats">
        <StatCard icon="xCircle" tone="danger" label="Faltas" value={faltas.length} caption="Faltas não justificadas" />
        <StatCard icon="checkSquare" tone="warning" label="Justificadas" value={justificadas.length} caption="Faltas com atestado" />
        <StatCard icon="check" tone="success" label="Presenças" value={presentes.length} caption="Aulas com presença" />
        <StatCard
          icon="trending"
          tone={frequencia !== null && frequencia >= 75 ? 'success' : 'danger'}
          label="Frequência"
          value={frequencia === null ? '—' : `${frequencia}%`}
          caption="Mínimo exigido: 75%"
        />
      </div>

      <div style={{ height: 16 }} />

      {frequencia !== null && frequencia < 75 && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning" title="Frequência abaixo do mínimo">
            Sua frequência está em {frequencia}%. O mínimo exigido é 75% — procure a secretaria para regularizar faltas
            justificadas.
          </Alert>
        </div>
      )}

      <Card
        title="Registros de Frequência"
        icon="checkSquare"
        subtitle={`${filtradas.length} registro(s)`}
        flush
        footer={<Pagination page={safePage} pageSize={PAGE_SIZE} total={filtradas.length} onPageChange={setPage} itemLabel="registros" />}
      >
        <div style={{ padding: '14px 18px' }}>
          <FilterChips
            options={CHIP_OPCOES}
            value={filtro}
            onChange={(v) => {
              setFiltro(v);
              setPage(1);
            }}
          />
        </div>
        <DataTable
          loading={loading}
          rows={paginate(filtradas, safePage, PAGE_SIZE)}
          emptyIcon="checkSquare"
          emptyTitle="Nenhum registro encontrado"
          emptySubtitle="Não há lançamentos de frequência para o filtro selecionado."
          columns={[
            { key: 'data', label: 'Data', width: 140, render: (v) => <span className="cell-strong mono">{formatDate(v)}</span> },
            { key: 'disciplinaNome', label: 'Disciplina', render: (v) => v || '-' },
            { key: 'status', label: 'Status', width: 180, render: (v) => <StatusBadge status={v} /> },
            { key: 'justificativa', label: 'Justificativa', className: 'cell-muted' }
          ]}
        />
      </Card>
    </>
  );
}
