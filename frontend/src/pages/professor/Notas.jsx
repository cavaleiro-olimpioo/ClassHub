import { useEffect, useMemo, useState } from 'react';
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
import { BIMESTRES, calcularMediaPesos, formatGrade, PESOS_PADRAO, TIPOS_NOTA } from '../../lib/format.js';

const TIPO_LABEL = { PROVA: 'Prova', TRABALHO: 'Trabalho', ATIVIDADE: 'Atividade' };

/** Limita a entrada a 0..10 (RN-01 do front estatico). */
function clampNota(value) {
  if (value === '') return '';
  let num = Number(value);
  if (Number.isNaN(num)) return '';
  if (num < 0) num = 0;
  if (num > 10) num = 10;
  return String(num);
}

export default function ProfessorNotas() {
  const toast = useToast();
  const { turmas, disciplinasDaTurma, loading: loadingVinculos } = useProfessorVinculos();

  const [turmaId, setTurmaId] = useState('');
  const [disciplinaId, setDisciplinaId] = useState('');
  const [bimestre, setBimestre] = useState(1);

  const [alunos, setAlunos] = useState([]);
  const [notas, setNotas] = useState({});
  const [loading, setLoading] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [saving, setSaving] = useState(false);

  const disciplinaOpcoes = useMemo(() => disciplinasDaTurma(turmaId), [disciplinasDaTurma, turmaId]);

  useEffect(() => {
    if (disciplinaId && !disciplinaOpcoes.some((d) => String(d.value) === String(disciplinaId))) {
      setDisciplinaId('');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turmaId]);

  async function carregar() {
    if (!turmaId || !disciplinaId) {
      toast.warning('Selecione turma e disciplina antes de carregar.');
      return;
    }
    setLoading(true);
    try {
      const [lista, existentes] = await Promise.all([
        api.get('/alunos', { turmaId }),
        api.get('/notas', { turmaId, disciplinaId, bimestre: Number(bimestre) }).catch(() => [])
      ]);

      const alunosList = Array.isArray(lista) ? lista : [];
      setAlunos(alunosList);

      // Cada aluno possui seu proprio mapa de notas: porAluno[alunoId][TIPO] = { id, valor, peso }
      const porAluno = {};
      alunosList.forEach((aluno) => {
        const mapa = {};
        TIPOS_NOTA.forEach((tipo) => {
          mapa[tipo] = { id: null, valor: '', peso: PESOS_PADRAO[tipo] };
        });
        porAluno[aluno.id] = mapa;
      });

      (Array.isArray(existentes) ? existentes : []).forEach((nota) => {
        const tipo = String(nota.tipo).toUpperCase();
        if (nota.alunoId && porAluno[nota.alunoId] && porAluno[nota.alunoId][tipo]) {
          porAluno[nota.alunoId][tipo] = {
            id: nota.id,
            valor: nota.valor === null ? '' : String(nota.valor),
            peso: nota.peso ?? PESOS_PADRAO[tipo]
          };
        }
      });

      setNotas(porAluno);
    } catch (error) {
      setAlunos([]);
      toast.error(error.message || 'Não foi possível carregar as notas.');
    } finally {
      setLoading(false);
    }
  }

  function setNota(alunoId, tipo, valor) {
    setNotas((current) => {
      const copia = { ...current, [alunoId]: { ...current[alunoId], [tipo]: { ...current[alunoId][tipo], valor: clampNota(valor) } } };
      return copia;
    });
  }

  async function salvar() {
    setSaving(true);
    try {
      const requests = [];
      Object.entries(notas).forEach(([alunoId, porTipo]) => {
        TIPOS_NOTA.forEach((tipo) => {
          const item = porTipo[tipo];
          if (item.valor === '' || item.valor === null) return;
          const body = {
            alunoId: Number(alunoId),
            disciplinaId: Number(disciplinaId),
            turmaId: Number(turmaId),
            bimestre: Number(bimestre),
            tipo,
            valor: Number(Number(item.valor).toFixed(2)),
            peso: item.peso
          };
          requests.push(item.id ? api.put(`/notas/${item.id}`, body) : api.post('/notas', body));
        });
      });

      if (requests.length === 0) {
        toast.warning('Nenhuma nota informada para salvar.');
        return;
      }

      await Promise.all(requests);
      toast.success('Todas as notas foram salvas com sucesso!');
      setConfirmOpen(false);
      await carregar();
    } catch (error) {
      toast.error(error.message || 'Algumas notas não puderam ser salvas. Verifique os valores.');
    } finally {
      setSaving(false);
    }
  }

  if (loadingVinculos) return <Loading message="Carregando suas turmas..." />;

  return (
    <>
      <PageHead
        title="Lançamento de Notas"
        subtitle="Informe as avaliações da turma. A média é calculada automaticamente com os pesos padrão (Prova 5, Trabalho 3, Atividade 2)."
      />

      <Card title="Configurar Avaliação" icon="edit">
        <div className="form-grid">
          <Field label="Turma" required htmlFor="n-turma">
            <Select id="n-turma" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} placeholder="Selecione uma turma...">
              {turmas.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          </Field>

          <Field label="Disciplina" required htmlFor="n-disc">
            <Select
              id="n-disc"
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

          <Field label="Bimestre" required htmlFor="n-bim">
            <Select id="n-bim" value={bimestre} onChange={(e) => setBimestre(e.target.value)}>
              {BIMESTRES.map((b) => (
                <option key={b} value={b}>
                  {b}º Bimestre
                </option>
              ))}
            </Select>
          </Field>

          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <Button icon="book" onClick={carregar} disabled={!turmaId || !disciplinaId} block>
              Carregar Alunos e Notas
            </Button>
          </div>
        </div>
      </Card>

      <div style={{ height: 16 }} />

      {loading ? (
        <Card>
          <Loading message="Carregando notas..." />
        </Card>
      ) : !turmaId || !disciplinaId ? (
        <Card>
          <EmptyState icon="edit" title="Selecione turma e disciplina" subtitle="Escolha a turma e a disciplina para carregar a grade de notas." />
        </Card>
      ) : alunos.length === 0 ? (
        <Card>
          <EmptyState icon="students" title="Nenhum aluno nesta turma" subtitle="Não há alunos matriculados na turma selecionada." />
        </Card>
      ) : (
        <Card
          title="Grade de Notas"
          icon="edit"
          subtitle={`${alunos.length} aluno(s) — ${bimestre}º Bimestre`}
          actions={
            <Button variant="success" icon="check" onClick={() => setConfirmOpen(true)}>
              Salvar Todas as Notas
            </Button>
          }
          flush
        >
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th style={{ width: 60 }}>#</th>
                  <th>Aluno</th>
                  {TIPOS_NOTA.map((tipo) => (
                    <th key={tipo} style={{ width: 130 }}>
                      {TIPO_LABEL[tipo]} <span className="text-muted">(peso {PESOS_PADRAO[tipo]})</span>
                    </th>
                  ))}
                  <th style={{ width: 120 }}>Média</th>
                </tr>
              </thead>
              <tbody>
                {alunos.map((aluno, index) => {
                  const porTipo = notas[aluno.id] || {};
                  const media = calcularMediaPesos(porTipo);
                  return (
                    <tr key={aluno.id}>
                      <td className="num text-muted">{index + 1}</td>
                      <td className="cell-strong">{aluno.nome}</td>
                      {TIPOS_NOTA.map((tipo) => (
                        <td key={tipo}>
                          <Input
                            type="number"
                            min="0"
                            max="10"
                            step="0.01"
                            value={porTipo?.[tipo]?.valor ?? ''}
                            onChange={(e) => setNota(aluno.id, tipo, e.target.value)}
                            placeholder="—"
                            aria-label={`${TIPO_LABEL[tipo]} de ${aluno.nome}`}
                          />
                        </td>
                      ))}
                      <td>
                        {media === null ? (
                          <span className="text-muted">-</span>
                        ) : (
                          <strong className={media >= 6 ? 'text-success' : 'text-danger'}>{formatGrade(media)}</strong>
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

      <div style={{ height: 16 }} />

      <Alert tone="info" title="Como a média é calculada">
        Média = (Σ nota × peso) ÷ (Σ pesos das notas informadas). Notas aceitas de 0 a 10. Aprovação exige média ≥ 6,00 e
        frequência ≥ 75%.
      </Alert>

      <ConfirmDialog
        open={confirmOpen}
        title="Salvar notas"
        message="Todas as notas informadas serão gravadas. Deseja prosseguir?"
        confirmText="Salvar notas"
        loading={saving}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={salvar}
      />
    </>
  );
}
