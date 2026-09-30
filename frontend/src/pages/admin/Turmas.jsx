import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Button,
  Card,
  ConfirmDialog,
  DataTable,
  Field,
  Input,
  Modal,
  PageHead,
  Pagination,
  SearchBox,
  Select,
  paginate
} from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import { toOptions } from '../../lib/useReference.js';
import { normalizeText } from '../../lib/format.js';

/** Valores iniciais (vazios) do formulário de nova/edição de turma. */
const EMPTY_TURMA = { nome: '', serieId: '', anoLetivo: new Date().getFullYear() };
/** Valores iniciais (vazios) do formulário de novo vínculo professor ↔ disciplina. */
const EMPTY_VINCULO = { professorId: '', turmaId: '', disciplinaId: '', anoLetivo: new Date().getFullYear() };
/** Quantidade de itens exibidos por página em cada tabela (turmas e vínculos). */
const PAGE_SIZE = 8;

/**
 * Página administrativa de Turmas & Vínculos: permite criar, editar e
 * excluir turmas (por série e ano letivo) e gerenciar os vínculos entre
 * professores, turmas e disciplinas.
 *
 * @returns {JSX.Element} a página de gerenciamento de turmas e vínculos
 */
export default function AdminTurmas() {
  const toast = useToast();

  const [series, setSeries] = useState([]);
  const [turmas, setTurmas] = useState([]);
  const [professores, setProfessores] = useState([]);
  const [disciplinas, setDisciplinas] = useState([]);
  const [vinculos, setVinculos] = useState([]);
  const [loading, setLoading] = useState(true);

  const [turmaTerm, setTurmaTerm] = useState('');
  const [vinculoTerm, setVinculoTerm] = useState('');
  const [turmaPage, setTurmaPage] = useState(1);
  const [vinculoPage, setVinculoPage] = useState(1);

  const [turmaModal, setTurmaModal] = useState(false);
  const [editingTurma, setEditingTurma] = useState(null);
  const [turmaForm, setTurmaForm] = useState(EMPTY_TURMA);
  const [vinculoModal, setVinculoModal] = useState(false);
  const [vinculoForm, setVinculoForm] = useState(EMPTY_VINCULO);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);
  const [removingVinculo, setRemovingVinculo] = useState(null);
  const [removingTurma, setRemovingTurma] = useState(null);

  /** Carrega séries, turmas, professores, disciplinas e vínculos usados na página. */
  const load = useCallback(async () => {
    setLoading(true);
    const [s, t, p, d, v] = await Promise.all([
      api.get('/series').catch(() => []),
      api.get('/turmas').catch(() => []),
      api.get('/professores').catch(() => []),
      api.get('/disciplinas').catch(() => []),
      api.get('/vinculos').catch(() => [])
    ]);
    setSeries(Array.isArray(s) ? s : []);
    setTurmas(Array.isArray(t) ? t : []);
    setProfessores(Array.isArray(p) ? p : []);
    setDisciplinas(Array.isArray(d) ? d : []);
    setVinculos(Array.isArray(v) ? v : []);
    setLoading(false);
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const serieOptions = useMemo(() => toOptions(series), [series]);
  const turmaOptions = useMemo(() => toOptions(turmas, 'id', (t) => `${t.nome} (${t.anoLetivo})`), [turmas]);
  const professorOptions = useMemo(() => toOptions(professores), [professores]);
  const disciplinaOptions = useMemo(() => toOptions(disciplinas), [disciplinas]);

  // Filtros + paginacao client-side
  const filteredTurmas = useMemo(
    () => turmas.filter((t) => normalizeText(t.nome).includes(normalizeText(turmaTerm.trim()))),
    [turmas, turmaTerm]
  );
  const filteredVinculos = useMemo(
    () =>
      vinculos.filter((v) => {
        const needle = normalizeText(vinculoTerm.trim());
        if (!needle) return true;
        return normalizeText([v.professor?.nome, v.turma?.nome, v.disciplina?.nome].join(' ')).includes(needle);
      }),
    [vinculos, vinculoTerm]
  );

  const turmaPageSafe = Math.min(turmaPage, Math.max(1, Math.ceil(filteredTurmas.length / PAGE_SIZE)));
  const vinculoPageSafe = Math.min(vinculoPage, Math.max(1, Math.ceil(filteredVinculos.length / PAGE_SIZE)));

  /** Abre o modal de criação de uma nova turma, com o formulário vazio. */
  function openCreateTurma() {
    setEditingTurma(null);
    setTurmaForm(EMPTY_TURMA);
    setFormError(null);
    setTurmaModal(true);
  }

  /**
   * Abre o modal de edição de uma turma existente, preenchendo o formulário
   * com os dados atuais da turma.
   *
   * @param {object} row registro da turma a ser editada
   */
  function openEditTurma(row) {
    setEditingTurma(row);
    setTurmaForm({ nome: row.nome || '', serieId: row.serieId || '', anoLetivo: row.anoLetivo || new Date().getFullYear() });
    setFormError(null);
    setTurmaModal(true);
  }

  /**
   * Envia o formulário de turma (criação via POST ou atualização via PUT,
   * conforme haja ou não uma turma em edição) e recarrega os dados.
   *
   * @param {import('react').FormEvent} event evento de submit do formulário
   */
  async function saveTurma(event) {
    event.preventDefault();
    setFormError(null);
    setSaving(true);
    try {
      const payload = { nome: turmaForm.nome, serieId: Number(turmaForm.serieId), anoLetivo: Number(turmaForm.anoLetivo) };
      if (editingTurma) {
        await api.put(`/turmas/${editingTurma.id}`, payload);
        toast.success('Turma atualizada!');
      } else {
        await api.post('/turmas', payload);
        toast.success('Turma criada!');
      }
      setTurmaModal(false);
      await load();
    } catch (error) {
      setFormError(error.message || 'Não foi possível salvar a turma.');
    } finally {
      setSaving(false);
    }
  }

  /**
   * Envia o formulário de novo vínculo professor ↔ turma ↔ disciplina para
   * a API e recarrega os dados.
   *
   * @param {import('react').FormEvent} event evento de submit do formulário
   */
  async function saveVinculo(event) {
    event.preventDefault();
    setFormError(null);
    setSaving(true);
    try {
      await api.post('/vinculos', {
        professorId: Number(vinculoForm.professorId),
        turmaId: Number(vinculoForm.turmaId),
        disciplinaId: Number(vinculoForm.disciplinaId),
        anoLetivo: Number(vinculoForm.anoLetivo)
      });
      toast.success('Vínculo criado com sucesso!');
      setVinculoModal(false);
      setVinculoForm(EMPTY_VINCULO);
      await load();
    } catch (error) {
      setFormError(error.message || 'Não foi possível criar o vínculo.');
    } finally {
      setSaving(false);
    }
  }

  /** Remove o vínculo atualmente selecionado para exclusão (`removingVinculo`) e recarrega os dados. */
  async function removeVinculo() {
    if (!removingVinculo) return;
    try {
      await api.delete(`/vinculos/${removingVinculo.id}`);
      toast.success('Vínculo removido.');
      setRemovingVinculo(null);
      await load();
    } catch (error) {
      toast.error(error.message || 'Não foi possível remover o vínculo.');
    }
  }

  /** Exclui a turma atualmente selecionada para exclusão (`removingTurma`) e recarrega os dados. */
  async function removeTurma() {
    if (!removingTurma) return;
    try {
      await api.delete(`/turmas/${removingTurma.id}`);
      toast.success('Turma excluída com sucesso.');
      setRemovingTurma(null);
      await load();
    } catch (error) {
      toast.error(error.message || 'Não foi possível excluir a turma.');
    }
  }

  return (
    <>
      <PageHead
        title="Turmas & Vínculos"
        subtitle="Crie as turmas por série e ano letivo, e associe professores às disciplinas de cada turma."
      />

      <div className="stack">
        <Card
          title="Turmas Cadastradas"
          icon="layers"
          subtitle={`${filteredTurmas.length} turma(s)`}
          actions={
            <Button icon="plus" onClick={openCreateTurma}>
              Nova Turma
            </Button>
          }
          flush
          footer={<Pagination page={turmaPageSafe} pageSize={PAGE_SIZE} total={filteredTurmas.length} onPageChange={setTurmaPage} itemLabel="turmas" />}
        >
          <div style={{ padding: '14px 18px' }}>
            <SearchBox value={turmaTerm} onChange={(v) => { setTurmaTerm(v); setTurmaPage(1); }} placeholder="Buscar por nome da turma..." />
          </div>
          <DataTable
            loading={loading}
            rows={paginate(filteredTurmas, turmaPageSafe, PAGE_SIZE)}
            emptyIcon="layers"
            emptyTitle="Nenhuma turma cadastrada"
            emptySubtitle="Crie a primeira turma para começar a montar a grade."
            columns={[
              { key: 'nome', label: 'Turma', render: (v) => <span className="cell-strong">{v}</span> },
              { key: 'serie', label: 'Série', render: (v) => v?.nome || '-' },
              { key: 'anoLetivo', label: 'Ano Letivo', className: 'mono' },
              {
                key: '__actions',
                label: 'Ações',
                width: 120,
                render: (_v, row) => (
                  <div className="actions">
                    <Button variant="secondary" size="sm" icon="edit" onClick={() => openEditTurma(row)} aria-label={`Editar ${row.nome}`} />
                    <Button variant="ghost" size="sm" icon="trash" onClick={() => setRemovingTurma(row)} aria-label={`Excluir ${row.nome}`} />
                  </div>
                )
              }
            ]}
          />
        </Card>

        <Card
          title="Vínculos Professor ↔ Disciplina"
          icon="link"
          subtitle={`${filteredVinculos.length} vínculo(s) no ano letivo`}
          actions={
            <Button
              icon="plus"
              onClick={() => {
                setFormError(null);
                setVinculoModal(true);
              }}
            >
              Novo Vínculo
            </Button>
          }
          flush
          footer={<Pagination page={vinculoPageSafe} pageSize={PAGE_SIZE} total={filteredVinculos.length} onPageChange={setVinculoPage} itemLabel="vínculos" />}
        >
          <div style={{ padding: '14px 18px' }}>
            <SearchBox value={vinculoTerm} onChange={(v) => { setVinculoTerm(v); setVinculoPage(1); }} placeholder="Buscar por professor, turma ou disciplina..." />
          </div>
          <DataTable
            loading={loading}
            rows={paginate(filteredVinculos, vinculoPageSafe, PAGE_SIZE)}
            emptyIcon="link"
            emptyTitle="Nenhum vínculo cadastrado"
            emptySubtitle="Associe um professor a uma disciplina de uma turma."
            columns={[
              { key: 'professor', label: 'Professor', render: (v) => v?.nome || '-' },
              { key: 'turma', label: 'Turma', render: (v) => v?.nome || '-' },
              { key: 'disciplina', label: 'Disciplina', render: (v) => v?.nome || '-' },
              { key: 'anoLetivo', label: 'Ano Letivo', className: 'mono' },
              {
                key: '__actions',
                label: 'Ações',
                width: 70,
                render: (_v, row) => (
                  <div className="actions">
                    <Button variant="ghost" size="sm" icon="trash" onClick={() => setRemovingVinculo(row)} aria-label="Remover vínculo" />
                  </div>
                )
              }
            ]}
          />
        </Card>
      </div>

      {turmaModal && (
        <Modal
          title={editingTurma ? 'Editar Turma' : 'Nova Turma'}
          onClose={() => !saving && setTurmaModal(false)}
          footer={
            <>
              <Button variant="secondary" onClick={() => setTurmaModal(false)} disabled={saving}>
                Cancelar
              </Button>
              <Button form="turma-form" type="submit" loading={saving}>
                Salvar
              </Button>
            </>
          }
        >
          <form id="turma-form" className="form-grid" onSubmit={saveTurma}>
            {formError && (
              <div className="span-all">
                <Alert tone="danger">{formError}</Alert>
              </div>
            )}
            <Field label="Nome da Turma" required htmlFor="t-nome" className="span-all">
              <Input id="t-nome" value={turmaForm.nome} onChange={(e) => setTurmaForm({ ...turmaForm, nome: e.target.value })} placeholder="Ex.: 9º A" required />
            </Field>
            <Field label="Série" required htmlFor="t-serie">
              <Select id="t-serie" value={turmaForm.serieId} onChange={(e) => setTurmaForm({ ...turmaForm, serieId: e.target.value })} placeholder="Selecione..." required>
                {serieOptions.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Ano Letivo" required htmlFor="t-ano">
              <Input id="t-ano" type="number" min={2000} value={turmaForm.anoLetivo} onChange={(e) => setTurmaForm({ ...turmaForm, anoLetivo: e.target.value })} required />
            </Field>
          </form>
        </Modal>
      )}

      {vinculoModal && (
        <Modal
          title="Novo Vínculo"
          onClose={() => !saving && setVinculoModal(false)}
          footer={
            <>
              <Button variant="secondary" onClick={() => setVinculoModal(false)} disabled={saving}>
                Cancelar
              </Button>
              <Button form="vinculo-form" type="submit" loading={saving}>
                Vincular
              </Button>
            </>
          }
        >
          <form id="vinculo-form" className="form-grid" onSubmit={saveVinculo}>
            {formError && (
              <div className="span-all">
                <Alert tone="danger">{formError}</Alert>
              </div>
            )}
            <Field label="Professor" required htmlFor="v-prof">
              <Select id="v-prof" value={vinculoForm.professorId} onChange={(e) => setVinculoForm({ ...vinculoForm, professorId: e.target.value })} placeholder="Selecione..." required>
                {professorOptions.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Turma" required htmlFor="v-turma">
              <Select id="v-turma" value={vinculoForm.turmaId} onChange={(e) => setVinculoForm({ ...vinculoForm, turmaId: e.target.value })} placeholder="Selecione..." required>
                {turmaOptions.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Disciplina" required htmlFor="v-disc">
              <Select id="v-disc" value={vinculoForm.disciplinaId} onChange={(e) => setVinculoForm({ ...vinculoForm, disciplinaId: e.target.value })} placeholder="Selecione..." required>
                {disciplinaOptions.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Ano Letivo" required htmlFor="v-ano">
              <Input id="v-ano" type="number" min={2000} value={vinculoForm.anoLetivo} onChange={(e) => setVinculoForm({ ...vinculoForm, anoLetivo: e.target.value })} required />
            </Field>
          </form>
        </Modal>
      )}

      <ConfirmDialog
        open={Boolean(removingVinculo)}
        title="Remover vínculo"
        message={`Remover o vínculo entre ${removingVinculo?.professor?.nome || '?'} e ${removingVinculo?.disciplina?.nome || '?'} na turma ${removingVinculo?.turma?.nome || '?'}?`}
        confirmText="Remover"
        tone="danger"
        onCancel={() => setRemovingVinculo(null)}
        onConfirm={removeVinculo}
      />

      <ConfirmDialog
        open={Boolean(removingTurma)}
        title="Excluir turma"
        message={`Tem certeza que deseja excluir a turma "${removingTurma?.nome}"? Os vínculos associados a ela também serão removidos.`}
        confirmText="Excluir"
        tone="danger"
        onCancel={() => setRemovingTurma(null)}
        onConfirm={removeTurma}
      />
    </>
  );
}
