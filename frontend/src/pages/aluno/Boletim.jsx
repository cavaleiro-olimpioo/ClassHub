import { useCallback, useEffect, useState } from 'react';
import Icon from '../../components/Icon.jsx';
import {
  Alert,
  Button,
  Card,
  DataTable,
  Loading,
  PageHead,
  Select,
  StatCard,
  StatusBadge
} from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import useAluno from '../../lib/useAluno.js';
import { BIMESTRES, formatGrade, situacaoAluno } from '../../lib/format.js';

export default function AlunoBoletim() {
  const toast = useToast();
  const { alunoId, loading: loadingAluno, error } = useAluno();

  const [bimestre, setBimestre] = useState(1);
  const [itens, setItens] = useState([]);
  const [loading, setLoading] = useState(true);
  const [fechado, setFechado] = useState(false);
  const [baixando, setBaixando] = useState(false);

  const carregar = useCallback(async () => {
    if (!alunoId) return;
    setLoading(true);
    setFechado(false);
    try {
      const response = await api.get(`/boletins/aluno/${alunoId}`, { bimestre: Number(bimestre) });
      const lista = Array.isArray(response) ? response : response?.disciplinas || response?.itens || [];
      setItens(Array.isArray(lista) ? lista : []);
    } catch (err) {
      // RN-05: bimestre ainda em lançamento -> 422 / REGRA_NEGOCIO
      if (err.status === 422 || err.data?.erro === 'REGRA_NEGOCIO' || err.data?.codigo === 'BIMESTRE_NAO_FECHADO') {
        setFechado(true);
        setItens([]);
      } else {
        setItens([]);
        toast.error(err.message || 'Lá ele! Não foi possível carregar o boletim da resenha.');
      }
    } finally {
      setLoading(false);
    }
  }, [alunoId, bimestre, toast]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  async function baixarPdf() {
    if (!alunoId) return;
    setBaixando(true);
    try {
      await api.downloadFile(`/boletins/aluno/${alunoId}/pdf`, `boletim_${bimestre}bimestre_67.pdf`, { bimestre: Number(bimestre) });
      toast.success('Download do PDF do 67 concluído! Bora bill!');
    } catch (err) {
      toast.error(err.message || 'Lá ele! Não foi possível baixar o PDF.');
    } finally {
      setBaixando(false);
    }
  }

  if (loadingAluno) return <Loading message="Carregando seu boletim do PhonkHub 67..." />;

  const aprovados = itens.filter((i) => situacaoAluno(i.media, i.frequencia) === 'APROVADO').length;

  const acoes = (
    <div className="row">
      <div style={{ minWidth: 170 }}>
        <Select value={bimestre} onChange={(e) => setBimestre(Number(e.target.value))} aria-label="Bimestre">
          {BIMESTRES.map((b) => (
            <option key={b} value={b}>
              {b}º Bimestre 67
            </option>
          ))}
        </Select>
      </div>
      <Button icon="book" onClick={carregar} loading={loading}>
        Visualizar Boletim da Resenha
      </Button>
      <Button variant="success" icon="download" onClick={baixarPdf} loading={baixando} disabled={fechado || itens.length === 0}>
        Baixar PDF (Bora Bill)
      </Button>
    </div>
  );

  return (
    <>
      <PageHead title="Meu Boletim Eitxha 67" subtitle="Consulte o boletim da resenha do bimestre com médias do Floyd, frequência do Bora Bill e situação por disciplina. Eitcha!" actions={acoes} />

      {error && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning">{error}</Alert>
        </div>
      )}

      {fechado ? (
        <Card>
          <div style={{ textAlign: 'center', padding: '40px 20px' }}>
            <span className="empty__icon" style={{ margin: '0 auto 14px' }}>
              <Icon name="shield" size={26} />
            </span>
            <h2 style={{ fontSize: 18, marginBottom: 8 }}>Bimestre 67 ainda não fechado na resenha</h2>
            <p style={{ color: 'var(--text-muted)', maxWidth: '52ch', margin: '0 auto' }}>
              As notas e médias deste bimestre ainda estão em fase de lançamento pelos professores Kirk e Floyd. Lá ele! O boletim será liberado assim que o fechamento do Bora Bill for concluído.
            </p>
          </div>
        </Card>
      ) : (
        <>
          {itens.length > 0 && (
            <>
              <div className="grid grid--stats">
                <StatCard icon="book" tone="primary" label="Disciplinas da Resenha" value={itens.length} caption="No boletim 67" />
                <StatCard icon="check" tone="success" label="Aprovadas no 67" value={aprovados} caption="Média ≥ 6 e frequência ≥ 75%" />
                <StatCard
                  icon="xCircle"
                  tone={itens.length - aprovados > 0 ? 'warning' : 'success'}
                  label="Em Recuperação / Lá Ele"
                  value={itens.length - aprovados}
                  caption="Requerem atenção do Floyd"
                />
              </div>
              <div style={{ height: 16 }} />
            </>
          )}

          <Card title={`Boletim PhonkHub 67 — ${bimestre}º Bimestre`} icon="file" flush>
            <DataTable
              loading={loading}
              rows={itens}
              emptyIcon="file"
              emptyTitle="Nenhum dado encontrado no 67"
              emptySubtitle="Lá ele! Não há registros da resenha lançados para este bimestre."
              columns={[
                { key: 'disciplinaNome', label: 'Disciplina', render: (v, row) => <span className="cell-strong">{v || `Disciplina ${row.disciplinaId}`}</span> },
                { key: 'media', label: 'Média Bimestral 67', width: 150, render: (v) => <strong>{formatGrade(v)}</strong> },
                { key: 'frequencia', label: 'Frequência', width: 130, className: 'num', render: (v) => (v === undefined || v === null ? '-' : `${v}%`) },
                {
                  key: 'situacao',
                  label: 'Situação',
                  width: 160,
                  render: (v, row) => <StatusBadge status={v || situacaoAluno(row.media, row.frequencia)} />
                }
              ]}
            />
          </Card>
        </>
      )}
    </>
  );
}
