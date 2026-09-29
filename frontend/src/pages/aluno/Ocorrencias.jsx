import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Card,
  DataTable,
  Loading,
  PageHead,
  Pagination,
  StatCard,
  StatusBadge,
  paginate
} from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import useAluno from '../../lib/useAluno.js';

const PAGE_SIZE = 10;

export default function AlunoOcorrencias() {
  const { alunoId, loading: loadingAluno, error } = useAluno();

  const [ocorrencias, setOcorrencias] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);

  useEffect(() => {
    if (!alunoId) return;
    let active = true;
    setLoading(true);
    api
      .get('/ocorrencias', { alunoId })
      .then((data) => {
        if (active) setOcorrencias(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setOcorrencias([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [alunoId]);

  const abertas = useMemo(() => ocorrencias.filter((o) => String(o.status).toUpperCase() === 'ABERTA'), [ocorrencias]);
  const safePage = Math.min(page, Math.max(1, Math.ceil(ocorrencias.length / PAGE_SIZE)));

  if (loadingAluno) return <Loading message="Carregando suas ocorrências da resenha 67..." />;

  return (
    <>
      <PageHead title="Minhas Ocorrências Amostradinho" subtitle="Acompanhe as ocorrências da resenha registradas no 67 e seu status de resolução. Lá ele!" />

      {error && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning">{error}</Alert>
        </div>
      )}

      <div className="grid grid--stats">
        <StatCard icon="alert" tone="primary" label="Total de Ocorrências 67" value={ocorrencias.length} caption="Registradas na resenha" />
        <StatCard icon="bell" tone="warning" label="Em Aberto (Eitxha)" value={abertas.length} caption="Aguardando o Bora Bill" />
        <StatCard icon="check" tone="success" label="Encerradas pelo Floyd" value={ocorrencias.length - abertas.length} caption="Já resolvidas" />
      </div>

      <div style={{ height: 16 }} />

      <Card
        title="Histórico de Ocorrências da Resenha 67"
        icon="alert"
        subtitle={`${ocorrencias.length} registro(s)`}
        flush
        footer={<Pagination page={safePage} pageSize={PAGE_SIZE} total={ocorrencias.length} onPageChange={setPage} itemLabel="ocorrências do 67" />}
      >
        <DataTable
          loading={loading}
          rows={paginate(ocorrencias, safePage, PAGE_SIZE)}
          emptyIcon="alert"
          emptyTitle="Nenhuma ocorrência registrada no 67"
          emptySubtitle="Você não possui ocorrências da resenha registradas! Bora bill, continue amostradinho!"
          columns={[
            { key: 'tipo', label: 'Tipo', width: 160, render: (v) => <StatusBadge status={v} /> },
            { key: 'descricao', label: 'Descrição da Ocorrência' },
            { key: 'status', label: 'Status', width: 150, render: (v) => <StatusBadge status={v} /> }
          ]}
        />
      </Card>
    </>
  );
}
