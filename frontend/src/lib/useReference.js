import { useCallback, useEffect, useState } from 'react';
import { api } from './api.js';

/**
 * Carrega uma lista de referencia (turmas, series, disciplinas...) para
 * popular <select>. Falhas nao geram toast: apenas devolvem lista vazia,
 * evitando inundar a tela quando um endpoint acessivel ao perfil nao existe.
 *
 * @param {string} endpoint caminho do endpoint que retorna a lista de referência
 * @returns {{data: Array<object>, loading: boolean, reload: Function}} a lista carregada, estado de carregamento e função para recarregar
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

/**
 * Converte uma lista de objetos em opções de `<select>` (par `value`/`label`).
 *
 * @param {Array<object>} list lista de objetos a ser convertida
 * @param {string} [valueKey] nome da propriedade usada como valor da opção
 * @param {Function} [labelFn] função que extrai o rótulo de cada item
 * @returns {Array<{value: *, label: string}>} lista de opções prontas para uso em `<select>`
 */
export function toOptions(list, valueKey = 'id', labelFn = (item) => item.nome) {
  return list.map((item) => ({ value: item[valueKey], label: labelFn(item) }));
}
