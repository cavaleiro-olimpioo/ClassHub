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
      toast.success('Fechamento de bimestre da resenha 67 concluído com sucesso! Eitcha!', 6000);
      setConfirmOpen(false);
    } catch (error) {
      toast.error(error.message || 'Lá ele! Não foi possível fechar o bimestre na resenha.');
    } finally {
      setProcessing(false);
    }
  }

  return (
    <>
      <PageHead
        title="Fechamento Bimestre 67"
        subtitle="Consolide as notas da resenha, feche o bimestre e libere os boletins 67 para consulta de Jennifer, Bora Bill e Floyd."
      />

      <div className="grid grid--2">
        <Card title="Fechar Bimestre da Resenha 67" icon="clipboard" subtitle="Defina o período letivo que o Bora Bill deseja consolidar">
          <form
            className="stack"
            style={{ gap: 14 }}
            onSubmit={(e) => {
              e.preventDefault();
              setResult(null);
              setConfirmOpen(true);
            }}
          >
            <Field label="Ano Letivo da Resenha" required htmlFor="f-ano">
              <Input id="f-ano" type="number" min={2000} value={anoLetivo} onChange={(e) => setAnoLetivo(e.target.value)} required />
            </Field>

            <Field label="Bimestre 67" required htmlFor="f-bimestre">
              <Select id="f-bimestre" value={bimestre} onChange={(e) => setBimestre(e.target.value)}>
                {BIMESTRES.map((b) => (
                  <option key={b} value={b}>
                    {b}º Bimestre da Resenha 67
                  </option>
                ))}
              </Select>
            </Field>

            <Field label="Turma Específica da Resenha (opcional)" htmlFor="f-turma" help="Deixe em branco para fechar o bimestre de todas as turmas da resenha no ano letivo.">
              <Select id="f-turma" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} placeholder="Todas as Turmas da Resenha">
                {turmaOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Select>
            </Field>

            <Button type="submit" size="lg" block icon="clipboard">
              Fechar Bimestre e Gerar Boletins da Resenha (Bora Bill!)
            </Button>
          </form>
        </Card>

        <div className="stack">
          {processing ? (
            <Card>
              <Loading message="Consolidando notas do 67 e gerando boletins com Floyd..." />
            </Card>
          ) : result ? (
            <Alert tone="success" title="Processamento concluído no 67">
              O {bimestre}º Bimestre da resenha de {anoLetivo} foi oficialmente fechado. {result}
            </Alert>
          ) : (
            <Card title="Como funciona a resenha" icon="info">
              <ol style={{ margin: 0, paddingLeft: 18, color: 'var(--text-soft)', fontSize: 13, lineHeight: 1.8 }}>
                <li>Confira se todas as notas 67 e faltas lá ele do bimestre foram lançadas.</li>
                <li>Selecione o ano letivo e o bimestre desejado.</li>
                <li>Opcionalmente, restrinja o fechamento a uma turma da resenha.</li>
                <li>Bora bill confirmar a operação — os boletins serão consolidados pro amostradinho!</li>
              </ol>
            </Card>
          )}

          <div className="notice-panel">
            <h3 className="notice-panel__title">
              <Icon name="alert" size={17} />
              Atenção Amostradinho
            </h3>
            <ul>
              <li>O fechamento é definitivo para o bimestre 67 selecionado.</li>
              <li>Notas lançadas após o fechamento podem exigir a intervenção do Floyd.</li>
              <li>Lá ele! Confirme com a secretaria da resenha antes de executar.</li>
            </ul>
          </div>
        </div>
      </div>

      <ConfirmDialog
        open={confirmOpen}
        title="Confirmação de fechamento da resenha 67"
        message={`Você está prestes a fechar o ${bimestre}º Bimestre de ${anoLetivo} no 67 ${turmaId ? 'para a turma da resenha' : 'para TODAS as turmas'}. Bora bill! Os boletins serão consolidados para Jennifer, Floyd e Kirk. Deseja prosseguir?`}
        confirmText="Sim, fechar bimestre (Bora Bill!)"
        loading={processing}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={handleConfirm}
      />
    </>
  );
}
