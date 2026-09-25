import { useCallback, useEffect, useMemo, useState } from 'react';
import Icon from '../../components/Icon.jsx';
import {
  Alert,
  Button,
  Card,
  ConfirmDialog,
  EmptyState,
  Field,
  Input,
  Loading,
  Modal,
  PageHead,
  Select
} from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import { toOptions } from '../../lib/useReference.js';
import { WEEKDAYS, WEEKDAYS_SHORT } from '../../lib/format.js';

const EMPTY_FORM = { turmaId: '', diaSemana: '1', horaInicio: '', horaFim: '', disciplinaId: '', professorId: '' };
const WEEK = [1, 2, 3, 4, 5];

export default function AdminHorarios() {
  const toast = useToast();

  const [turmas, setTurmas] = useState([]);
  const [disciplinas, setDisciplinas] = useState([]);
  const [professores, setProfessores] = useState([]);
  const [loading, setLoading] = useState(true);

  const [selectedTurma, setSelectedTurma] = useState('');
  const [horarios, setHorarios] = useState([]);
  const [loadingGrid, setLoadingGrid] = useState(false);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);
  const [removing, setRemoving] = useState(null);

  const loadSupport = useCallback(async () => {
    setLoading(true);
    const [t, d, p] = await Promise.all([
      api.get('/turmas').catch(() => []),
      api.get('/disciplinas').catch(() => []),
      api.get('/professores').catch(() => [])
    ]);
    setTurmas(Array.isArray(t) ? t : []);
    setDisciplinas(Array.isArray(d) ? d : []);
    setProfessores(Array.isArray(p) ? p : []);
    setLoading(false);
  }, []);

  useEffect(() => {
    loadSupport();
  }, [loadSupport]);

  const reloadGrid = useCallback(async () => {
    if (!selectedTurma) {
      setHorarios([]);
      return;
    }
    setLoadingGrid(true);
    try {
      const data = await api.get(`/horarios/turma/${selectedTurma}`);
      setHorarios(Array.isArray(data) ? data : []);
    } catch {
      setHorarios([]);
    } finally {
      setLoadingGrid(false);
    }
  }, [selectedTurma]);

  useEffect(() => {
    reloadGrid();
  }, [reloadGrid]);

  // Agrupa por dia da semana
  const byDay = useMemo(() => {
    const map = new Map(WEEK.map((day) => [day, []]));
    horarios.forEach((item) => {
      const day = Number(item.diaSemana);
      if (!map.has(day)) map.set(day, []);
      map.get(day).push(item);
    });
    map.forEach((list) => list.sort((a, b) => String(a.horaInicio).localeCompare(String(b.horaInicio))));
    return map;
  }, [horarios]);

  function openCreate(day) {
    setForm({ ...EMPTY_FORM, turmaId: selectedTurma, diaSemana: String(day || 1) });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSave(event) {
    event.preventDefault();
    setFormError(null);
    setSaving(true);
    try {
      await api.post('/horarios', {
        turmaId: Number(form.turmaId),
        diaSemana: Number(form.diaSemana),
        horaInicio: form.horaInicio,
        horaFim: form.horaFim,
        disciplinaId: Number(form.disciplinaId),
        professorId: Number(form.professorId)
      });
      toast.success('Horário criado com sucesso!');
      setModalOpen(false);
      await reloadGrid();
    } catch (error) {
      setFormError(error.message || 'Não foi possível salvar o horário.');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!removing) return;
    try {
      await api.delete(`/horarios/${removing.id}`);
      toast.success('Horário removido.');
      setRemoving(null);
      await reloadGrid();
    } catch (error) {
      toast.error(error.message || 'Não foi possível remover o horário.');
    }
  }

  if (loading) return <Loading message="Carregando turmas e disciplinas..." />;

  return (
    <>
      <PageHead
        title="Grade de Horários"
        subtitle="Monte a grade semanal de aulas por turma, definindo disciplina, professor e faixa de horário."
      />

      <Card
        title="Filtros de Consulta"
        icon="filter"
        subtitle={selectedTurma ? `${horarios.length} registro(s) na grade` : 'Selecione uma turma para visualizar a grade'}
        actions={
          <>
            <div style={{ minWidth: 220 }}>
              <Select value={selectedTurma} onChange={(e) => setSelectedTurma(e.target.value)} placeholder="Selecione uma turma..." aria-label="Turma">
                {toOptions(turmas).map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Select>
            </div>
            <Button icon="plus" onClick={() => openCreate(1)} disabled={!selectedTurma}>
              Novo Horário
            </Button>
          </>
        }
      >
        {!selectedTurma ? (
          <EmptyState
            icon="clock"
            title="Nenhuma turma selecionada"
            subtitle="Escolha uma turma acima para visualizar e editar a grade de horários."
          />
        ) : loadingGrid ? (
          <Loading />
        ) : horarios.length === 0 ? (
          <EmptyState icon="clock" title="Grade vazia" subtitle="Nenhum horário cadastrado para esta turma." />
        ) : (
          <div className="grid grid--3">
            {WEEK.map((day) => {
              const items = byDay.get(day) || [];
              return (
                <article className="card" key={day}>
                  <header className="card__head" style={{ padding: '12px 14px' }}>
                    <div className="card__title">
                      <Icon name="clock" size={15} />
                      {WEEKDAYS[day]}
                    </div>
                    <div className="card__spacer" />
                    <Button variant="ghost" size="sm" icon="plus" onClick={() => openCreate(day)} aria-label={`Adicionar em ${WEEKDAYS[day]}`} />
                  </header>
                  <div className="card__body" style={{ padding: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
                    {items.length === 0 && <span className="text-muted" style={{ fontSize: 12.5 }}>Sem aulas</span>}
                    {items.map((item) => (
                      <div
                        key={item.id}
                        style={{
                          border: '1px solid var(--border)',
                          borderLeft: '3px solid var(--primary)',
                          borderRadius: 'var(--radius-sm)',
                          padding: '9px 11px',
                          background: 'var(--surface-alt)'
                        }}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
                          <strong style={{ fontSize: 13 }}>{item.disciplina?.nome || item.disciplinaNome || 'Disciplina'}</strong>
                          <button
                            type="button"
                            onClick={() => setRemoving(item)}
                            aria-label="Remover horário"
                            style={{ background: 'none', border: 0, cursor: 'pointer', color: 'var(--danger)', padding: 0, display: 'inline-flex' }}
                          >
                            <Icon name="trash" size={14} />
                          </button>
                        </div>
                        <div className="text-muted" style={{ fontSize: 12 }}>
                          {WEEKDAYS_SHORT[day]} · {item.horaInicio} – {item.horaFim}
                        </div>
                        <div style={{ fontSize: 12.5, color: 'var(--text-soft)' }}>{item.professor?.nome || item.professorNome || '—'}</div>
                      </div>
                    ))}
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </Card>

      {modalOpen && (
        <Modal
          title="Novo Horário"
          onClose={() => !saving && setModalOpen(false)}
          footer={
            <>
              <Button variant="secondary" onClick={() => setModalOpen(false)} disabled={saving}>
                Cancelar
              </Button>
              <Button form="horario-form" type="submit" loading={saving}>
                Salvar
              </Button>
            </>
          }
        >
          <form id="horario-form" className="form-grid" onSubmit={handleSave}>
            {formError && (
              <div className="span-all">
                <Alert tone="danger">{formError}</Alert>
              </div>
            )}
            <Field label="Turma" required htmlFor="h-turma">
              <Select id="h-turma" value={form.turmaId} onChange={(e) => setForm({ ...form, turmaId: e.target.value })} placeholder="Selecione...">
                {toOptions(turmas).map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Dia da Semana" required htmlFor="h-dia">
              <Select id="h-dia" value={form.diaSemana} onChange={(e) => setForm({ ...form, diaSemana: e.target.value })}>
                {WEEK.map((d) => (
                  <option key={d} value={d}>
                    {WEEKDAYS[d]}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Hora Início" required htmlFor="h-ini">
              <Input id="h-ini" type="time" value={form.horaInicio} onChange={(e) => setForm({ ...form, horaInicio: e.target.value })} required />
            </Field>
            <Field label="Hora Fim" required htmlFor="h-fim">
              <Input id="h-fim" type="time" value={form.horaFim} onChange={(e) => setForm({ ...form, horaFim: e.target.value })} required />
            </Field>
            <Field label="Disciplina" required htmlFor="h-disc">
              <Select id="h-disc" value={form.disciplinaId} onChange={(e) => setForm({ ...form, disciplinaId: e.target.value })} placeholder="Selecione...">
                {toOptions(disciplinas).map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Professor" required htmlFor="h-prof">
              <Select id="h-prof" value={form.professorId} onChange={(e) => setForm({ ...form, professorId: e.target.value })} placeholder="Selecione...">
                {toOptions(professores).map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </Select>
            </Field>
          </form>
        </Modal>
      )}

      <ConfirmDialog
        open={Boolean(removing)}
        title="Remover horário"
        message={`Remover o horário de ${removing?.disciplina?.nome || removing?.disciplinaNome || ''} (${removing?.horaInicio}–${removing?.horaFim})?`}
        confirmText="Remover"
        tone="danger"
        onCancel={() => setRemoving(null)}
        onConfirm={handleDelete}
      />
    </>
  );
}
