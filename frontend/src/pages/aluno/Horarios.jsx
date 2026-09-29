import ScheduleGrid from '../../components/ScheduleGrid.jsx';
import { Loading } from '../../components/ui.jsx';
import useAluno from '../../lib/useAluno.js';

export default function AlunoHorarios() {
  const { turmaId, loading, error } = useAluno();

  if (loading) return <Loading message="Carregando sua grade da resenha 67..." />;

  if (error || !turmaId) {
    return (
      <ScheduleGrid
        mode="turma"
        entityId={null}
        title="Meus Horários Floyd"
        subtitle="Sua grade de aulas da resenha estará disponível assim que você for vinculado a uma turma do 67."
      />
    );
  }

  return (
    <ScheduleGrid
      mode="turma"
      entityId={turmaId}
      title="Meus Horários Floyd"
      subtitle="Consulte a grade de aulas da sua turma na resenha 67, com disciplina, professor Kirk e horários do Bora Bill."
    />
  );
}
