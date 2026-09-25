import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Card,
  DataTable,
  Loading,
  PageHead,
  Pagination,
  Select,
  StatCard,
  StatusBadge,
  paginate
} from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import useAluno from '../../lib/useAluno.js';
import { BIMESTRES, calcularMediaPesos, formatGrade, situacaoAluno } from '../../lib/format.js';

const TIPO_LABEL = { PROVA: 'Prova', TRABALHO: 'Trabalho', ATIVIDADE: 'Atividade' };
const PAGE_SIZE = 12;

export default function AlunoNotas() {
  const { alunoId, loading: loadingAluno, error } = useAluno();

  const [bimestre, setBimestre] = useState(1);
  const [notas, setNotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);

  useEffect(() => {
    if (!alunoId) return;
    let active = true;
    setLoading(true);
    api
      .get(`/notas/aluno/${alunoId}`)
      .then((data) => {
        if (active) setNotas(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setNotas([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [alunoId]);

  const doBimestre = useMemo(
    () => notas.filter((n) => Number(n.bimestre) === Number(bimestre)),
    [notas, bimestre]
  );

  // Consolida por disciplina usando os pesos declarados em cada nota
  const porDisciplina = useMemo(() => {
    const mapa = new Map();
    doBimestre.forEach((nota) => {
      const key = nota.disciplinaId;
      if (!mapa.has(key)) {
        mapa.set(key, { disciplinaId: key, disciplinaNome: nota.disciplinaNome || `Disciplina ${key}`, notas: {} });
      }
      mapa.get(key).notas[String(nota.tipo).toUpperCase()] = { valor: nota.valor, peso: nota.peso };
    });
    return Array.from(mapa.values()).map((item) => {
      const media = calcularMediaPesos(item.notas);
      return { ...item, media, situacao: media === null ? null : situacaoAluno(media) };
    });
  }, [doBimestre]);

  const safePage = Math.min(page, Math.max(1, Math.ceil(doBimestre.length / PAGE_SIZE)));
  const mediaGeral = useMemo(() => {
    const validas = porDisciplina.filter((d) => d.media !== null);
    if (validas.length === 0) return null;
    return validas.reduce((soma, d) => soma + d.media, 0) / validas.length;
  }, [porDisciplina]);

  if (loadingAluno) return <Loading message="Carregando suas notas..." />;

  return (
    <>
      <PageHead
        title="Minhas Notas"
        subtitle="Consulte as avaliações lançadas pelos professores e a média de cada disciplina."
        actions={
          <div style={{ minWidth: 180 }}>
            <Select value={bimestre} onChange={(e) => { setBimestre(Number(e.target.value)); setPage(1); }} aria-label="Bimestre">
              {BIMESTRES.map((b) => (
                <option key={b} value={b}>
                  {b}º Bimestre
                </option>
              ))}
            </Select>
          </div>
        }
      />

      {error && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning">{error}</Alert>
        </div>
      )}

      <div className="grid grid--stats">
        <StatCard icon="edit" tone="primary" label="Disciplinas" value={porDisciplina.length} caption={`Com notas no ${bimestre}º bimestre`} />
        <StatCard icon="clipboard" tone="info" label="Avaliações" value={doBimestre.length} caption="Notas lançadas" />
        <StatCard
          icon="trending"
          tone={mediaGeral !== null && mediaGeral >= 6 ? 'success' : 'danger'}
          label="Média Geral"
          value={formatGrade(mediaGeral)}
          caption="Média das disciplinas com notas"
        />
      </div>

      <div style={{ height: 16 }} />

      <Card title="Média por Disciplina" icon="book" subtitle={`${bimestre}º Bimestre`} flush>
        <DataTable
          loading={loading}
          rows={porDisciplina}
          emptyIcon="edit"
          emptyTitle="Nenhuma nota lançada"
          emptySubtitle="Ainda não há notas registradas para este bimestre."
          columns={[
            { key: 'disciplinaNome', label: 'Disciplina', render: (v) => <span className="cell-strong">{v}</span> },
            { key: 'media', label: 'Média', width: 120, render: (v) => (v === null ? '-' : <strong>{formatGrade(v)}</strong>) },
            {
              key: 'situacao',
              label: 'Situação',
              width: 150,
              render: (v) => (v ? <StatusBadge status={v} /> : <span className="text-muted">-</span>)
            }
          ]}
        />
      </Card>

      <div style={{ height: 16 }} />

      <Card
        title="Notas Lançadas"
        icon="edit"
        subtitle={`${doBimestre.length} avaliação(ões)`}
        flush
        footer={<Pagination page={safePage} pageSize={PAGE_SIZE} total={doBimestre.length} onPageChange={setPage} itemLabel="avaliações" />}
      >
        <DataTable
          loading={loading}
          rows={paginate(doBimestre, safePage, PAGE_SIZE)}
          emptyIcon="edit"
          emptyTitle="Nenhuma nota lançada"
          emptySubtitle="As avaliações dos professores aparecerão aqui."
          columns={[
            { key: 'disciplinaNome', label: 'Disciplina', render: (v, row) => v || `Disciplina ${row.disciplinaId}` },
            { key: 'bimestre', label: 'Bimestre', width: 110, render: (v) => `${v}º` },
            { key: 'tipo', label: 'Tipo', width: 130, render: (v) => TIPO_LABEL[String(v).toUpperCase()] || v },
            { key: 'valor', label: 'Nota', width: 100, className: 'num', render: (v) => <strong>{formatGrade(v)}</strong> },
            { key: 'peso', label: 'Peso', width: 90, className: 'num' }
          ]}
        />
      </Card>
    </>
  );
}
