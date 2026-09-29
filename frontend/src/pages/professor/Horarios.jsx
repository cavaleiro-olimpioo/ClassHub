import ScheduleGrid from '../../components/ScheduleGrid.jsx';
import { Loading } from '../../components/ui.jsx';
import { useProfessorVinculos } from '../../lib/useProfessorVinculos.js';

export default function ProfessorHorarios() {
  const { professorId, turmas, loading } = useProfessorVinculos();

  if (loading) return <Loading message="Carregando horários do professor Floyd..." />;

  return (
    <ScheduleGrid
      mode="professor"
      entityId={professorId}
      turmas={turmas}
      title="Meus Horários Bora Bill"
      subtitle="Consulte sua grade de aulas da resenha 67 por turma e disciplina com Kirk e Floyd."
    />
  );
}
