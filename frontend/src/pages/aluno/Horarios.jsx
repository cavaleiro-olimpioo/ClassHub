import ScheduleGrid from '../../components/ScheduleGrid.jsx';
import { Loading } from '../../components/ui.jsx';
import useAluno from '../../lib/useAluno.js';

export default function AlunoHorarios() {
  const { turmaId, loading, error } = useAluno();

  if (loading) return <Loading message="Carregando sua grade..." />;

  if (error || !turmaId) {
    return (
      <ScheduleGrid
        mode="turma"
        entityId={null}
        title="Meus Horários"
        subtitle="Sua grade de aulas está disponível assim que você for vinculado a uma turma."
      />
    );
  }

  return (
    <ScheduleGrid
      mode="turma"
      entityId={turmaId}
      title="Meus Horários"
      subtitle="Consulte a grade de aulas da sua turma, com disciplina, professor e horário."
    />
  );
}
