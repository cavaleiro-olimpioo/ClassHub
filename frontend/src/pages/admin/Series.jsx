import CrudPage from '../../components/CrudPage.jsx';
import { StatusBadge } from '../../components/ui.jsx';

/** Opções de nível de ensino disponíveis para uma série. */
const NIVEIS = [
  { value: 'ENSINO_FUNDAMENTAL', label: 'Ensino Fundamental' },
  { value: 'ENSINO_MEDIO', label: 'Ensino Médio' }
];

/**
 * Página administrativa de gerenciamento de séries de ensino (listagem,
 * criação, edição e exclusão), construída sobre o {@link CrudPage} genérico.
 *
 * @returns {JSX.Element} a página CRUD de séries
 */
export default function AdminSeries() {
  return (
    <CrudPage
      title="Gerenciar Séries"
      subtitle="Defina as séries de ensino disponíveis. As turmas são vinculadas a uma série."
      icon="school"
      endpoint="/series"
      createLabel="Nova Série"
      cardTitle="Séries Cadastradas"
      searchKeys={['nome', 'nivel']}
      deletable
      columns={[
        { key: 'nome', label: 'Série', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'nivel', label: 'Nível', render: (v) => (v ? <StatusBadge status={v} /> : '-') }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome da série', required: true, placeholder: '9º Ano' },
        { name: 'nivel', label: 'Nível de ensino', type: 'select', required: true, placeholder: 'Selecione...', options: NIVEIS }
      ]}
      toForm={(row) => ({ nome: row.nome || '', nivel: row.nivel || '' })}
      toPayload={(values) => ({ nome: values.nome, nivel: values.nivel })}
    />
  );
}
