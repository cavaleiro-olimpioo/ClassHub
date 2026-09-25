import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Alert,
  Button,
  Card,
  ConfirmDialog,
  EmptyState,
  Field,
  Input,
  Loading,
  PageHead,
  Select
} from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import { useProfessorVinculos } from '../../lib/useProfessorVinculos.js';
import { formatDate, todayISO } from '../../lib/format.js';

const STATUS_OPCOES = [
  { value: 'PRESENTE', label: 'P' },
  { value: 'FALTA', label: 'F' },
  { value: 'FALTA_JUSTIFICADA', label: 'FJ' }
];

export default function ProfessorChamada() {
  const toast = useToast();
  const [searchParams] = useSearchParams();
  const { turmas, disciplinasDaTurma, loading: loadingVinculos, error } = useProfessorVinculos();

  const [turmaId, setTurmaId] = useState(searchParams.get('turmaId') || '');
  const [disciplinaId, setDisciplinaId] = useState(searchParams.get('disciplinaId') || '');
  const [data, setData] = useState(todayISO());

  const [alunos, setAlunos] = useState([]);
  const [presencas, setPresencas] = useState({});
  const [loading, setLoading] = useState(false);
  const [naoLetivo, setNaoLetivo] = useState(false);

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [saving, setSaving] = useState(false);

  const disciplinaOpcoes = useMemo(() => disciplinasDaTurma(turmaId), [disciplinasDaTurma, turmaId]);

  // Ao trocar de turma, zera a disciplina se ela nao pertence a nova turma
  useEffect(() => {
    if (disciplinaId && !disciplinaOpcoes.some((d) => String(d.value) === String(disciplinaId))) {
      setDisciplinaId('');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turmaId]);

  // Carrega alunos + presencas ja registradas
  async function carregarAlunos() {
    if (!turmaId || !disciplinaId || !data) {
      toast.warning('Selecione turma, disciplina e data antes de carregar os alunos.');
      return;
    }

    setLoading(true);
    setNaoLetivo(false);
    try {
      const [lista, existentes, diaLetivo] = await Promise.all([
        api.get('/alunos', { turmaId }),
        api.get('/presencas', { turmaId, disciplinaId, data }).catch(() => []),
        // Se a data for feriado/recesso, o dia nao e letivo (bloqueia a edicao)
        api.get('/calendario', { anoLetivo: Number(data.slice(0, 4)), mes: Number(data.slice(5, 7)) }).catch(() => [])
      ]);

      setAlunos(Array.isArray(lista) ? lista : []);

      const mapa = {};
      (Array.isArray(existentes) ? existentes : []).forEach((p) => {
        mapa[p.alunoId] = { status: p.status, justificativa: p.justificativa || '' };
      });
      setPresencas(mapa);

      const bloqueado = (Array.isArray(diaLetivo) ? diaLetivo : []).some(
        (evento) => evento.data === data && ['FERIADO', 'RECESSO'].includes(String(evento.tipo).toUpperCase())
      );
      setNaoLetivo(bloqueado);

      if (bloqueado) toast.warning('A data selecionada não é dia letivo. A chamada está bloqueada.');
    } catch (err) {
      setAlunos([]);
      toast.error(err.message || 'Não foi possível carregar os alunos.');
    } finally {
      setLoading(false);
    }
  }

  function setStatus(alunoId, status) {
    setPresencas((current) => ({
      ...current,
      [alunoId]: {
        status,
        justificativa: status === 'FALTA_JUSTIFICADA' ? current[alunoId]?.justificativa || '' : ''
      }
    }));
  }

  function setJustificativa(alunoId, justificativa) {
    setPresencas((current) => ({
      ...current,
      [alunoId]: { ...(current[alunoId] || { status: 'FALTA_JUSTIFICADA' }), justificativa }
    }));
  }

  function marcarTodos() {
    const next = { ...presencas };
    alunos.forEach((aluno) => {
      next[aluno.id] = { status: 'PRESENTE', justificativa: '' };
    });
    setPresencas(next);
    toast.info('Todos os alunos marcados como presentes.');
  }

  async function salvar() {
    setSaving(true);
    try {
      const payload = Object.entries(presencas)
        .filter(([, value]) => value?.status)
        .map(([alunoId, value]) => ({
          alunoId: Number(alunoId),
          turmaId: Number(turmaId),
          disciplinaId: Number(disciplinaId),
          data,
          status: value.status,
          justificativa: value.status === 'FALTA_JUSTIFICADA' ? value.justificativa || null : null
        }));

      if (payload.length === 0) {
        toast.warning('Marque ao menos um aluno antes de salvar.');
        return;
      }

      await api.post('/presencas/lote', payload);
      toast.success('Chamada salva com sucesso!');
      setConfirmOpen(false);
      await carregarAlunos();
    } catch (err) {
      toast.error(err.message || 'Não foi possível salvar a chamada.');
    } finally {
      setSaving(false);
    }
  }

  if (loadingVinculos) return <Loading message="Carregando suas turmas..." />;

  return (
    <>
      <PageHead title="Realizar Chamada" subtitle="Selecione a turma, a disciplina e a data para registrar a presença da aula." />

      {error && (
        <div style={{ marginBottom: 16 }}>
          <Alert tone="warning" title="Atenção">
            {error}
          </Alert>
        </div>
      )}

      <Card title="Configurar Aula" icon="checkSquare">
        <div className="form-grid">
          <Field label="Turma" required htmlFor="c-turma">
            <Select id="c-turma" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} placeholder="Selecione uma turma...">
              {turmas.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          </Field>

          <Field label="Disciplina" required htmlFor="c-disc">
            <Select
              id="c-disc"
              value={disciplinaId}
              onChange={(e) => setDisciplinaId(e.target.value)}
              placeholder={turmaId ? 'Selecione uma disciplina...' : 'Selecione a turma primeiro'}
              disabled={!turmaId}
            >
              {disciplinaOpcoes.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          </Field>

          <Field label="Data da Aula" required htmlFor="c-data">
            <Input id="c-data" type="date" value={data} onChange={(e) => setData(e.target.value)} />
          </Field>

          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <Button icon="checkSquare" onClick={carregarAlunos} disabled={!turmaId || !disciplinaId} block>
              Carregar Alunos
            </Button>
          </div>
        </div>
      </Card>

      {naoLetivo && (
        <div style={{ marginTop: 16 }}>
          <Alert tone="warning" title="Não é dia letivo">
            A data selecionada está marcada como feriado ou recesso no calendário escolar. A edição da chamada está
            desabilitada.
          </Alert>
        </div>
      )}

      <div style={{ height: 16 }} />

      {loading ? (
        <Card>
          <Loading message="Carregando alunos..." />
        </Card>
      ) : !turmaId || !disciplinaId ? (
        <Card>
          <EmptyState icon="checkSquare" title="Selecione turma e disciplina" subtitle="Escolha a turma e a disciplina para carregar a lista de alunos." />
        </Card>
      ) : alunos.length === 0 ? (
        <Card>
          <EmptyState icon="students" title="Nenhum aluno nesta turma" subtitle="Não há alunos matriculados na turma selecionada." />
        </Card>
      ) : (
        <Card
          title="Lista de Presença"
          icon="students"
          subtitle={`${alunos.length} aluno(s) — ${formatDate(data)}`}
          actions={
            <div className="btn-row">
              <Button variant="secondary" size="sm" icon="check" onClick={marcarTodos} disabled={naoLetivo}>
                Marcar todos
              </Button>
              <Button variant="success" icon="check" onClick={() => setConfirmOpen(true)} disabled={naoLetivo}>
                Salvar Chamada
              </Button>
            </div>
          }
          flush
        >
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th style={{ width: 60 }}>#</th>
                  <th>Aluno</th>
                  <th style={{ width: 90 }}>Matrícula</th>
                  <th style={{ width: 150 }}>Presença</th>
                  <th style={{ width: 250 }}>Justificativa</th>
                </tr>
              </thead>
              <tbody>
                {alunos.map((aluno, index) => {
                  const atual = presencas[aluno.id];
                  return (
                    <tr key={aluno.id}>
                      <td className="num text-muted">{index + 1}</td>
                      <td className="cell-strong">{aluno.nome}</td>
                      <td className="mono cell-muted">{aluno.matricula || '-'}</td>
                      <td>
                        <div className="attendance">
                          {STATUS_OPCOES.map((opcao) => (
                            <button
                              key={opcao.value}
                              type="button"
                              data-status={opcao.value}
                              className={atual?.status === opcao.value ? 'is-on' : ''}
                              onClick={() => setStatus(aluno.id, opcao.value)}
                              disabled={naoLetivo}
                              title={opcao.value}
                            >
                              {opcao.label}
                            </button>
                          ))}
                        </div>
                      </td>
                      <td>
                        {atual?.status === 'FALTA_JUSTIFICADA' ? (
                          <Input
                            value={atual.justificativa || ''}
                            onChange={(e) => setJustificativa(aluno.id, e.target.value)}
                            placeholder="Motivo da falta..."
                            disabled={naoLetivo}
                          />
                        ) : (
                          <span className="text-muted">—</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      <ConfirmDialog
        open={confirmOpen}
        title="Salvar chamada"
        message={`Deseja registrar a chamada do dia ${formatDate(data)} com ${Object.keys(presencas).filter((k) => presencas[k]?.status).length} aluno(s)?`}
        confirmText="Salvar Frequência"
        loading={saving}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={salvar}
      >
        <div style={{ marginTop: 12 }}>
          <Alert tone="info">
            A frequência registrada aparece imediatamente no portal do aluno.
          </Alert>
        </div>
      </ConfirmDialog>
    </>
  );
}
