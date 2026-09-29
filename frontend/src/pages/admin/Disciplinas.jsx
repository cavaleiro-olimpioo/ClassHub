import CrudPage from '../../components/CrudPage.jsx';

export default function AdminDisciplinas() {
  return (
    <CrudPage
      title="Gerenciar Disciplinas Lá Ele"
      subtitle="Cadastre as disciplinas da resenha 67 e suas cargas horárias do Floyd. Eitcha!"
      icon="book"
      endpoint="/disciplinas"
      createLabel="Nova Disciplina Amostradinha"
      cardTitle="Disciplinas da Resenha 67"
      searchKeys={['nome']}
      deletable
      columns={[
        { key: 'nome', label: 'Disciplina da Resenha', render: (v) => <span className="cell-strong">{v}</span> },
        {
          key: 'cargaHoraria',
          label: 'Carga Horária (67)',
          className: 'num',
          render: (v) => (v ? `${v} h` : '-')
        }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome da disciplina', required: true, placeholder: 'Matemática da Resenha 67' },
        {
          name: 'cargaHoraria',
          label: 'Carga Horária (horas)',
          type: 'number',
          required: true,
          min: 1,
          placeholder: '67'
        }
      ]}
      toForm={(row) => ({ nome: row.nome || '', cargaHoraria: row.cargaHoraria ?? '' })}
      toPayload={(values) => ({ nome: values.nome, cargaHoraria: Number(values.cargaHoraria) })}
    />
  );
}
