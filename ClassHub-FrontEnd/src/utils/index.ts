export function cn(...classes: (string | boolean | undefined | null)[]) {
  return classes.filter(Boolean).join(' ');
}

export function formatDate(date: string) {
  return new Date(date + 'T12:00:00').toLocaleDateString('pt-BR');
}

export function formatDateTime(date: string) {
  return new Date(date).toLocaleString('pt-BR');
}

export function getInitials(name: string) {
  return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
}

export function calculateAverage(grades: (number | null)[]) {
  const valid = grades.filter((g): g is number => g !== null);
  if (valid.length === 0) return null;
  return Math.round((valid.reduce((a, b) => a + b, 0) / valid.length) * 10) / 10;
}

export function getGradeStatus(average: number | null) {
  if (average === null) return { label: 'Em andamento', color: 'gray' };
  if (average >= 7) return { label: 'Aprovado', color: 'green' };
  if (average >= 5) return { label: 'Recuperação', color: 'yellow' };
  return { label: 'Reprovado', color: 'red' };
}

export function getAttendanceStatus(rate: number) {
  if (rate >= 75) return { label: 'Regular', color: 'green' };
  if (rate >= 60) return { label: 'Atenção', color: 'yellow' };
  return { label: 'Crítico', color: 'red' };
}

export const DAYS = ['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta'];
export const MONTHS = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];

export function paginate<T>(items: T[], page: number, perPage: number) {
  const start = (page - 1) * perPage;
  return {
    data: items.slice(start, start + perPage),
    total: items.length,
    totalPages: Math.ceil(items.length / perPage),
    page,
    perPage,
  };
}

export function generateId(prefix = 'id') {
  return `${prefix}_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`;
}
