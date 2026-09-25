import { useEffect, useState } from 'react';
import { api } from './api.js';
import { getSession } from './session.js';

/**
 * Dados do aluno logado.
 *
 * O id vem do claim `sub` do JWT (ver lib/session.js). Com ele buscamos
 * `GET /alunos/{id}`, que traz nome, matrícula e a turma — necessário para
 * as telas de notas, faltas, horários e ocorrências.
 */
export default function useAluno() {
  const session = getSession();
  const alunoId = session?.vinculoId;

  const [aluno, setAluno] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!alunoId) {
      setLoading(false);
      setError('Sessão do aluno não encontrada. Faça login novamente.');
      return;
    }
    let active = true;
    setLoading(true);
    api
      .get(`/alunos/${alunoId}`)
      .then((data) => {
        if (!active) return;
        setAluno(data);
        setError(null);
      })
      .catch((err) => {
        if (!active) return;
        setAluno(null);
        setError(err.message || 'Não foi possível carregar os dados do aluno.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [alunoId]);

  return { alunoId, aluno, turmaId: aluno?.turmaId ?? null, loading, error };
}
