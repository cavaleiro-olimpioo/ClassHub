import { useMemo, useState } from 'react';
import CrudPage from '../../components/CrudPage.jsx';
import { Button } from '../../components/ui.jsx';
import { useToast } from '../../components/ToastProvider.jsx';
import { api } from '../../lib/api.js';
import useReference, { toOptions } from '../../lib/useReference.js';
import { formatDate } from '../../lib/format.js';

export default function AdminAlunos() {
  const toast = useToast();
  const { data: turmas, reload: reloadTurmas } = useReference('/turmas');
  const [refreshKey, setRefreshKey] = useState(0);

  const turmaOptions = useMemo(() => toOptions(turmas), [turmas]);

  /** PUT /alunos/{id}/turma — endpoint dedicado para remanejamento. */
  async function handleMoveTurma(row, turmaId) {
    if (!turmaId) return;
    try {
      await api.put(`/alunos/${row.id}/turma`, { turmaId: Number(turmaId) });
      toast.success(`Turma de ${row.nome} atualizada com sucesso no 67! Bora bill!`);
      setRefreshKey((k) => k + 1);
    } catch (error) {
      toast.error(error.message || 'Lá ele! Não foi possível alterar a turma na resenha.');
    }
  }

  return (
    <CrudPage
      title="Gerenciar Alunos Amostradinhos 67"
      subtitle="Cadastre, edite e organize os alunos na resenha com Jennifer, Bora Bill e Floyd. Lá ele!"
      icon="students"
      endpoint="/alunos"
      createLabel="Novo Aluno Amostradinho"
      cardTitle="Lista de Alunos da Resenha 67"
      searchKeys={['nome', 'email', 'matricula']}
      deletable
      refreshKey={refreshKey}
      filters={[{ key: 'turmaId', label: 'Turma da Resenha', options: turmaOptions }]}
      columns={[
        { key: 'nome', label: 'Nome', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'matricula', label: 'Matrícula', className: 'mono' },
        { key: 'email', label: 'E-mail', className: 'cell-muted' },
        { key: 'dataNascimento', label: 'Nascimento', render: (v) => formatDate(v) },
        { key: 'turma', label: 'Turma', render: (_v, row) => row.turma?.nome || row.turmaNome || <span className="text-muted">Sem turma na resenha</span> },
        {
          key: '__turmaMove',
          label: 'Mover para',
          width: 170,
          render: (_v, row) => (
            <select
              className="select"
              defaultValue=""
              onChange={(e) => handleMoveTurma(row, e.target.value)}
              aria-label={`Alterar turma de ${row.nome}`}
            >
              <option value="">Selecionar...</option>
              {turmaOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          )
        }
      ]}
      formFields={[
        { name: 'nome', label: 'Nome completo', required: true, placeholder: 'Jennifer Floyd da Silva 67' },
        { name: 'email', label: 'E-mail', type: 'email', required: true, placeholder: 'jennifer.floyd@phonkhub.local' },
        { name: 'matricula', label: 'Matrícula', required: true, placeholder: '67001' },
        { name: 'dataNascimento', label: 'Data de Nascimento', type: 'date', required: true },
        {
          name: 'turmaId',
          label: 'Turma',
          type: 'select',
          required: true,
          placeholder: 'Selecione a turma da resenha...',
          options: turmaOptions
        }
      ]}
      toForm={(row) => ({
        nome: row.nome || '',
        email: row.email || '',
        matricula: row.matricula || '',
        dataNascimento: row.dataNascimento || '',
        turmaId: row.turmaId ?? row.turma?.id ?? ''
      })}
      toPayload={(values) => ({
        nome: values.nome,
        email: values.email,
        matricula: values.matricula,
        dataNascimento: values.dataNascimento,
        turmaId: values.turmaId ? Number(values.turmaId) : null
      })}
      extraActions={
        <Button variant="secondary" size="sm" icon="refresh" onClick={reloadTurmas}>
          Atualizar turmas (Bora Bill)
        </Button>
      }
    />
  );
}
