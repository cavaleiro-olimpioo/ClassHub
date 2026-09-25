import { useCallback, useEffect, useState } from 'react';
import { api } from './api.js';

/**
 * Carrega uma lista de referencia (turmas, series, disciplinas...) para
 * popular <select>. Falhas nao geram toast: apenas devolvem lista vazia,
 * evitando inundar a tela quando um endpoint acessivel ao perfil nao existe.
 */
export default function useReference(endpoint) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const response = await api.get(endpoint);
      setData(Array.isArray(response) ? response : []);
    } catch {
      setData([]);
    } finally {
      setLoading(false);
    }
  }, [endpoint]);

  useEffect(() => {
    load();
  }, [load]);

  return { data, loading, reload: load };
}

/** Converte uma lista de objetos em opcoes de <select>. */
export function toOptions(list, valueKey = 'id', labelFn = (item) => item.nome) {
  return list.map((item) => ({ value: item[valueKey], label: labelFn(item) }));
}
