import { useMemo } from 'react';
import CrudPage from '../../components/CrudPage.jsx';
import { StatusBadge } from '../../components/ui.jsx';
import { CATEGORIAS_ACHADOS, todayISO } from '../../lib/format.js';

const CATEGORIA_OPTIONS = CATEGORIAS_ACHADOS.map((categoria) => ({ value: categoria, label: categoria }));

/**
 * Mural de Achados e Perdidos do aluno na Resenha 67.
 * Jennifer, Kirk, Floyd, Bora Bill, Amostradinho, Lá Ele!
 */
export default function AlunoAchadosPerdidos() {
  const statusFiltros = useMemo(
    () => [
      { key: 'status', label: 'Status da Resenha', options: [
        { value: 'NAO_REIVINDICADO', label: 'Não Reivindicado (Amostradinho)' },
        { value: 'DEVOLVIDO', label: 'Devolvido pro Bora Bill' }
      ] },
      { key: 'categoria', label: 'Categoria 67', options: CATEGORIA_OPTIONS }
    ],
    []
  );

  return (
    <CrudPage
      title="Mural de Achados e Perdidos Jennifer"
      subtitle="Consulte os objetos da resenha registrados no 67 e reporte itens que você encontrou com Floyd e Bora Bill. Lá ele!"
      icon="search"
      endpoint="/achados-perdidos"
      createLabel="Registrar Objeto do Amostradinho"
      cardTitle="Registros Oficiais de Achados e Perdidos 67"
      searchKeys={['descricao', 'localEncontrado', 'categoria']}
      deletable={false}
      filters={statusFiltros}
      emptyTitle="Nenhum objeto registrado no 67"
      emptySubtitle="Quando algo for encontrado na resenha do 67, cadastre aqui para que a secretaria do Bora Bill localize o dono, lá ele!"
      columns={[
        { key: 'descricao', label: 'Descrição do Objeto', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'categoria', label: 'Categoria', width: 140 },
        { key: 'localEncontrado', label: 'Local Encontrado' },
        { key: 'data', label: 'Data', width: 130, className: 'mono' },
        { key: 'status', label: 'Status', width: 160, render: (v) => <StatusBadge status={v} /> }
      ]}
      formFields={[
        { name: 'descricao', label: 'Descrição do Objeto', required: true, placeholder: 'Ex.: mochila 67 com caderno do Floyd e chaveiro da Jennifer', full: true },
        { name: 'categoria', label: 'Categoria', type: 'select', required: true, placeholder: 'Selecione a categoria...', options: CATEGORIA_OPTIONS },
        { name: 'data', label: 'Data que foi encontrado', type: 'date', required: true, value: todayISO() },
        { name: 'localEncontrado', label: 'Local Encontrado', required: true, placeholder: 'Ex.: Pátio da Resenha, Quadra Lá Ele', full: true }
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
