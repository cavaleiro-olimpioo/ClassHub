import CrudPage from '../../components/CrudPage.jsx';
import { StatusBadge } from '../../components/ui.jsx';
import { formatDate, todayISO, TIPOS_CALENDARIO } from '../../lib/format.js';

const TIPO_OPTIONS = TIPOS_CALENDARIO.map((tipo) => ({ value: tipo, label: tipo }));

export default function AdminCalendario() {
  return (
    <CrudPage
      title="Calendário Escolar"
      subtitle="Registre dias letivos, feriados, recesses e eventos que se aplicam a toda a escola."
      icon="calendar"
      endpoint="/calendario"
      createLabel="Novo Registro"
      cardTitle="Registros Oficiais do Calendário"
      searchKeys={['titulo', 'descricao']}
      deletable
      pageSize={10}
      columns={[
        { key: 'data', label: 'Data', render: (v) => <span className="cell-strong mono">{formatDate(v)}</span>, width: 120 },
        { key: 'anoLetivo', label: 'Ano Letivo', className: 'mono', width: 110 },
        { key: 'tipo', label: 'Tipo', render: (v) => <StatusBadge status={v} />, width: 140 },
        { key: 'titulo', label: 'Título' },
        { key: 'descricao', label: 'Descrição', className: 'cell-muted' }
      ]}
      formFields={[
        { name: 'data', label: 'Data', type: 'date', required: true, value: todayISO() },
        { name: 'anoLetivo', label: 'Ano Letivo', type: 'number', required: true, min: 2000, value: new Date().getFullYear() },
        { name: 'tipo', label: 'Tipo', type: 'select', required: true, placeholder: 'Selecione...', options: TIPO_OPTIONS },
        { name: 'titulo', label: 'Título', required: true, placeholder: 'Feriado Nacional', full: true },
        { name: 'descricao', label: 'Descrição', type: 'textarea', placeholder: 'Detalhes do evento (opcional)', full: true }
      ]}
      toForm={(row) => ({
        data: row.data || '',
        anoLetivo: row.anoLetivo ?? new Date().getFullYear(),
        tipo: row.tipo || '',
        titulo: row.titulo || '',
        descricao: row.descricao || ''
      })}
      toPayload={(values) => ({
        data: values.data,
        anoLetivo: Number(values.anoLetivo),
        tipo: values.tipo,
        titulo: values.titulo,
        descricao: values.descricao || null
      })}
    />
  );
}
