import { useAuth } from '../../contexts/AuthContext';
import { useData } from '../../contexts/DataContext';
import { StatCard } from '../../components/StatCard/StatCard';
import { ChartCard, BarChartWidget, LineChartWidget, PieChartWidget } from '../../components/ChartCard/ChartCard';
import { NoticeCard } from '../../components/NoticeCard/NoticeCard';
import { PageHeader } from '../../components/PageHeader/PageHeader';
import { Users, GraduationCap, UserCheck, ClipboardCheck, AlertTriangle, BookOpen, Clock, FileText, Calendar } from 'lucide-react';
import { calculateAverage, getGradeStatus, getAttendanceStatus } from '../../utils';
import { Link } from 'react-router-dom';
import { Badge } from '../../components/Badge/Badge';

export function DashboardPage() {
  const { user } = useAuth();
  if (user?.role === 'professor') return <ProfessorDashboard />;
  if (user?.role === 'aluno') return <StudentDashboard />;
  return <AdminDashboard />;
}

function AdminDashboard() {
  const { students, teachers, classes, occurrences, announcements, grades } = useData();
  const avgAttendance = Math.round(students.reduce((a, s) => a + s.attendance, 0) / students.length);
  const lowAttendance = students.filter(s => s.attendance < 75).length;

  const studentsByYear = Array.from({ length: 9 }, (_, i) => ({
    ano: `${i + 1}º`,
    alunos: students.filter(s => s.year === i + 1).length,
  }));

  const monthlyAttendance = [
    { mes: 'Mar', freq: 92 }, { mes: 'Abr', freq: 89 }, { mes: 'Mai', freq: 91 },
    { mes: 'Jun', freq: 88 }, { mes: 'Jul', freq: 0 }, { mes: 'Ago', freq: avgAttendance },
  ];

  const classAverages = classes.map(c => {
    const classGrades = grades.filter(g => g.classId === c.id && g.grade !== null);
    const avg = classGrades.length ? Math.round(classGrades.reduce((a, g) => a + (g.grade || 0), 0) / classGrades.length * 10) / 10 : 0;
    return { turma: c.name.replace(' Ano', ''), media: avg };
  });

  const gradeDistribution = [
    { name: '0-4', value: grades.filter(g => g.grade !== null && g.grade < 5).length },
    { name: '5-6', value: grades.filter(g => g.grade !== null && g.grade >= 5 && g.grade < 7).length },
    { name: '7-8', value: grades.filter(g => g.grade !== null && g.grade >= 7 && g.grade < 9).length },
    { name: '9-10', value: grades.filter(g => g.grade !== null && g.grade >= 9).length },
  ];

  const absencesByMonth = [
    { mes: 'Mar', faltas: 45 }, { mes: 'Abr', faltas: 52 }, { mes: 'Mai', faltas: 38 },
    { mes: 'Jun', faltas: 61 }, { mes: 'Ago', faltas: 47 },
  ];

  return (
    <div>
      <PageHeader title="Dashboard" description="Visão geral do sistema escolar" />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
        <StatCard title="Total de Alunos" value={students.length} icon={Users} color="blue" />
        <StatCard title="Professores" value={teachers.length} icon={UserCheck} color="green" />
        <StatCard title="Turmas" value={classes.length} icon={GraduationCap} color="purple" />
        <StatCard title="Frequência Média" value={`${avgAttendance}%`} icon={ClipboardCheck} color="orange" />
        <StatCard title="Baixa Frequência" value={lowAttendance} icon={AlertTriangle} color="red" trend={`${lowAttendance} alunos`} />
        <StatCard title="Ocorrências" value={occurrences.length} icon={AlertTriangle} color="gray" />
      </div>
      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <ChartCard title="Alunos por Ano"><BarChartWidget data={studentsByYear} dataKey="alunos" xKey="ano" /></ChartCard>
        <ChartCard title="Frequência Mensal"><LineChartWidget data={monthlyAttendance} dataKey="freq" xKey="mes" /></ChartCard>
        <ChartCard title="Média por Turma"><BarChartWidget data={classAverages} dataKey="media" xKey="turma" /></ChartCard>
        <ChartCard title="Distribuição de Notas"><PieChartWidget data={gradeDistribution} /></ChartCard>
      </div>
      <div className="mt-6">
        <ChartCard title="Faltas por Mês"><BarChartWidget data={absencesByMonth} dataKey="faltas" xKey="mes" /></ChartCard>
      </div>
      <div className="mt-6">
        <h3 className="mb-4 text-lg font-semibold">Avisos Recentes</h3>
        <div className="grid gap-4 md:grid-cols-2">
          {announcements.slice(0, 4).map(a => <NoticeCard key={a.id} announcement={a} />)}
        </div>
      </div>
    </div>
  );
}

function ProfessorDashboard() {
  const { user } = useAuth();
  const { classes, students, grades, announcements, schedules, subjects, teachers } = useData();
  const myClasses = classes.filter(c => c.teacherId === user?.teacherId);
  const myStudents = students.filter(s => myClasses.some(c => c.id === s.classId));
  const pendingGrades = grades.filter(g => myClasses.some(c => c.id === g.classId) && g.grade === null).length;
  const mySchedules = schedules.filter(s => myClasses.some(c => c.id === s.classId));

  return (
    <div>
      <PageHeader title="Dashboard" description={`Bem-vindo, ${user?.name}`} />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard title="Minhas Turmas" value={myClasses.length} icon={GraduationCap} color="blue" />
        <StatCard title="Total de Alunos" value={myStudents.length} icon={Users} color="green" />
        <StatCard title="Notas Pendentes" value={pendingGrades} icon={BookOpen} color="orange" />
        <StatCard title="Próximas Aulas" value={mySchedules.length} icon={Clock} color="purple" />
      </div>
      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <div className="rounded-xl border border-gray-200 bg-white p-5 dark:border-gray-700 dark:bg-gray-800">
          <h3 className="mb-4 font-semibold">Minhas Turmas</h3>
          <div className="space-y-3">
            {myClasses.map(c => (
              <Link key={c.id} to={`/turmas/${c.id}`} className="flex items-center justify-between rounded-lg border border-gray-100 p-3 hover:bg-gray-50 dark:border-gray-700 dark:hover:bg-gray-700/50">
                <div>
                  <p className="font-medium">{c.name}</p>
                  <p className="text-sm text-gray-500">{c.studentCount} alunos · {c.shift}</p>
                </div>
                <Badge color="blue">{c.room}</Badge>
              </Link>
            ))}
          </div>
        </div>
        <div className="rounded-xl border border-gray-200 bg-white p-5 dark:border-gray-700 dark:bg-gray-800">
          <h3 className="mb-4 font-semibold">Próximas Aulas</h3>
          <div className="space-y-3">
            {mySchedules.slice(0, 5).map(s => {
              const subject = subjects.find(sub => sub.id === s.subjectId);
              return (
                <div key={s.id} className="flex items-center justify-between rounded-lg border border-gray-100 p-3 dark:border-gray-700">
                  <div>
                    <p className="font-medium">{subject?.name}</p>
                    <p className="text-sm text-gray-500">{s.startTime} - {s.endTime}</p>
                  </div>
                  <Badge color="gray">{s.room}</Badge>
                </div>
              );
            })}
          </div>
        </div>
      </div>
      <div className="mt-6">
        <h3 className="mb-4 text-lg font-semibold">Avisos</h3>
        <div className="grid gap-4 md:grid-cols-2">
          {announcements.slice(0, 2).map(a => <NoticeCard key={a.id} announcement={a} />)}
        </div>
      </div>
    </div>
  );
}

function StudentDashboard() {
  const { user } = useAuth();
  const { students, grades, subjects, announcements, events, schedules, classes } = useData();
  const student = students.find(s => s.matricula === user?.matricula) || students[0];
  const studentGrades = grades.filter(g => g.studentId === student.id && g.grade !== null);
  const averages = subjects.map(sub => {
    const subGrades = studentGrades.filter(g => g.subjectId === sub.id).map(g => g.grade);
    return calculateAverage(subGrades);
  }).filter(a => a !== null) as number[];
  const generalAvg = averages.length ? Math.round(averages.reduce((a, b) => a + b, 0) / averages.length * 10) / 10 : 0;
  const status = getGradeStatus(generalAvg);
  const attStatus = getAttendanceStatus(student.attendance);
  const myClass = classes.find(c => c.id === student.classId);
  const mySchedule = schedules.filter(s => s.classId === student.classId).slice(0, 3);

  return (
    <div>
      <PageHeader title="Dashboard" description={`Olá, ${student.name}!`} />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard title="Média Geral" value={generalAvg.toFixed(1)} icon={BookOpen} color="blue" trend={status.label} trendUp={generalAvg >= 7} />
        <StatCard title="Frequência" value={`${student.attendance}%`} icon={ClipboardCheck} color={attStatus.color === 'green' ? 'green' : 'red'} trend={attStatus.label} trendUp={student.attendance >= 75} />
        <StatCard title="Turma" value={myClass?.name || '-'} icon={GraduationCap} color="purple" />
        <StatCard title="Próximas Avaliações" value={2} icon={FileText} color="orange" />
      </div>
      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <div className="rounded-xl border border-gray-200 bg-white p-5 dark:border-gray-700 dark:bg-gray-800">
          <h3 className="mb-4 font-semibold">Notas Recentes</h3>
          <div className="space-y-2">
            {studentGrades.slice(0, 6).map(g => {
              const sub = subjects.find(s => s.id === g.subjectId);
              return (
                <div key={g.id} className="flex items-center justify-between rounded-lg bg-gray-50 p-3 dark:bg-gray-700/50">
                  <span className="text-sm">{sub?.name} - {g.bimester}º Bim</span>
                  <Badge color={g.grade! >= 7 ? 'green' : g.grade! >= 5 ? 'yellow' : 'red'}>{g.grade}</Badge>
                </div>
              );
            })}
          </div>
          <Link to="/boletim" className="mt-3 inline-block text-sm text-primary-600 hover:underline">Ver boletim completo →</Link>
        </div>
        <div className="rounded-xl border border-gray-200 bg-white p-5 dark:border-gray-700 dark:bg-gray-800">
          <h3 className="mb-4 font-semibold">Próximas Aulas</h3>
          <div className="space-y-2">
            {mySchedule.map(s => {
              const sub = subjects.find(sub => sub.id === s.subjectId);
              return (
                <div key={s.id} className="flex items-center justify-between rounded-lg bg-gray-50 p-3 dark:bg-gray-700/50">
                  <span className="text-sm">{sub?.name}</span>
                  <span className="text-xs text-gray-500">{s.startTime} - {s.endTime}</span>
                </div>
              );
            })}
          </div>
          <Link to="/horarios" className="mt-3 inline-block text-sm text-primary-600 hover:underline">Ver horário completo →</Link>
        </div>
      </div>
      <div className="mt-6 grid gap-4 sm:grid-cols-2">
        <Link to="/boletim" className="flex items-center gap-3 rounded-xl border border-gray-200 bg-white p-4 hover:shadow-md dark:border-gray-700 dark:bg-gray-800">
          <FileText className="h-8 w-8 text-primary-600" />
          <div><p className="font-medium">Boletim Escolar</p><p className="text-sm text-gray-500">Consultar notas e médias</p></div>
        </Link>
        <Link to="/horarios" className="flex items-center gap-3 rounded-xl border border-gray-200 bg-white p-4 hover:shadow-md dark:border-gray-700 dark:bg-gray-800">
          <Clock className="h-8 w-8 text-primary-600" />
          <div><p className="font-medium">Horário de Aulas</p><p className="text-sm text-gray-500">Grade semanal</p></div>
        </Link>
      </div>
      <div className="mt-6">
        <h3 className="mb-4 font-semibold">Avisos e Eventos</h3>
        <div className="grid gap-4 md:grid-cols-2">
          {announcements.slice(0, 2).map(a => <NoticeCard key={a.id} announcement={a} />)}
        </div>
      </div>
    </div>
  );
}
