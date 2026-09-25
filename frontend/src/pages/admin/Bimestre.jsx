import { useState } from 'react';
import Icon from '../../components/Icon.jsx';
import { Alert, Button, Card, ConfirmDialog, Field, Input, Loading, PageHead, Select } from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import useReference, { toOptions } from '../../lib/useReference.js';
import { BIMESTRES } from '../../lib/format.js';

export default function AdminBimestre() {
  const toast = useToast();
  const { data: turmas } = useReference('/turmas');

  const [anoLetivo, setAnoLetivo] = useState(new Date().getFullYear());
  const [bimestre, setBimestre] = useState(1);
  const [turmaId, setTurmaId] = useState('');

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [result, setResult] = useState(null);

  const turmaOptions = toOptions(turmas, 'id', (t) => `${t.nome} (${t.anoLetivo})`);

  async function handleConfirm() {
    setProcessing(true);
    try {
      const body = { anoLetivo: Number(anoLetivo), bimestre: Number(bimestre) };
      if (turmaId) body.turmaId = Number(turmaId);

      const response = await api.post('/boletins/gerar', body);
      setResult(response?.mensagem || 'Boletins gerados com sucesso.');
      toast.success('Fechamento de bimestre concluído com sucesso!', 6000);
      setConfirmOpen(false);
    } catch (error) {
      toast.error(error.message || 'Não foi possível fechar o bimestre.');
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

      <ConfirmDialog
        open={confirmOpen}
        title="Confirmação de fechamento"
        message={`Você está prestes a fechar o ${bimestre}º Bimestre de ${anoLetivo} ${turmaId ? 'para a turma selecionada' : 'para TODAS as turmas'}. Os boletins serão consolidados e liberados para visualização. Deseja prosseguir?`}
        confirmText="Sim, fechar bimestre"
        loading={processing}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={handleConfirm}
      />
    </>
  );
}
