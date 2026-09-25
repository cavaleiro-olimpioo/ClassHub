import ScheduleGrid from '../../components/ScheduleGrid.jsx';
import { Loading } from '../../components/ui.jsx';
import { useProfessorVinculos } from '../../lib/useProfessorVinculos.js';

export default function ProfessorHorarios() {
  const { professorId, turmas, loading } = useProfessorVinculos();

  if (loading) return <Loading message="Carregando seus horários..." />;

  return (
    <ScheduleGrid
      mode="professor"
      entityId={professorId}
      turmas={turmas}
      title="Meus Horários"
      subtitle="Consulte sua grade de aulas por turma e disciplina."
    />
  );
}
