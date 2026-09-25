/** Formatadores e mapas de status compartilhados pelas telas. */

/** YYYY-MM-DD -> DD/MM/YYYY */
export function formatDate(value) {
  if (!value) return '-';
  const raw = String(value);
  const match = raw.match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (match) return `${match[3]}/${match[2]}/${match[1]}`;
  // Data ja vem em ISO completo (com hora): 2026-01-15T00:00:00
  const dt = new Date(raw);
  if (!Number.isNaN(dt.getTime())) {
    return dt.toLocaleDateString('pt-BR');
  }
  return raw;
}

/** Numero de 0 a 10 com 2 casas e virgula decimal. */
export function formatGrade(value) {
  if (value === null || value === undefined || value === '' || Number.isNaN(Number(value))) return '-';
  return Number(value).toFixed(2).replace('.', ',');
}

export function todayISO() {
  const now = new Date();
  const offset = now.getTimezoneOffset();
  return new Date(now.getTime() - offset * 60000).toISOString().slice(0, 10);
}

export function greetingFor(date = new Date()) {
  const hour = date.getHours();
  if (hour < 12) return 'Bom dia';
  if (hour < 18) return 'Boa tarde';
  return 'Boa noite';
}

export function longDateBR(date = new Date()) {
  return date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'long', year: 'numeric' });
}

export const WEEKDAYS = ['Domingo', 'Segunda-feira', 'Terca-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sabado'];
export const WEEKDAYS_SHORT = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SAB'];
export const MONTHS = [
  'Janeiro', 'Fevereiro', 'Marco', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

export function bimestreLabel(value) {
  return `${value}º Bimestre`;
}

/** Rotulo e tom visual de um status generico. */
const STATUS_MAP = {
  // Presenca
  PRESENTE: { label: 'Presente', tone: 'success' },
  FALTA: { label: 'Falta', tone: 'danger' },
  FALTA_JUSTIFICADA: { label: 'Falta Justificada', tone: 'warning' },
  // Ocorrencia
  ABERTA: { label: 'Aberta', tone: 'warning' },
  ENCERRADA: { label: 'Encerrada', tone: 'neutral' },
  // Achados e perdidos
  NAO_REIVINDICADO: { label: 'Nao Reivindicado', tone: 'info' },
  DEVOLVIDO: { label: 'Devolvido', tone: 'primary' },
  // Situacao escolar
  APROVADO: { label: 'Aprovado', tone: 'success' },
  RECUPERACAO: { label: 'Recuperacao', tone: 'warning' },
  REPROVADO: { label: 'Reprovado', tone: 'danger' },
  // Calendario
  LETIVO: { label: 'Dia Letivo', tone: 'info' },
  FERIADO: { label: 'Feriado', tone: 'danger' },
  RECESSO: { label: 'Recesso', tone: 'warning' },
  EVENTO: { label: 'Evento', tone: 'primary' },
  //series
  ENSINO_FUNDAMENTAL: { label: 'Ensino Fundamental', tone: 'info' },
  ENSINO_MEDIO: { label: 'Ensino Medio', tone: 'info' }
};

export function statusInfo(status) {
  if (!status) return { label: '-', tone: 'neutral' };
  const key = String(status).toUpperCase().replace(/\s+/g, '_');
  return STATUS_MAP[key] || { label: String(status), tone: 'neutral' };
}

export const CATEGORIAS_ACHADOS = ['UNIFORME', 'MATERIAL', 'ELETRONICO', 'OUTRO'];
export const TIPOS_CALENDARIO = ['LETIVO', 'FERIADO', 'RECESSO', 'EVENTO'];
export const TIPOS_NOTA = ['PROVA', 'TRABALHO', 'ATIVIDADE'];
export const STATUS_PRESENCA = ['PRESENTE', 'FALTA', 'FALTA_JUSTIFICADA'];

/** Pesos padrao usados no calculo da media de notas. */
export const PESOS_PADRAO = { PROVA: 5, TRABALHO: 3, ATIVIDADE: 2 };

export const BIMESTRES = [1, 2, 3, 4];

/**
 * RN-02 Aprovacao: media >= 6.00 E frequencia >= 75%.
 * Entre 4.00 e 6.00 => recuperacao. Abaixo disso => reprovado.
 */
export function situacaoAluno(media, frequencia) {
  const m = Number(media) || 0;
  const f = frequencia === undefined || frequencia === null ? 100 : Number(frequencia);
  if (m >= 6 && f >= 75) return 'APROVADO';
  if (m >= 4 && f >= 75) return 'RECUPERACAO';
  return 'REPROVADO';
}

/** Media ponderada usando PESOS_PADRAO. Retorna null se nao houver notas. */
export function calcularMediaPesos(valores) {
  let soma = 0;
  let somaPesos = 0;
  TIPOS_NOTA.forEach((tipo) => {
    const item = valores?.[tipo];
    const valor = item?.valor;
    if (valor === '' || valor === null || valor === undefined || Number.isNaN(Number(valor))) return;
    const peso = item?.peso ?? PESOS_PADRAO[tipo];
    soma += Number(valor) * peso;
    somaPesos += peso;
  });
  if (somaPesos === 0) return null;
  return soma / somaPesos;
}

/** Normaliza o texto para busca "contem" insensivel a acento e caixa. */
export function normalizeText(value) {
  return String(value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

export function firstName(nome) {
  return String(nome || '').trim().split(/\s+/)[0] || '';
}

export function initials(nome) {
  const parts = String(nome || '').trim().split(/\s+/).filter(Boolean);
  if (!parts.length) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}
