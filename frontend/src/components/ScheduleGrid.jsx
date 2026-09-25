import { useEffect, useMemo, useState } from 'react';
import Icon from './Icon.jsx';
import { Card, EmptyState, Loading, PageHead, Select } from './ui.jsx';
import { api } from '../lib/api.js';
import { WEEKDAYS, WEEKDAYS_SHORT } from '../lib/format.js';

const WEEK = [1, 2, 3, 4, 5];

/**
 * Grade de horarios somente-leitura, compartilhada pelas telas
 * "Meus Horarios" (professor) e "Meus Horarios" (aluno).
 *
 * @param mode      'professor' -> GET /horarios/professor/{id}
 *                 'turma'     -> GET /horarios/turma/{id}
 * @param entityId  id do professor ou da turma
 * @param turmas    lista de turmas para o filtro (apenas mode='professor')
 */
export default function ScheduleGrid({ mode, entityId, turmas = [], title = 'Meus Horários', subtitle }) {
  const [selectedTurma, setSelectedTurma] = useState('');
  const [horarios, setHorarios] = useState([]);
  const [loading, setLoading] = useState(true);

  // No modo professor, o endpoint busca todos os horarios do professor e filtra localmente por turma se selecionada
  const endpoint = useMemo(() => {
    if (!entityId) return null;
    return mode === 'professor' ? `/horarios/professor/${entityId}` : `/horarios/turma/${entityId}`;
  }, [mode, entityId]);

  useEffect(() => {
    if (!endpoint) {
      setHorarios([]);
      setLoading(false);
      return;
    }
    let active = true;
    setLoading(true);
    api
      .get(endpoint)
      .then((data) => {
        if (active) setHorarios(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setHorarios([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [endpoint]);

  const filteredHorarios = useMemo(() => {
    if (mode === 'professor' && selectedTurma) {
      return horarios.filter((h) => String(h.turmaId ?? h.turma?.id) === String(selectedTurma));
    }
    return horarios;
  }, [mode, selectedTurma, horarios]);

  const byDay = useMemo(() => {
    const map = new Map(WEEK.map((d) => [d, []]));
    filteredHorarios.forEach((item) => {
      const day = Number(item.diaSemana);
      if (!map.has(day)) map.set(day, []);
      map.get(day).push(item);
    });
    map.forEach((list) => list.sort((a, b) => String(a.horaInicio).localeCompare(String(b.horaInicio))));
    return map;
  }, [filteredHorarios]);

  return (
    <>
      <PageHead title={title} subtitle={subtitle} />

      {mode === 'professor' && turmas.length > 0 && (
        <Card title="Filtros de Consulta" icon="filter" className="stack" >
          <div style={{ maxWidth: 280 }}>
            <Select value={selectedTurma} onChange={(e) => setSelectedTurma(e.target.value)} placeholder="Todas as turmas" aria-label="Turma">
              {turmas.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          </div>
        </Card>
      )}

      {mode === 'professor' && turmas.length > 0 && <div style={{ height: 16 }} />}

      <Card title="Grade Semanal" icon="clock" subtitle={`${horarios.length} aula(s) na semana`}>
        {!effectiveId ? (
          <EmptyState icon="clock" title="Nenhuma turma vinculada" subtitle="Você ainda não possui turmas vinculadas neste ano letivo." />
        ) : loading ? (
          <Loading />
        ) : horarios.length === 0 ? (
          <EmptyState icon="clock" title="Nenhum horário cadastrado" subtitle="Não há aulas registradas para este período." />
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
                    <span className="badge badge--neutral">{items.length}</span>
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
                        <strong style={{ fontSize: 13, display: 'block' }}>{item.disciplina?.nome || item.disciplinaNome || 'Disciplina'}</strong>
                        <div className="text-muted" style={{ fontSize: 12 }}>
                          {WEEKDAYS_SHORT[day]} · {item.horaInicio} – {item.horaFim}
                        </div>
                        {mode === 'professor' ? (
                          <div style={{ fontSize: 12.5, color: 'var(--text-soft)' }}>{item.turma?.nome || item.turmaNome || '—'}</div>
                        ) : (
                          <div style={{ fontSize: 12.5, color: 'var(--text-soft)' }}>{item.professor?.nome || item.professorNome || '—'}</div>
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
