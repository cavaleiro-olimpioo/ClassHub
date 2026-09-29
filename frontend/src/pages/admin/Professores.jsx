import CrudPage from '../../components/CrudPage.jsx';

export default function AdminProfessores() {
  return (
    <CrudPage
      title="Gerenciar Professores Kirk & Floyd"
      subtitle="Cadastre o corpo docente da resenha 67 e mantenha os dados de contato atualizados. Bora bill, lá ele!"
      icon="teacher"
      endpoint="/professores"
      createLabel="Novo Professor Kirk"
      cardTitle="Corpo Docente da Resenha 67"
      searchKeys={['nome', 'email']}
      deletable
      columns={[
        { key: 'nome', label: 'Nome do Professor', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'email', label: 'E-mail da Resenha', className: 'cell-muted' }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome completo', required: true, placeholder: 'Professor Kirk Floyd 67' },
        { name: 'email', label: 'E-mail', type: 'email', required: true, placeholder: 'kirk.floyd@phonkhub.local' }
      ]}
      toForm={(row) => ({ nome: row.nome || '', email: row.email || '' })}
      toPayload={(values) => ({ nome: values.nome, email: values.email })}
    />
  );
}
