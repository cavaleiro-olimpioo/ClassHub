import type { Permission, UserRole } from '../types';

export const ROLE_PERMISSIONS: Record<UserRole, Permission[]> = {
  admin: [
    'view_students', 'manage_students', 'view_teachers', 'manage_teachers',
    'view_classes', 'manage_classes', 'manage_grades', 'manage_attendance',
    'view_report_card', 'create_occurrences', 'manage_occurrences',
    'manage_announcements', 'manage_lost_found', 'manage_registrations',
    'view_settings', 'manage_settings',
  ],
  professor: [
    'view_students', 'view_classes', 'manage_grades', 'manage_attendance',
    'create_occurrences', 'view_report_card',
  ],
  aluno: ['view_report_card'],
};

export const DEMO_USERS = [
  {
    id: 'u1',
    name: 'Ana Paula Silva',
    email: 'ana.silva@classhub.edu.br',
    matricula: 'ADM001',
    password: '123456',
    role: 'admin' as UserRole,
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ana',
  },
  {
    id: 'u2',
    name: 'Carlos Henrique',
    email: 'carlos.henrique@classhub.edu.br',
    matricula: 'PROF001',
    password: '123456',
    role: 'professor' as UserRole,
    teacherId: 't1',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Carlos',
  },
  {
    id: 'u3',
    name: 'Lucas Mendes',
    email: 'lucas.mendes@classhub.edu.br',
    matricula: '2024001',
    password: '123456',
    role: 'aluno' as UserRole,
    classId: 'c5',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Lucas',
  },
];

export const SUBJECTS = [
  { id: 's1', name: 'Português', code: 'POR', workload: 5 },
  { id: 's2', name: 'Matemática', code: 'MAT', workload: 5 },
  { id: 's3', name: 'Ciências', code: 'CIE', workload: 3 },
  { id: 's4', name: 'História', code: 'HIS', workload: 2 },
  { id: 's5', name: 'Geografia', code: 'GEO', workload: 2 },
  { id: 's6', name: 'Inglês', code: 'ING', workload: 2 },
  { id: 's7', name: 'Educação Física', code: 'EDF', workload: 2 },
  { id: 's8', name: 'Artes', code: 'ART', workload: 2 },
];

export const CLASSES = [
  { id: 'c1', name: '1º Ano A', year: 1, shift: 'Manhã' as const, room: 'Sala 101', teacherId: 't5', studentCount: 28 },
  { id: 'c2', name: '2º Ano A', year: 2, shift: 'Manhã' as const, room: 'Sala 102', teacherId: 't6', studentCount: 26 },
  { id: 'c3', name: '3º Ano B', year: 3, shift: 'Tarde' as const, room: 'Sala 201', teacherId: 't7', studentCount: 25 },
  { id: 'c4', name: '4º Ano A', year: 4, shift: 'Manhã' as const, room: 'Sala 202', teacherId: 't8', studentCount: 27 },
  { id: 'c5', name: '5º Ano A', year: 5, shift: 'Manhã' as const, room: 'Sala 301', teacherId: 't1', studentCount: 30 },
  { id: 'c6', name: '6º Ano B', year: 6, shift: 'Tarde' as const, room: 'Sala 302', teacherId: 't2', studentCount: 29 },
  { id: 'c7', name: '7º Ano A', year: 7, shift: 'Manhã' as const, room: 'Sala 401', teacherId: 't3', studentCount: 28 },
  { id: 'c8', name: '8º Ano B', year: 8, shift: 'Tarde' as const, room: 'Sala 402', teacherId: 't4', studentCount: 27 },
  { id: 'c9', name: '9º Ano A', year: 9, shift: 'Manhã' as const, room: 'Sala 501', teacherId: 't1', studentCount: 26 },
];

export const TEACHERS = [
  { id: 't1', name: 'Carlos Henrique', email: 'carlos.henrique@classhub.edu.br', phone: '(11) 98765-4321', cpf: '123.456.789-00', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Carlos', subjects: ['s2', 's3'], classIds: ['c5', 'c9'], status: 'Ativo' as const, hireDate: '2018-02-01' },
  { id: 't2', name: 'Mariana Costa', email: 'mariana.costa@classhub.edu.br', phone: '(11) 98765-4322', cpf: '234.567.890-11', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Mariana', subjects: ['s1'], classIds: ['c6'], status: 'Ativo' as const, hireDate: '2019-03-15' },
  { id: 't3', name: 'Roberto Alves', email: 'roberto.alves@classhub.edu.br', phone: '(11) 98765-4323', cpf: '345.678.901-22', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Roberto', subjects: ['s4', 's5'], classIds: ['c7'], status: 'Ativo' as const, hireDate: '2017-08-20' },
  { id: 't4', name: 'Fernanda Lima', email: 'fernanda.lima@classhub.edu.br', phone: '(11) 98765-4324', cpf: '456.789.012-33', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Fernanda', subjects: ['s6', 's8'], classIds: ['c8'], status: 'Ativo' as const, hireDate: '2020-01-10' },
  { id: 't5', name: 'Paulo Santos', email: 'paulo.santos@classhub.edu.br', phone: '(11) 98765-4325', cpf: '567.890.123-44', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Paulo', subjects: ['s1', 's2'], classIds: ['c1'], status: 'Ativo' as const, hireDate: '2016-05-05' },
  { id: 't6', name: 'Juliana Rocha', email: 'juliana.rocha@classhub.edu.br', phone: '(11) 98765-4326', cpf: '678.901.234-55', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Juliana', subjects: ['s3', 's7'], classIds: ['c2'], status: 'Ativo' as const, hireDate: '2021-02-28' },
  { id: 't7', name: 'Ricardo Moura', email: 'ricardo.moura@classhub.edu.br', phone: '(11) 98765-4327', cpf: '789.012.345-66', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ricardo', subjects: ['s2', 's4'], classIds: ['c3'], status: 'Ativo' as const, hireDate: '2019-07-12' },
  { id: 't8', name: 'Camila Dias', email: 'camila.dias@classhub.edu.br', phone: '(11) 98765-4328', cpf: '890.123.456-77', photo: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Camila', subjects: ['s5', 's6'], classIds: ['c4'], status: 'Licença' as const, hireDate: '2018-11-30' },
];

const studentNames = [
  { name: 'Lucas Mendes', classId: 'c5', year: 5 },
  { name: 'Beatriz Oliveira', classId: 'c5', year: 5 },
  { name: 'Gabriel Souza', classId: 'c5', year: 5 },
  { name: 'Isabela Ferreira', classId: 'c5', year: 5 },
  { name: 'Matheus Barbosa', classId: 'c5', year: 5 },
  { name: 'Laura Martins', classId: 'c6', year: 6 },
  { name: 'Pedro Henrique', classId: 'c6', year: 6 },
  { name: 'Sophia Ribeiro', classId: 'c6', year: 6 },
  { name: 'Enzo Carvalho', classId: 'c6', year: 6 },
  { name: 'Valentina Gomes', classId: 'c7', year: 7 },
  { name: 'Miguel Araújo', classId: 'c7', year: 7 },
  { name: 'Helena Castro', classId: 'c7', year: 7 },
  { name: 'Arthur Nunes', classId: 'c8', year: 8 },
  { name: 'Alice Pinto', classId: 'c8', year: 8 },
  { name: 'Bernardo Lopes', classId: 'c8', year: 8 },
  { name: 'Manuela Teixeira', classId: 'c9', year: 9 },
  { name: 'Davi Correia', classId: 'c9', year: 9 },
  { name: 'Julia Cardoso', classId: 'c9', year: 9 },
  { name: 'Theo Moreira', classId: 'c1', year: 1 },
  { name: 'Lívia Freitas', classId: 'c2', year: 2 },
  { name: 'Rafael Monteiro', classId: 'c3', year: 3 },
  { name: 'Clara Duarte', classId: 'c4', year: 4 },
  { name: 'Nicolas Ramos', classId: 'c5', year: 5 },
  { name: 'Marina Vieira', classId: 'c6', year: 6 },
];

export const STUDENTS = studentNames.map((s, i) => ({
  id: `st${i + 1}`,
  name: s.name,
  matricula: `2024${String(i + 1).padStart(3, '0')}`,
  photo: `https://api.dicebear.com/7.x/avataaars/svg?seed=${s.name.replace(' ', '')}`,
  birthDate: `${2015 - s.year}-${String((i % 12) + 1).padStart(2, '0')}-${String((i % 28) + 1).padStart(2, '0')}`,
  cpf: `${String(100 + i).padStart(3, '0')}.${String(200 + i).padStart(3, '0')}.${String(300 + i).padStart(3, '0')}-${String(i % 99).padStart(2, '0')}`,
  year: s.year,
  classId: s.classId,
  shift: CLASSES.find(c => c.id === s.classId)!.shift,
  guardian: `Responsável de ${s.name.split(' ')[0]}`,
  phone: `(11) 9${String(8000 + i).slice(0, 4)}-${String(1000 + i).slice(0, 4)}`,
  email: `${s.name.toLowerCase().replace(' ', '.')}@email.com`,
  address: `Rua ${['das Flores', 'Brasil', 'São Paulo', 'Central', 'Nova'][i % 5]}, ${100 + i}`,
  notes: i % 5 === 0 ? 'Aluno com desempenho destacado' : undefined,
  attendance: 75 + (i % 25),
  status: 'Ativo' as const,
}));

function generateGrades() {
  const grades = [];
  let id = 1;
  for (const student of STUDENTS) {
    for (const subject of SUBJECTS.slice(0, 6)) {
      for (let b = 1; b <= 4; b++) {
        grades.push({
          id: `g${id++}`,
          studentId: student.id,
          classId: student.classId,
          subjectId: subject.id,
          bimester: b as 1 | 2 | 3 | 4,
          grade: b <= 2 ? Math.round((5 + Math.random() * 5) * 10) / 10 : null,
          year: 2024,
        });
      }
    }
  }
  return grades;
}

export const GRADES = generateGrades();

export const ATTENDANCE = STUDENTS.flatMap((student, si) =>
  SUBJECTS.slice(0, 4).flatMap((subject, subi) =>
    Array.from({ length: 5 }, (_, di) => ({
      id: `a${si}-${subi}-${di}`,
      studentId: student.id,
      classId: student.classId,
      subjectId: subject.id,
      date: `2024-08-${String(di + 1).padStart(2, '0')}`,
      status: (Math.random() > 0.15 ? 'present' : Math.random() > 0.5 ? 'absent' : 'justified') as 'present' | 'absent' | 'justified',
    }))
  )
);

export const OCCURRENCES = [
  { id: 'o1', studentId: 'st1', date: '2024-08-20', type: 'Elogio' as const, description: 'Excelente participação em trabalho de grupo', severity: 'Baixa' as const, teacherId: 't1' },
  { id: 'o2', studentId: 'st2', date: '2024-08-18', type: 'Atraso' as const, description: 'Chegou 15 minutos após o início das aulas', severity: 'Baixa' as const, teacherId: 't1' },
  { id: 'o3', studentId: 'st6', date: '2024-08-15', type: 'Indisciplina' as const, description: 'Conversou durante explicação do professor', severity: 'Média' as const, teacherId: 't2' },
  { id: 'o4', studentId: 'st10', date: '2024-08-12', type: 'Problema acadêmico' as const, description: 'Não entregou trabalho de Geografia', severity: 'Média' as const, teacherId: 't3' },
  { id: 'o5', studentId: 'st13', date: '2024-08-10', type: 'Comportamento' as const, description: 'Desrespeitou colega no recreio', severity: 'Alta' as const, teacherId: 't4' },
  { id: 'o6', studentId: 'st16', date: '2024-08-08', type: 'Elogio' as const, description: 'Destaque na olimpíada de matemática', severity: 'Baixa' as const, teacherId: 't1' },
  { id: 'o7', studentId: 'st3', date: '2024-08-05', type: 'Atraso' as const, description: 'Atraso recorrente nas segundas-feiras', severity: 'Média' as const, teacherId: 't1' },
  { id: 'o8', studentId: 'st8', date: '2024-08-03', type: 'Outros' as const, description: 'Esqueceu material escolar', severity: 'Baixa' as const, teacherId: 't2' },
];

export const ANNOUNCEMENTS = [
  { id: 'an1', title: 'Reunião de Pais - 5º Ano', description: 'Convidamos todos os responsáveis dos alunos do 5º ano para reunião no dia 30/08 às 19h no auditório.', date: '2024-08-25', author: 'Ana Paula Silva', category: 'Comunicado' as const, priority: 'Alta' as const, status: 'Ativo' as const },
  { id: 'an2', title: 'Feira de Ciências 2024', description: 'A Feira de Ciências será realizada nos dias 15 e 16 de setembro. Inscrições abertas até 10/09.', date: '2024-08-22', author: 'Carlos Henrique', category: 'Evento' as const, priority: 'Normal' as const, status: 'Ativo' as const },
  { id: 'an3', title: 'Prova Bimestral - Matemática', description: 'A prova bimestral de Matemática do 5º ano será no dia 28/08. Estudem os capítulos 3 e 4.', date: '2024-08-20', author: 'Carlos Henrique', category: 'Acadêmico' as const, priority: 'Alta' as const, status: 'Ativo' as const },
  { id: 'an4', title: 'Manutenção do Sistema Elétrico', description: 'No sábado haverá manutenção preventiva. Não haverá aulas extras.', date: '2024-08-18', author: 'Ana Paula Silva', category: 'Geral' as const, priority: 'Baixa' as const, status: 'Ativo' as const },
  { id: 'an5', title: 'URGENTE: Suspensão de Aulas', description: 'Devido às chuvas intensas, as aulas de amanhã estão suspensas. Retorno previsto para quinta-feira.', date: '2024-08-15', author: 'Ana Paula Silva', category: 'Urgente' as const, priority: 'Alta' as const, status: 'Arquivado' as const },
  { id: 'an6', title: 'Campeonato Interclasses', description: 'Inscrições abertas para o campeonato de futsal. Equipes de 7º a 9º ano.', date: '2024-08-12', author: 'Juliana Rocha', category: 'Evento' as const, priority: 'Normal' as const, status: 'Ativo' as const },
];

export const CALENDAR_EVENTS = [
  { id: 'e1', title: 'Prova de Matemática', description: 'Prova bimestral', date: '2024-08-28', category: 'Prova' as const, classId: 'c5' },
  { id: 'e2', title: 'Entrega Trabalho História', description: 'Trabalho sobre Revolução Industrial', date: '2024-09-05', category: 'Trabalho' as const, classId: 'c7' },
  { id: 'e3', title: 'Feriado - Independência', description: 'Não haverá aulas', date: '2024-09-07', category: 'Feriado' as const },
  { id: 'e4', title: 'Reunião Pedagógica', description: 'Reunião com corpo docente', date: '2024-09-10', category: 'Reunião' as const },
  { id: 'e5', title: 'Feira de Ciências', description: 'Apresentação dos projetos', date: '2024-09-15', endDate: '2024-09-16', category: 'Evento' as const },
  { id: 'e6', title: 'Início 3º Bimestre', description: 'Início do terceiro bimestre letivo', date: '2024-09-02', category: 'Bimestre' as const },
  { id: 'e7', title: 'Férias de Julho', description: 'Recesso escolar', date: '2024-07-01', endDate: '2024-07-31', category: 'Férias' as const },
  { id: 'e8', title: 'Conselho de Classe', description: 'Conselho do 9º ano', date: '2024-09-20', category: 'Conselho' as const, classId: 'c9' },
];

export const LOST_FOUND = [
  { id: 'lf1', object: 'Estojo azul com lápis e canetas', photo: 'https://api.dicebear.com/7.x/shapes/svg?seed=estojo', category: 'Material escolar' as const, location: 'Pátio central', date: '2024-08-24', status: 'Encontrado' as const, description: 'Estojo azul marca Faber-Castell' },
  { id: 'lf2', object: 'Agasalho verde tamanho M', category: 'Roupas' as const, location: 'Quadra esportiva', date: '2024-08-22', status: 'Encontrado' as const, description: 'Moletom verde com capuz' },
  { id: 'lf3', object: 'Fone de ouvido preto', category: 'Eletrônicos' as const, location: 'Sala 301', date: '2024-08-20', status: 'Reivindicado' as const, description: 'Fone JBL preto' },
  { id: 'lf4', object: 'Carteira de identidade', category: 'Documentos' as const, location: 'Biblioteca', date: '2024-08-18', status: 'Devolvido' as const, description: 'RG em nome de Pedro H.' },
  { id: 'lf5', object: 'Relógio digital', category: 'Acessórios' as const, location: 'Corredor bloco B', date: '2024-08-15', status: 'Encontrado' as const, description: 'Relógio Casio preto' },
  { id: 'lf6', object: 'Caderno de Ciências', category: 'Material escolar' as const, location: 'Sala 401', date: '2024-08-12', status: 'Encontrado' as const, description: 'Caderno espiral 10 matérias' },
];

export const ROOMS = [
  { id: 'r1', name: 'Sala 101', capacity: 30, floor: 1 },
  { id: 'r2', name: 'Sala 102', capacity: 30, floor: 1 },
  { id: 'r3', name: 'Sala 201', capacity: 28, floor: 2 },
  { id: 'r4', name: 'Sala 301', capacity: 32, floor: 3 },
  { id: 'r5', name: 'Sala 401', capacity: 30, floor: 4 },
  { id: 'r6', name: 'Auditório', capacity: 150, floor: 1 },
  { id: 'r7', name: 'Laboratório', capacity: 25, floor: 2 },
];

export const SCHEDULES = [
  { id: 'sch1', classId: 'c5', day: 1 as const, startTime: '07:30', endTime: '08:20', subjectId: 's1', teacherId: 't2', room: 'Sala 301' },
  { id: 'sch2', classId: 'c5', day: 1 as const, startTime: '08:20', endTime: '09:10', subjectId: 's2', teacherId: 't1', room: 'Sala 301' },
  { id: 'sch3', classId: 'c5', day: 1 as const, startTime: '09:30', endTime: '10:20', subjectId: 's3', teacherId: 't1', room: 'Sala 301' },
  { id: 'sch4', classId: 'c5', day: 1 as const, startTime: '10:20', endTime: '11:10', subjectId: 's4', teacherId: 't3', room: 'Sala 301' },
  { id: 'sch5', classId: 'c5', day: 1 as const, startTime: '11:10', endTime: '12:00', subjectId: 's7', teacherId: 't6', room: 'Quadra' },
  { id: 'sch6', classId: 'c5', day: 2 as const, startTime: '07:30', endTime: '08:20', subjectId: 's2', teacherId: 't1', room: 'Sala 301' },
  { id: 'sch7', classId: 'c5', day: 2 as const, startTime: '08:20', endTime: '09:10', subjectId: 's5', teacherId: 't3', room: 'Sala 301' },
  { id: 'sch8', classId: 'c5', day: 2 as const, startTime: '09:30', endTime: '10:20', subjectId: 's6', teacherId: 't4', room: 'Sala 301' },
  { id: 'sch9', classId: 'c5', day: 2 as const, startTime: '10:20', endTime: '11:10', subjectId: 's1', teacherId: 't2', room: 'Sala 301' },
  { id: 'sch10', classId: 'c5', day: 2 as const, startTime: '11:10', endTime: '12:00', subjectId: 's8', teacherId: 't4', room: 'Sala 301' },
  { id: 'sch11', classId: 'c5', day: 3 as const, startTime: '07:30', endTime: '08:20', subjectId: 's3', teacherId: 't1', room: 'Lab' },
  { id: 'sch12', classId: 'c5', day: 3 as const, startTime: '08:20', endTime: '09:10', subjectId: 's1', teacherId: 't2', room: 'Sala 301' },
  { id: 'sch13', classId: 'c5', day: 4 as const, startTime: '07:30', endTime: '08:20', subjectId: 's2', teacherId: 't1', room: 'Sala 301' },
  { id: 'sch14', classId: 'c5', day: 4 as const, startTime: '08:20', endTime: '09:10', subjectId: 's4', teacherId: 't3', room: 'Sala 301' },
  { id: 'sch15', classId: 'c5', day: 5 as const, startTime: '07:30', endTime: '08:20', subjectId: 's6', teacherId: 't4', room: 'Sala 301' },
  { id: 'sch16', classId: 'c5', day: 5 as const, startTime: '08:20', endTime: '09:10', subjectId: 's2', teacherId: 't1', room: 'Sala 301' },
];

export const NOTIFICATIONS = [
  { id: 'n1', title: 'Nova ocorrência', message: 'Ocorrência registrada para Beatriz Oliveira', type: 'occurrence' as const, date: '2024-08-25T10:30:00', read: false },
  { id: 'n2', title: 'Nota lançada', message: 'Nota de Matemática disponível para Lucas Mendes', type: 'grade' as const, date: '2024-08-24T14:00:00', read: false },
  { id: 'n3', title: 'Novo aviso', message: 'Reunião de Pais - 5º Ano publicada', type: 'announcement' as const, date: '2024-08-25T08:00:00', read: true },
  { id: 'n4', title: 'Avaliação próxima', message: 'Prova de Matemática em 28/08', type: 'exam' as const, date: '2024-08-23T09:00:00', read: false },
  { id: 'n5', title: 'Baixa frequência', message: 'Pedro Henrique com frequência abaixo de 75%', type: 'attendance' as const, date: '2024-08-22T11:00:00', read: true },
  { id: 'n6', title: 'Novo evento', message: 'Feira de Ciências agendada para 15/09', type: 'event' as const, date: '2024-08-22T08:00:00', read: false },
];

export const GUARDIANS = STUDENTS.slice(0, 10).map((s, i) => ({
  id: `g${i + 1}`,
  name: s.guardian,
  cpf: `${String(900 + i).padStart(3, '0')}.${String(100 + i).padStart(3, '0')}.${String(200 + i).padStart(3, '0')}-${String(i % 99).padStart(2, '0')}`,
  phone: s.phone,
  email: `resp${i + 1}@email.com`,
  studentIds: [s.id],
}));
