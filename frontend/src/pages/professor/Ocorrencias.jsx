import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Button,
  Card,
  DataTable,
  Field,
  Modal,
  PageHead,
  Pagination,
  SearchBox,
  Select,
  StatusBadge,
  Textarea,
  paginate
} from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import { useProfessorVinculos } from '../../lib/useProfessorVinculos.js';
import { normalizeText } from '../../lib/format.js';

const TIPOS = [
  { value: 'ATRASO', label: 'Atraso' },
  { value: 'FALTA', label: 'Falta' },
  { value: 'COMPORTAMENTO', label: 'Comportamento' },
  { value: 'NOTA', label: 'Desempenho acadêmico' },
  { value: 'OUTRO', label: 'Outro' }
];

const EMPTY = { turmaId: '', alunoId: '', tipo: '', descricao: '' };
const PAGE_SIZE = 10;

export default function ProfessorOcorrencias() {
  const toast = useToast();
  const { turmas } = useProfessorVinculos();

  const [ocorrencias, setOcorrencias] = useState([]);
  const [alunos, setAlunos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [turmaFiltro, setTurmaFiltro] = useState('');
  const [alunosDaTurma, setAlunosDaTurma] = useState(null);
  const [term, setTerm] = useState('');
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  // `GET /ocorrencias` so aceita `alunoId`; o filtro por turma e feito no cliente
  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.get('/ocorrencias');
      setOcorrencias(Array.isArray(data) ? data : []);
    } catch (error) {
      setOcorrencias([]);
      toast.error(error.message || 'Não foi possível carregar as ocorrências.');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => {
    load();
  }, [load]);

  // Alunos da turma usada no filtro do cabecalho
  useEffect(() => {
    if (!turmaFiltro) {
      setAlunosDaTurma(null);
      return;
    }
    let active = true;
    api
      .get('/alunos', { turmaId: turmaFiltro })
      .then((data) => {
        if (active) setAlunosDaTurma(Array.isArray(data) ? data.map((a) => a.id) : []);
      })
      .catch(() => {
        if (active) setAlunosDaTurma([]);
      });
    return () => {
      active = false;
    };
  }, [turmaFiltro]);

  // Alunos da turma escolhida dentro do modal
  useEffect(() => {
    if (!modalOpen || !form.turmaId) {
      setAlunos([]);
      return;
    }
    let active = true;
    api
      .get('/alunos', { turmaId: form.turmaId })
      .then((data) => {
        if (active) setAlunos(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setAlunos([]);
      });
    return () => {
      active = false;
    };
  }, [modalOpen, form.turmaId]);

  const filtered = useMemo(() => {
    const needle = normalizeText(term.trim());
    return ocorrencias.filter((item) => {
      if (alunosDaTurma && !alunosDaTurma.includes(item.alunoId)) return false;
      if (needle && !normalizeText(`${item.aluno?.nome || ''} ${item.descricao} ${item.tipo}`).includes(needle)) return false;
      return true;
    });
  }, [ocorrencias, term, alunosDaTurma]);

  const safePage = Math.min(page, Math.max(1, Math.ceil(filtered.length / PAGE_SIZE)));

  async function salvar(event) {
    event.preventDefault();
    setFormError(null);
    if (!form.alunoId || !form.tipo || !form.descricao.trim()) {
      setFormError('Preencha aluno, tipo e descrição.');
      return;
    }
    setSaving(true);
    try {
      await api.post('/ocorrencias', {
        alunoId: Number(form.alunoId),
        tipo: form.tipo,
        descricao: form.descricao.trim()
      });
      toast.success('Ocorrência registrada com sucesso!');
      setModalOpen(false);
      setForm(EMPTY);
      await load();
    } catch (error) {
      setFormError(error.message || 'Não foi possível registrar a ocorrência.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <PageHead title="Ocorrências" subtitle="Registre e acompanhe as ocorrências acadêmicas e comportamentais das suas turmas." />

      <Card
        title="Histórico de Ocorrências"
        icon="alert"
        subtitle={`${filtered.length} registro(s)`}
        actions={
          <>
            <div style={{ minWidth: 180 }}>
              <Select value={turmaFiltro} onChange={(e) => { setTurmaFiltro(e.target.value); setPage(1); }} placeholder="Todas as turmas" aria-label="Turma">
                {turmas.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Select>
            </div>
            <Button icon="plus" onClick={() => { setFormError(null); setModalOpen(true); }}>
              Nova Ocorrência
            </Button>
          </>
        }
        flush
        footer={<Pagination page={safePage} pageSize={PAGE_SIZE} total={filtered.length} onPageChange={setPage} itemLabel="ocorrências" />}
      >
        <div style={{ padding: '14px 18px' }}>
          <SearchBox value={term} onChange={(v) => { setTerm(v); setPage(1); }} placeholder="Buscar por aluno ou descrição..." />
        </div>
        <DataTable
          loading={loading}
          rows={paginate(filtered, safePage, PAGE_SIZE)}
          emptyIcon="alert"
          emptyTitle="Nenhuma ocorrência registrada"
          emptySubtitle="As ocorrências que você registrar aparecerão aqui."
          columns={[
            { key: 'aluno', label: 'Aluno', render: (v) => <span className="cell-strong">{v?.nome || '-'}</span> },
            { key: 'tipo', label: 'Tipo', render: (v) => <StatusBadge status={v} />, width: 150 },
            { key: 'descricao', label: 'Descrição' },
            { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} />, width: 130 }
          ]}
        />
      </Card>

      {modalOpen && (
        <Modal
          title="Nova Ocorrência"
          onClose={() => !saving && setModalOpen(false)}
          footer={
            <>
              <Button variant="secondary" onClick={() => setModalOpen(false)} disabled={saving}>
                Cancelar
              </Button>
              <Button form="ocor-form" type="submit" loading={saving}>
                Registrar
              </Button>
            </>
          }
        >
          <form id="ocor-form" className="form-grid" onSubmit={salvar}>
            {formError && (
              <div className="span-all">
                <Alert tone="danger">{formError}</Alert>
              </div>
            )}
            <Field label="Turma" required htmlFor="o-turma" help="Necessária para selecionar o aluno.">
              <Select id="o-turma" value={form.turmaId} onChange={(e) => setForm({ ...form, turmaId: e.target.value, alunoId: '' })} placeholder="Selecione a turma...">
                {turmas.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Aluno" required htmlFor="o-aluno">
              <Select
                id="o-aluno"
                value={form.alunoId}
                onChange={(e) => setForm({ ...form, alunoId: e.target.value })}
                placeholder={form.turmaId ? 'Selecione o aluno...' : 'Selecione a turma primeiro'}
                disabled={!form.turmaId}
              >
                {alunos.map((aluno) => (
                  <option key={aluno.id} value={aluno.id}>
                    {aluno.nome}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Tipo" required htmlFor="o-tipo" className="span-all">
              <Select id="o-tipo" value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })} placeholder="Selecione o tipo...">
                {TIPOS.map((tipo) => (
                  <option key={tipo.value} value={tipo.value}>
                    {tipo.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Descrição" required htmlFor="o-desc" className="span-all">
              <Textarea
                id="o-desc"
                value={form.descricao}
                onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                placeholder="Descreva o que aconteceu..."
              />
            </Field>
          </form>
        </Modal>
      )}
    </>
  );
}
