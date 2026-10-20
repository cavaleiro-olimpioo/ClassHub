import { useEffect, useState } from 'react';
import Icon from '../../components/Icon.jsx';
import { Alert, Button, Card, ConfirmDialog, DataTable, Field, Input, Loading, PageHead, Select } from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import useReference, { toOptions } from '../../lib/useReference.js';
import { BIMESTRES, formatDate } from '../../lib/format.js';

/**
 * Página administrativa de fechamento de bimestre: consolida as notas de
 * um bimestre/ano letivo (opcionalmente restrito a uma turma) e gera os
 * boletins correspondentes via `POST /boletins/gerar`.
 *
 * @returns {JSX.Element} a página de fechamento de bimestre
 */
export default function AdminBimestre() {
  const toast = useToast();
  const { data: turmas } = useReference('/turmas');

  const [anoLetivo, setAnoLetivo] = useState(new Date().getFullYear());
  const [bimestre, setBimestre] = useState(1);
  const [turmaId, setTurmaId] = useState('');
  const [dataInicio, setDataInicio] = useState('');
  const [dataFim, setDataFim] = useState('');

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [result, setResult] = useState(null);
  const [fechamentos, setFechamentos] = useState([]);
  const [loadingFechamentos, setLoadingFechamentos] = useState(true);
  const [fechamentoParaReabrir, setFechamentoParaReabrir] = useState(null);
  const [limpezaConfirmOpen, setLimpezaConfirmOpen] = useState(false);

  /** Busca os fechamentos reais que estão bloqueando, ou liberando, os lançamentos. */
  async function carregarFechamentos() {
    setLoadingFechamentos(true);
    try {
      const data = await api.get('/boletins/fechamentos');
      setFechamentos(Array.isArray(data) ? data : []);
    } catch (error) {
      toast.error(error.message || 'Não foi possível carregar os fechamentos.');
      setFechamentos([]);
    } finally {
      setLoadingFechamentos(false);
    }
  }

  useEffect(() => {
    carregarFechamentos();
    // O carregamento inicial não deve ser repetido ao mudar o formulário.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const turmaOptions = toOptions(turmas, 'id', (t) => `${t.nome} (${t.anoLetivo})`);

  /**
   * Confirma o fechamento do bimestre selecionado, chamando a API para
   * gerar os boletins e exibindo o resultado do processamento.
   */
  async function handleConfirm() {
    setProcessing(true);
    try {
      const body = { anoLetivo: Number(anoLetivo), bimestre: Number(bimestre), dataInicio, dataFim };
      if (turmaId) body.turmaId = Number(turmaId);

      const response = await api.post('/boletins/gerar', body);
      setResult(response?.mensagem || 'Boletins gerados com sucesso.');
      toast.success('Fechamento de bimestre concluído com sucesso!', 6000);
      setConfirmOpen(false);
      await carregarFechamentos();
    } catch (error) {
      toast.error(error.message || 'Não foi possível fechar o bimestre.');
    } finally {
      setProcessing(false);
    }
  }

  /** Reabre um período sem excluir as notas já lançadas. */
  async function reabrirBimestre() {
    if (!fechamentoParaReabrir) return;
    setProcessing(true);
    try {
      await api.delete(`/boletins/fechamentos/${fechamentoParaReabrir.id}`);
      toast.success('Bimestre reaberto. Os professores já podem corrigir e lançar notas.');
      setFechamentoParaReabrir(null);
      await carregarFechamentos();
    } catch (error) {
      toast.error(error.message || 'Não foi possível reabrir o bimestre.');
    } finally {
      setProcessing(false);
    }
  }

  /** Remove dados lançados por engano no período e reabre seus fechamentos. */
  async function limparNotasDoPeriodo() {
    setProcessing(true);
    try {
      const params = new URLSearchParams({ anoLetivo: String(anoLetivo), bimestre: String(bimestre) });
      if (turmaId) params.set('turmaId', String(turmaId));
      const response = await api.delete(`/notas/periodo?${params.toString()}`);
      setResult(response?.mensagem || 'Notas removidas e bimestre reaberto.');
      toast.success('Notas removidas e bimestre reaberto.');
      setLimpezaConfirmOpen(false);
      await carregarFechamentos();
    } catch (error) {
      toast.error(error.message || 'Não foi possível limpar as notas do período.');
    } finally {
      setProcessing(false);
    }
  }

  return (
    <>
      <PageHead
        title="Fechamento de Bimestre"
        subtitle="Consolide as notas, feche o bimestre e libere os boletins para consulta dos alunos e responsáveis."
      />

      <div className="grid grid--2">
        <Card title="Fechar Bimestre" icon="clipboard" subtitle="Defina o período letivo que deseja consolidar">
          <form
            className="stack"
            style={{ gap: 14 }}
            onSubmit={(e) => {
              e.preventDefault();
              setResult(null);
              setConfirmOpen(true);
            }}
          >
            <Field label="Ano Letivo" required htmlFor="f-ano">
              <Input id="f-ano" type="number" min={2000} value={anoLetivo} onChange={(e) => setAnoLetivo(e.target.value)} required />
            </Field>

            <div className="grid grid--2">
              <Field label="Início do bimestre" required htmlFor="f-data-inicio">
                <Input id="f-data-inicio" type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} required />
              </Field>
              <Field label="Fim do bimestre" required htmlFor="f-data-fim">
                <Input id="f-data-fim" type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} min={dataInicio || undefined} required />
              </Field>
            </div>

            <Field label="Bimestre" required htmlFor="f-bimestre">
              <Select id="f-bimestre" value={bimestre} onChange={(e) => setBimestre(e.target.value)}>
                {BIMESTRES.map((b) => (
                  <option key={b} value={b}>
                    {b}º Bimestre
                  </option>
                ))}
              </Select>
            </Field>

            <Field label="Turma Específica (opcional)" htmlFor="f-turma" help="Deixe em branco para fechar o bimestre de todas as turmas do ano letivo.">
              <Select id="f-turma" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} placeholder="Todas as Turmas do Ano Letivo">
                {turmaOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Select>
            </Field>

            <Button type="submit" size="lg" block icon="clipboard">
              Fechar Bimestre e Gerar Boletins
            </Button>
            <Button type="button" variant="danger" size="sm" block icon="trash" onClick={() => setLimpezaConfirmOpen(true)}>
              Limpar notas e reabrir período
            </Button>
          </form>
        </Card>

        <div className="stack">
          {processing ? (
            <Card>
              <Loading message="Consolidando notas e gerando boletins..." />
            </Card>
          ) : result ? (
            <Alert tone="success" title="Processamento concluído">
              O {bimestre}º Bimestre de {anoLetivo} foi oficialmente fechado. {result}
            </Alert>
          ) : (
            <Card title="Como funciona" icon="info">
              <ol style={{ margin: 0, paddingLeft: 18, color: 'var(--text-soft)', fontSize: 13, lineHeight: 1.8 }}>
                <li>Confira se todas as notas e faltas do bimestre foram lançadas.</li>
                <li>Selecione o ano letivo e o bimestre desejado.</li>
                <li>Opcionalmente, restrinja o fechamento a uma turma específica.</li>
                <li>Confirme a operação — os boletins serão consolidados e liberados.</li>
              </ol>
            </Card>
          )}

          <div className="notice-panel">
            <h3 className="notice-panel__title">
              <Icon name="alert" size={17} />
              Atenção
            </h3>
            <ul>
              <li>O fechamento é definitivo para o bimestre selecionado.</li>
              <li>Notas lançadas após o fechamento podem exigir um novo ajuste manual.</li>
              <li>Confirme com a secretaria antes de executar.</li>
            </ul>
          </div>
        </div>
      </div>

      <div style={{ height: 16 }} />

      <Card
        title="Bimestres fechados"
        icon="clipboard"
        subtitle="Estes são os únicos registros que bloqueiam o lançamento de notas. Reabra um período se ele tiver sido fechado por engano."
        flush
      >
        <DataTable
          loading={loadingFechamentos}
          rows={fechamentos}
          emptyIcon="checkSquare"
          emptyTitle="Nenhum bimestre fechado"
          emptySubtitle="Os professores podem lançar e corrigir notas em todos os períodos."
          columns={[
            { key: 'anoLetivo', label: 'Ano letivo', width: 120 },
            { key: 'bimestre', label: 'Bimestre', width: 120, render: (v) => `${v}º Bimestre` },
            { key: 'turmaNome', label: 'Turma', render: (v) => v || 'Todas as turmas' },
            { key: 'dataInicio', label: 'Período', render: (v, row) => `${formatDate(v)} a ${formatDate(row.dataFim)}` },
            {
              key: '__acoes',
              label: 'Ações',
              width: 150,
              render: (_value, row) => (
                <Button variant="secondary" size="sm" icon="refresh" onClick={() => setFechamentoParaReabrir(row)}>
                  Reabrir
                </Button>
              )
            }
          ]}
        />
      </Card>

      <ConfirmDialog
        open={confirmOpen}
        title="Confirmação de fechamento"
        message={`Você está prestes a fechar o ${bimestre}º Bimestre de ${anoLetivo} ${turmaId ? 'para a turma selecionada' : 'para TODAS as turmas'}. Os boletins serão consolidados e liberados para visualização. Deseja prosseguir?`}
        confirmText="Sim, fechar bimestre"
        loading={processing}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={handleConfirm}
      />

      <ConfirmDialog
        open={Boolean(fechamentoParaReabrir)}
        title="Reabrir bimestre"
        message={`O ${fechamentoParaReabrir?.bimestre}º bimestre de ${fechamentoParaReabrir?.anoLetivo} será reaberto. As notas existentes serão mantidas e voltarão a poder ser corrigidas. Deseja prosseguir?`}
        confirmText="Reabrir bimestre"
        tone="warning"
        loading={processing}
        onCancel={() => !processing && setFechamentoParaReabrir(null)}
        onConfirm={reabrirBimestre}
      />

      <ConfirmDialog
        open={limpezaConfirmOpen}
        title="Limpar notas do período"
        message={`Esta ação remove permanentemente todas as notas do ${bimestre}º bimestre de ${anoLetivo}${turmaId ? ' para a turma selecionada' : ' para todas as turmas'} e reabre os fechamentos desse período. Use somente para eliminar dados de demonstração ou lançamentos indevidos.`}
        confirmText="Sim, limpar período"
        tone="danger"
        loading={processing}
        onCancel={() => !processing && setLimpezaConfirmOpen(false)}
        onConfirm={limparNotasDoPeriodo}
      />
    </>
  );
}
