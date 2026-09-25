import { useEffect, useMemo, useState } from 'react';
import { api } from './api.js';
import { getSession } from './session.js';

/**
 * Vinculos do professor logado.
 *
 * O `LoginResponse` da API nao devolve `vinculoId`; o id do professor e
 * recuperado do claim `sub` do JWT (ver lib/session.js). Com ele, buscamos
 * `GET /vinculos?professorId={id}` para saber turmas e disciplinas.
 */
export function useProfessorVinculos() {
  const session = getSession();
  const professorId = session?.vinculoId;

  const [vinculos, setVinculos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!professorId) {
      setLoading(false);
      setError('Identificador do professor não localizado na sessão.');
      return;
    }
    let active = true;
    setLoading(true);
    api
      .get('/vinculos', { professorId })
      .then((data) => {
        if (!active) return;
        setVinculos(Array.isArray(data) ? data : []);
        setError(null);
      })
      .catch((err) => {
        if (!active) return;
        setVinculos([]);
        setError(err.message || 'Não foi possível carregar seus vínculos.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [professorId]);

  // Turmas únicas vinculadas
  const turmas = useMemo(() => {
    const map = new Map();
    vinculos.forEach((v) => {
      const id = v.turma?.id ?? v.turmaId;
      if (id != null && !map.has(id)) map.set(id, v.turma?.nome || `Turma ${id}`);
    });
    return Array.from(map, ([value, label]) => ({ value, label }));
  }, [vinculos]);

  /** Disciplinas vinculadas a uma turma específica. */
  function disciplinasDaTurma(turmaId) {
    if (!turmaId) return [];
    const seen = new Map();
    vinculos
      .filter((v) => String(v.turma?.id ?? v.turmaId) === String(turmaId))
      .forEach((v) => {
        const id = v.disciplina?.id ?? v.disciplinaId;
        if (id != null && !seen.has(id)) seen.set(id, v.disciplina?.nome || `Disciplina ${id}`);
      });
    return Array.from(seen, ([value, label]) => ({ value, label }));
  }

  return { professorId, vinculos, turmas, disciplinasDaTurma, loading, error };
}
