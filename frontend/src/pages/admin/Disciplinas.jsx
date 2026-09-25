import CrudPage from '../../components/CrudPage.jsx';

export default function AdminDisciplinas() {
  return (
    <CrudPage
      title="Gerenciar Disciplinas"
      subtitle="Cadastre as disciplinas e suas cargas horárias. São usadas nos vínculos, horários e notas."
      icon="book"
      endpoint="/disciplinas"
      createLabel="Nova Disciplina"
      cardTitle="Disciplinas"
      searchKeys={['nome']}
      deletable
      columns={[
        { key: 'nome', label: 'Disciplina', render: (v) => <span className="cell-strong">{v}</span> },
        {
          key: 'cargaHoraria',
          label: 'Carga Horária',
          className: 'num',
          render: (v) => (v ? `${v} h` : '-')
        }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome da disciplina', required: true, placeholder: 'Matemática' },
        {
          name: 'cargaHoraria',
          label: 'Carga Horária (horas)',
          type: 'number',
          required: true,
          min: 1,
          placeholder: '80'
        }
      ]}
      toForm={(row) => ({ nome: row.nome || '', cargaHoraria: row.cargaHoraria ?? '' })}
      toPayload={(values) => ({ nome: values.nome, cargaHoraria: Number(values.cargaHoraria) })}
    />
  );
}
