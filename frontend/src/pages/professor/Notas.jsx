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

const TIPO_LABEL = { PROVA: 'Prova da Resenha', TRABALHO: 'Trabalho do Amostradinho', ATIVIDADE: 'Atividade 67' };

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
      toast.warning('Selecione turma e disciplina da resenha antes de carregar.');
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
      toast.error(error.message || 'Lá ele! Não foi possível carregar as notas.');
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
        toast.warning('Nenhuma nota informada para salvar no 67.');
        return;
      }

      await Promise.all(requests);
      toast.success('Todas as notas da resenha foram salvas com sucesso no 67! Bora bill!');
      setConfirmOpen(false);
      await carregar();
    } catch (error) {
      toast.error(error.message || 'Lá ele! Algumas notas não puderam ser salvas no 67.');
    } finally {
      setSaving(false);
    }
  }

  if (loadingVinculos) return <Loading message="Carregando turmas da resenha 67..." />;

  return (
    <>
      <PageHead
        title="Lançar Notas 67"
        subtitle="Informe as avaliações da turma na resenha. A média do Floyd é calculada com pesos padrão (Prova 5, Trabalho 3, Atividade 2). Bora bill!"
      />

      <Card title="Configurar Avaliação da Resenha 67" icon="edit">
        <div className="form-grid">
          <Field label="Turma da Resenha" required htmlFor="n-turma">
            <Select id="n-turma" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} placeholder="Selecione uma turma do 67...">
              {turmas.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          </Field>

          <Field label="Disciplina Lá Ele" required htmlFor="n-disc">
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

          <Field label="Bimestre 67" required htmlFor="n-bim">
            <Select id="n-bim" value={bimestre} onChange={(e) => setBimestre(e.target.value)}>
              {BIMESTRES.map((b) => (
                <option key={b} value={b}>
                  {b}º Bimestre 67
                </option>
              ))}
            </Select>
          </Field>

          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <Button icon="book" onClick={carregar} disabled={!turmaId || !disciplinaId} block>
              Carregar Alunos e Notas 67
            </Button>
          </div>
        </div>
      </Card>

      <div style={{ height: 16 }} />

      {loading ? (
        <Card>
          <Loading message="Carregando notas da resenha..." />
        </Card>
      ) : !turmaId || !disciplinaId ? (
        <Card>
          <EmptyState icon="edit" title="Selecione turma e disciplina da resenha" subtitle="Escolha a turma e a disciplina para carregar a grade de notas do Floyd." />
        </Card>
      ) : alunos.length === 0 ? (
        <Card>
          <EmptyState icon="students" title="Nenhum aluno nesta turma da resenha" subtitle="Lá ele! Não há alunos amostradinhos matriculados na turma selecionada." />
        </Card>
      ) : (
        <Card
          title="Grade de Notas da Resenha"
          icon="edit"
          subtitle={`${alunos.length} aluno(s) amostradinho(s) — ${bimestre}º Bimestre 67`}
          actions={
            <Button variant="success" icon="check" onClick={() => setConfirmOpen(true)}>
              Salvar Todas as Notas (Bora Bill)
            </Button>
          }
          flush
        >
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th style={{ width: 60 }}>#</th>
                  <th>Aluno Amostradinho</th>
                  {TIPOS_NOTA.map((tipo) => (
                    <th key={tipo} style={{ width: 130 }}>
                      {TIPO_LABEL[tipo]} <span className="text-muted">(peso {PESOS_PADRAO[tipo]})</span>
                    </th>
                  ))}
                  <th style={{ width: 120 }}>Média 67</th>
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

      <Alert tone="info" title="Como a média da resenha é calculada">
        Média do Floyd = (Σ nota × peso) ÷ (Σ pesos). Notas aceitas de 0 a 10. Bora bill tirar 10 na resenha 67, lá ele!
      </Alert>

      <ConfirmDialog
        open={confirmOpen}
        title="Salvar notas da resenha 67"
        message="Todas as notas informadas serão gravadas no PhonkHub 67. Deseja prosseguir, amostradinho?"
        confirmText="Salvar notas (Bora Bill!)"
        loading={saving}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={salvar}
      />
    </>
  );
}
