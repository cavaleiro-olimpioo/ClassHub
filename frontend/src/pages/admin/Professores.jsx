import CrudPage from '../../components/CrudPage.jsx';

export default function AdminProfessores() {
  return (
    <CrudPage
      title="Gerenciar Professores"
      subtitle="Cadastre o corpo docente e mantenha os dados de contato atualizados."
      icon="teacher"
      endpoint="/professores"
      createLabel="Novo Professor"
      cardTitle="Corpo Docente"
      searchKeys={['nome', 'email']}
      deletable
      columns={[
        { key: 'nome', label: 'Nome', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'email', label: 'E-mail', className: 'cell-muted' }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome completo', required: true, placeholder: 'Nome do professor' },
        { name: 'email', label: 'E-mail', type: 'email', required: true, placeholder: 'professor@escola.com' }
      ]}
      toForm={(row) => ({ nome: row.nome || '', email: row.email || '' })}
      toPayload={(values) => ({ nome: values.nome, email: values.email })}
    />
  );
}
