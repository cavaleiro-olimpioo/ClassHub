import { useMemo } from 'react';
import CrudPage from '../../components/CrudPage.jsx';
import { StatusBadge } from '../../components/ui.jsx';
import { CATEGORIAS_ACHADOS, todayISO } from '../../lib/format.js';

const CATEGORIA_OPTIONS = CATEGORIAS_ACHADOS.map((categoria) => ({ value: categoria, label: categoria }));

/**
 * Mural de Achados e Perdidos do aluno.
 * Diferente do admin, o aluno apenas consulta e registra objetos
 * encontrados — a devolução é feita pela secretaria.
 */
export default function AlunoAchadosPerdidos() {
  const statusFiltros = useMemo(
    () => [
      { key: 'status', label: 'Status', options: [
        { value: 'NAO_REIVINDICADO', label: 'Não Reivindicado' },
        { value: 'DEVOLVIDO', label: 'Devolvido' }
      ] },
      { key: 'categoria', label: 'Categoria', options: CATEGORIA_OPTIONS }
    ],
    []
  );

  return (
    <CrudPage
      title="Mural de Achados e Perdidos"
      subtitle="Consulte os objetos registrados pela escola e reporte itens que você encontrou."
      icon="search"
      endpoint="/achados-perdidos"
      createLabel="Registrar Objeto"
      cardTitle="Registros Oficiais de Achados e Perdidos"
      searchKeys={['descricao', 'localEncontrado', 'categoria']}
      deletable={false}
      filters={statusFiltros}
      emptyTitle="Nenhum objeto registrado"
      emptySubtitle="Quando algo for encontrado na escola, cadastre aqui para que a secretaria possa localizar o dono."
      columns={[
        { key: 'descricao', label: 'Descrição do Objeto', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'categoria', label: 'Categoria', width: 140 },
        { key: 'localEncontrado', label: 'Local Encontrado' },
        { key: 'data', label: 'Data', width: 130, className: 'mono' },
        { key: 'status', label: 'Status', width: 160, render: (v) => <StatusBadge status={v} /> }
      ]}
      formFields={[
        { name: 'descricao', label: 'Descrição do Objeto', required: true, placeholder: 'Ex.: mochila azul com caderno de matemática', full: true },
        { name: 'categoria', label: 'Categoria', type: 'select', required: true, placeholder: 'Selecione...', options: CATEGORIA_OPTIONS },
        { name: 'data', label: 'Data que foi encontrado', type: 'date', required: true, value: todayISO() },
        { name: 'localEncontrado', label: 'Local Encontrado', required: true, placeholder: 'Ex.: Pátio, Biblioteca...', full: true }
      ]}
      toForm={(row) => ({
        descricao: row.descricao || '',
        categoria: row.categoria || '',
        data: row.data || '',
        localEncontrado: row.localEncontrado || ''
      })}
      toPayload={(values) => ({
        descricao: values.descricao,
        categoria: values.categoria,
        data: values.data,
        localEncontrado: values.localEncontrado
      })}
    />
  );
}
