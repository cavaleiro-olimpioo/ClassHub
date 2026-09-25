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
      toast.success(`Turma de ${row.nome} atualizada com sucesso!`);
      setRefreshKey((k) => k + 1);
    } catch (error) {
      toast.error(error.message || 'Não foi possível alterar a turma.');
    }
  }

  return (
    <CrudPage
      title="Gerenciar Alunos"
      subtitle="Cadastre, edite e organize os alunos por turma. Use o seletor de turma na tabela para remanejar rapidamente."
      icon="students"
      endpoint="/alunos"
      createLabel="Novo Aluno"
      cardTitle="Lista de Alunos"
      searchKeys={['nome', 'email', 'matricula']}
      deletable
      refreshKey={refreshKey}
      filters={[{ key: 'turmaId', label: 'Turma', options: turmaOptions }]}
      columns={[
        { key: 'nome', label: 'Nome', render: (v) => <span className="cell-strong">{v}</span> },
        { key: 'matricula', label: 'Matrícula', className: 'mono' },
        { key: 'email', label: 'E-mail', className: 'cell-muted' },
        { key: 'dataNascimento', label: 'Nascimento', render: (v) => formatDate(v) },
        { key: 'turma', label: 'Turma', render: (_v, row) => row.turma?.nome || row.turmaNome || <span className="text-muted">Sem turma</span> },
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
        { name: 'nome', label: 'Nome completo', required: true, placeholder: 'Nome do aluno' },
        { name: 'email', label: 'E-mail', type: 'email', required: true, placeholder: 'aluno@escola.com' },
        { name: 'matricula', label: 'Matrícula', required: true, placeholder: '2026001' },
        { name: 'dataNascimento', label: 'Data de Nascimento', type: 'date', required: true },
        {
          name: 'turmaId',
          label: 'Turma',
          type: 'select',
          required: true,
          placeholder: 'Selecione a turma...',
          options: turmaOptions
        }
      ]}
      toForm={(row) => ({
        nome: row.nome || '',
        email: row.email || '',
        matricula: row.matricula || '',
        dataNascimento: row.dataNascimento || '',
        turmaId: row.turmaId || ''
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
          Atualizar turmas
        </Button>
      }
    />
  );
}
