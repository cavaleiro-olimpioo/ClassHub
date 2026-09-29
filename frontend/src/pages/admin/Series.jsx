import CrudPage from '../../components/CrudPage.jsx';
import { StatusBadge } from '../../components/ui.jsx';

const NIVEIS = [
  { value: 'ENSINO_FUNDAMENTAL', label: 'Ensino Fundamental 67' },
  { value: 'ENSINO_MEDIO', label: 'Ensino Médio Amostradinho' }
];

export default function AdminSeries() {
  return (
    <CrudPage
      title="Gerenciar Séries do 67"
      subtitle="Defina as séries de ensino e a resenha disponível. As turmas são vinculadas a uma série. Eitxha!"
      icon="school"
      endpoint="/series"
      createLabel="Nova Série 67"
      cardTitle="Séries Cadastradas na Resenha"
      searchKeys={['nome', 'nivel']}
      deletable
      columns={[
        { key: 'nome', label: 'Série da Resenha', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'nivel', label: 'Nível', render: (v) => (v ? <StatusBadge status={v} /> : '-') }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome da série', required: true, placeholder: '67º Ano Amostradinho' },
        { name: 'nivel', label: 'Nível de ensino', type: 'select', required: true, placeholder: 'Selecione o nível...', options: NIVEIS }
      ]}
      toForm={(row) => ({ nome: row.nome || '', nivel: row.nivel || '' })}
      toPayload={(values) => ({ nome: values.nome, nivel: values.nivel })}
    />
  );
}
