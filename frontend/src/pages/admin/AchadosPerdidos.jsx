import { useCallback, useState } from 'react';
import CrudPage from '../../components/CrudPage.jsx';
import { Badge, Button, StatusBadge } from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import { CATEGORIAS_ACHADOS, formatDate, todayISO } from '../../lib/format.js';

const CATEGORIA_OPTIONS = CATEGORIAS_ACHADOS.map((categoria) => ({ value: categoria, label: categoria }));

export default function AdminAchadosPerdidos() {
  const toast = useToast();
  const [refreshKey, setRefreshKey] = useState(0);

  const handleDevolver = useCallback(
    async (row) => {
      try {
        await api.put(`/achados-perdidos/${row.id}/devolver`);
        toast.success('Item marcado como devolvido no 67! Bora bill!');
        setRefreshKey((k) => k + 1);
      } catch (error) {
        toast.error(error.message || 'Lá ele! Não foi possível devolver o item.');
      }
    },
    [toast]
  );

  return (
    <CrudPage
      title="Achados e Perdidos Jennifer"
      subtitle="Registre objetos da resenha perdidos e encontrados no 67, e marque a devolução pro Bora Bill e Floyd."
      icon="search"
      endpoint="/achados-perdidos"
      createLabel="Novo Item do Amostradinho"
      cardTitle="Registros Oficiais de Achados e Perdidos do 67"
      searchKeys={['descricao', 'localEncontrado']}
      deletable
      refreshKey={refreshKey}
      filters={[
        { key: 'categoria', label: 'Categoria', options: CATEGORIA_OPTIONS },
        {
          key: 'status',
          label: 'Status',
          options: [
            { value: 'NAO_REIVINDICADO', label: 'Não Reivindicado' },
            { value: 'DEVOLVIDO', label: 'Devolvido' }
          ]
        }
      ]}
      rowActions={(row) =>
        row.status !== 'DEVOLVIDO' ? (
          <Button variant="soft" size="sm" icon="check" onClick={() => handleDevolver(row)} title="Marcar como devolvido">
            Devolver (Bora Bill)
          </Button>
        ) : null
      }
      columns={[
        {
          key: 'descricao',
          label: 'Descrição da Resenha',
          render: (v) => <span className="cell-strong">{v}</span>
        },
        { key: 'categoria', label: 'Categoria', render: (v) => <Badge tone="info">{v || '-'}</Badge>, width: 140 },
        { key: 'localEncontrado', label: 'Local Encontrado' },
        { key: 'data', label: 'Data', className: 'mono', render: (v) => formatDate(v), width: 120 },
        { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} />, width: 150 }
      ]}
      formFields={[
        { name: 'descricao', label: 'Descrição do item', required: true, placeholder: 'Ex.: casaco 67 com foto do Floyd e Jennifer', full: true },
        { name: 'categoria', label: 'Categoria', type: 'select', required: true, placeholder: 'Selecione a categoria...', options: CATEGORIA_OPTIONS },
        { name: 'data', label: 'Data', type: 'date', required: true, value: todayISO() },
        { name: 'localEncontrado', label: 'Local Encontrado', required: true, placeholder: 'Ex.: Quadra da Resenha, Biblioteca Lá Ele', full: true }
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
