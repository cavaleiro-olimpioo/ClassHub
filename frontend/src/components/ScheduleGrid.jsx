import { useEffect, useMemo, useState } from 'react';
import Icon from './Icon.jsx';
import { Card, EmptyState, Loading, PageHead, Select } from './ui.jsx';
import { api } from '../lib/api.js';
import { WEEKDAYS, WEEKDAYS_SHORT } from '../lib/format.js';
import { SCHEDULE_BREAK, SCHEDULE_PERIODS } from '../lib/schedule.js';

const WEEK = [1, 2, 3, 4, 5];

/** Grade semanal compartilhada pelas telas de aluno e professor. */
export default function ScheduleGrid({ mode, entityId, turmas = [], title = 'Meus Horários', subtitle }) {
  const [selectedTurma, setSelectedTurma] = useState('');
  const [horarios, setHorarios] = useState([]);
  const [loading, setLoading] = useState(true);

  const endpoint = useMemo(() => {
    if (!entityId) return null;
    return mode === 'professor' ? `/horarios/professor/${entityId}` : `/horarios/turma/${entityId}`;
  }, [mode, entityId]);

  useEffect(() => {
    if (!endpoint) {
      setHorarios([]);
      setLoading(false);
      return undefined;
    }
    let active = true;
    setLoading(true);
    api.get(endpoint)
      .then((data) => { if (active) setHorarios(Array.isArray(data) ? data : []); })
      .catch(() => { if (active) setHorarios([]); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [endpoint]);

  const filteredHorarios = useMemo(() => {
    if (mode === 'professor' && selectedTurma) {
      return horarios.filter((h) => String(h.turmaId ?? h.turma?.id) === String(selectedTurma));
    }
    return horarios;
  }, [mode, selectedTurma, horarios]);

  const byDay = useMemo(() => {
    const map = new Map(WEEK.map((day) => [day, []]));
    filteredHorarios.forEach((item) => {
      const day = Number(item.diaSemana);
      if (map.has(day)) map.get(day).push(item);
    });
    map.forEach((items) => items.sort((a, b) => String(a.horaInicio).localeCompare(String(b.horaInicio))));
    return map;
  }, [filteredHorarios]);

  function periodsForDay(items) {
    const remaining = [...items];
    return SCHEDULE_PERIODS.map((period) => {
      let index = remaining.findIndex((item) => String(item.horaInicio).slice(0, 5) === period.start);
      if (index < 0 && remaining.length) index = 0;
      return { period, item: index >= 0 ? remaining.splice(index, 1)[0] : null };
    });
  }

  const subtitleCount = mode === 'professor'
    ? `${filteredHorarios.length} aula(s) atribuída(s) a você · 6 períodos por dia`
    : `${filteredHorarios.length} aula(s) cadastrada(s) · 30 períodos semanais`;

  return (
    <>
      <PageHead title={title} subtitle={subtitle} />
      {mode === 'professor' && turmas.length > 0 && (
        <Card title="Filtros de Consulta" icon="filter" className="stack">
          <div style={{ maxWidth: 280 }}>
            <Select value={selectedTurma} onChange={(event) => setSelectedTurma(event.target.value)} placeholder="Todas as turmas" aria-label="Turma">
              {turmas.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
            </Select>
          </div>
        </Card>
      )}
      {mode === 'professor' && turmas.length > 0 && <div style={{ height: 16 }} />}
      <Card title="Grade Semanal" icon="clock" subtitle={subtitleCount}>
        {!entityId ? (
          <EmptyState icon="clock" title="Nenhuma turma vinculada" subtitle="Você ainda não possui turmas vinculadas neste ano letivo." />
        ) : loading ? (
          <Loading />
        ) : (
          <div className="grid grid--3">
            {WEEK.map((day) => {
              const periods = periodsForDay(byDay.get(day) || []);
              return (
                <article className="card" key={day}>
                  <header className="card__head" style={{ padding: '12px 14px' }}>
                    <div className="card__title"><Icon name="clock" size={15} />{WEEKDAYS[day]}</div>
                    <div className="card__spacer" />
                    <span className="badge badge--neutral">6</span>
                  </header>
                  <div className="card__body" style={{ padding: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
                    {periods.map(({ period, item }, index) => (
                      <div key={period.number}>
                        <div style={{
                          minHeight: 70,
                          border: '1px solid var(--border)',
                          borderLeft: `3px solid ${item ? 'var(--primary)' : 'var(--border)'}`,
                          borderRadius: 'var(--radius-sm)',
                          padding: '9px 11px',
                          background: 'var(--surface-alt)'
                        }}>
                          <strong style={{ fontSize: 13, display: 'block' }}>
                            {item?.disciplina?.nome || item?.disciplinaNome || `Aula ${period.number}`}
                          </strong>
                          <div className="text-muted" style={{ fontSize: 12 }}>
                            {WEEKDAYS_SHORT[day]} · {period.start} – {period.end}
                          </div>
                          {item ? (
                            <div style={{ fontSize: 12.5, color: 'var(--text-soft)' }}>
                              {mode === 'professor'
                                ? (item.turma?.nome || item.turmaNome || 'Turma')
                                : (item.professor?.nome || item.professorNome || 'Professor')}
                            </div>
                          ) : (
                            <div className="text-muted" style={{ fontSize: 12 }}>Aula a definir</div>
                          )}
                        </div>
                        {index === 2 && (
                          <div className="text-muted" style={{ textAlign: 'center', fontSize: 11, padding: '5px 0 0' }}>
                            Intervalo · {SCHEDULE_BREAK.start}–{SCHEDULE_BREAK.end}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </Card>
    </>
  );
}