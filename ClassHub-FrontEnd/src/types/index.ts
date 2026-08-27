export type UserRole = 'admin' | 'professor' | 'aluno';

export type Permission =
  | 'view_students'
  | 'manage_students'
  | 'view_teachers'
  | 'manage_teachers'
  | 'view_classes'
  | 'manage_classes'
  | 'manage_grades'
  | 'manage_attendance'
  | 'view_report_card'
  | 'create_occurrences'
  | 'manage_occurrences'
  | 'manage_announcements'
  | 'manage_lost_found'
  | 'manage_registrations'
  | 'view_settings'
  | 'manage_settings';

export interface User {
  id: string;
  name: string;
  email: string;
  matricula: string;
  role: UserRole;
  permissions: Permission[];
  avatar?: string;
  classId?: string;
  teacherId?: string;
}

export interface Student {
  id: string;
  name: string;
  matricula: string;
  photo?: string;
  birthDate: string;
  cpf: string;
  year: number;
  classId: string;
  shift: 'Manhã' | 'Tarde' | 'Integral';
  guardian: string;
  phone: string;
  email: string;
  address: string;
  notes?: string;
  attendance: number;
  status: 'Ativo' | 'Inativo' | 'Transferido';
}

export interface Teacher {
  id: string;
  name: string;
  email: string;
  phone: string;
  cpf: string;
  photo?: string;
  subjects: string[];
  classIds: string[];
  status: 'Ativo' | 'Inativo' | 'Licença';
  hireDate: string;
}

export interface SchoolClass {
  id: string;
  name: string;
  year: number;
  shift: 'Manhã' | 'Tarde' | 'Integral';
  room: string;
  teacherId: string;
  studentCount: number;
}

export interface Subject {
  id: string;
  name: string;
  code: string;
  workload: number;
}

export interface Grade {
  id: string;
  studentId: string;
  classId: string;
  subjectId: string;
  bimester: 1 | 2 | 3 | 4;
  grade: number | null;
  year: number;
}

export interface AttendanceRecord {
  id: string;
  studentId: string;
  classId: string;
  subjectId: string;
  date: string;
  status: 'present' | 'absent' | 'justified';
}

export interface Occurrence {
  id: string;
  studentId: string;
  date: string;
  type: 'Comportamento' | 'Atraso' | 'Indisciplina' | 'Elogio' | 'Problema acadêmico' | 'Outros';
  description: string;
  severity: 'Baixa' | 'Média' | 'Alta';
  teacherId: string;
}

export interface Announcement {
  id: string;
  title: string;
  description: string;
  date: string;
  author: string;
  category: 'Geral' | 'Acadêmico' | 'Evento' | 'Urgente' | 'Comunicado';
  priority: 'Baixa' | 'Normal' | 'Alta';
  status: 'Ativo' | 'Arquivado';
  attachment?: string;
}

export interface CalendarEvent {
  id: string;
  title: string;
  description: string;
  date: string;
  endDate?: string;
  category: 'Prova' | 'Trabalho' | 'Feriado' | 'Reunião' | 'Evento' | 'Bimestre' | 'Férias' | 'Conselho';
  classId?: string;
}

export interface LostFoundItem {
  id: string;
  object: string;
  photo?: string;
  category: 'Material escolar' | 'Roupas' | 'Eletrônicos' | 'Documentos' | 'Acessórios' | 'Outros';
  location: string;
  date: string;
  status: 'Encontrado' | 'Reivindicado' | 'Devolvido';
  description: string;
}

export interface ScheduleSlot {
  id: string;
  classId: string;
  day: 1 | 2 | 3 | 4 | 5;
  startTime: string;
  endTime: string;
  subjectId: string;
  teacherId: string;
  room: string;
}

export interface Room {
  id: string;
  name: string;
  capacity: number;
  floor: number;
}

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: 'occurrence' | 'grade' | 'announcement' | 'exam' | 'attendance' | 'event';
  date: string;
  read: boolean;
}

export interface Guardian {
  id: string;
  name: string;
  cpf: string;
  phone: string;
  email: string;
  studentIds: string[];
}
