/** Formatadores e mapas de status compartilhados pelas telas. */

/**
 * Converte uma data no formato "YYYY-MM-DD" (ou ISO completo) para o
 * formato brasileiro "DD/MM/YYYY".
 *
 * @param {string|Date|null|undefined} value data a ser formatada
 * @returns {string} a data formatada, ou "-" se `value` for vazio
 */
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

/**
 * Formata um valor numérico de nota (0 a 10) com duas casas decimais e
 * vírgula como separador decimal, no padrão brasileiro.
 *
 * @param {number|string|null|undefined} value valor da nota a ser formatado
 * @returns {string} a nota formatada (ex.: "8,50"), ou "-" se o valor for inválido
 */
export function formatGrade(value) {
  if (value === null || value === undefined || value === '' || Number.isNaN(Number(value))) return '-';
  return Number(value).toFixed(2).replace('.', ',');
}

/**
 * Retorna a data atual no formato ISO "YYYY-MM-DD", já ajustada para o
 * fuso horário local do navegador.
 *
 * @returns {string} a data de hoje no formato ISO
 */
export function todayISO() {
  const now = new Date();
  const offset = now.getTimezoneOffset();
  return new Date(now.getTime() - offset * 60000).toISOString().slice(0, 10);
}

/**
 * Retorna uma saudação (bom dia/boa tarde/boa noite) de acordo com o
 * horário informado.
 *
 * @param {Date} [date] data/hora de referência (padrão: agora)
 * @returns {string} a saudação apropriada para o horário
 */
export function greetingFor(date = new Date()) {
  const hour = date.getHours();
  if (hour < 12) return 'Bom dia';
  if (hour < 18) return 'Boa tarde';
  return 'Boa noite';
}

/**
 * Formata uma data por extenso no padrão brasileiro (ex.: "29 de setembro de 2026").
 *
 * @param {Date} [date] data a ser formatada (padrão: agora)
 * @returns {string} a data formatada por extenso
 */
export function longDateBR(date = new Date()) {
  return date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'long', year: 'numeric' });
}

/** Nomes completos dos dias da semana, em português, começando por domingo. */
export const WEEKDAYS = ['Domingo', 'Segunda-feira', 'Terca-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sabado'];
/** Abreviações dos dias da semana (3 letras), em português, começando por domingo. */
export const WEEKDAYS_SHORT = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SAB'];
/** Nomes completos dos meses do ano, em português. */
export const MONTHS = [
  'Janeiro', 'Fevereiro', 'Marco', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

/**
 * Monta o rótulo textual de um bimestre (ex.: "1º Bimestre").
 *
 * @param {number} value número do bimestre
 * @returns {string} o rótulo formatado
 */
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

/**
 * Traduz um status bruto vindo da API (ex.: "FALTA_JUSTIFICADA") em um
 * rótulo legível e um "tom" visual (cor) usado nos badges da interface.
 *
 * @param {string|null|undefined} status status bruto recebido da API
 * @returns {{label: string, tone: string}} rótulo e tom correspondentes ao status
 */
export function statusInfo(status) {
  if (!status) return { label: '-', tone: 'neutral' };
  const key = String(status).toUpperCase().replace(/\s+/g, '_');
  return STATUS_MAP[key] || { label: String(status), tone: 'neutral' };
}

/** Categorias válidas para itens de achados e perdidos. */
export const CATEGORIAS_ACHADOS = ['UNIFORME', 'MATERIAL', 'ELETRONICO', 'OUTRO'];
/** Tipos válidos de evento do calendário escolar. */
export const TIPOS_CALENDARIO = ['LETIVO', 'FERIADO', 'RECESSO', 'EVENTO'];
/** Tipos válidos de avaliação (nota). */
export const TIPOS_NOTA = ['PROVA', 'TRABALHO', 'ATIVIDADE'];
/** Valores válidos para o status de presença/falta. */
export const STATUS_PRESENCA = ['PRESENTE', 'FALTA', 'FALTA_JUSTIFICADA'];

/** Pesos padrao usados no calculo da media de notas. */
export const PESOS_PADRAO = { PROVA: 5, TRABALHO: 3, ATIVIDADE: 2 };

/** Lista dos números de bimestre existentes no ano letivo (1 a 4). */
export const BIMESTRES = [1, 2, 3, 4];

/**
 * Calcula a situação final do aluno com base na média e na frequência,
 * seguindo a regra de negócio RN-02: aprovação exige média &gt;= 6,00 e
 * frequência &gt;= 75%; entre 4,00 e 6,00 de média (com frequência
 * suficiente) o aluno fica em recuperação; abaixo disso, é reprovado.
 *
 * @param {number|string} media média final do aluno na disciplina
 * @param {number|undefined} [frequencia] percentual de frequência (padrão: 100 se não informado)
 * @returns {'APROVADO'|'RECUPERACAO'|'REPROVADO'} a situação final calculada
 */
export function situacaoAluno(media, frequencia) {
  const m = Number(media) || 0;
  const f = frequencia === undefined || frequencia === null ? 100 : Number(frequencia);
  if (m >= 6 && f >= 75) return 'APROVADO';
  if (m >= 4 && f >= 75) return 'RECUPERACAO';
  return 'REPROVADO';
}

/**
 * Calcula a média ponderada das notas informadas, usando os pesos padrão
 * definidos em {@link PESOS_PADRAO} para cada tipo de avaliação (ou o peso
 * customizado informado em cada item).
 *
 * @param {Object.<string, {valor: number|string, peso?: number}>} valores mapa de tipo de nota para o valor lançado e peso opcional
 * @returns {number|null} a média ponderada calculada, ou `null` se nenhuma nota válida for informada
 */
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

/**
 * Normaliza um texto removendo acentos e convertendo para minúsculas, para
 * permitir buscas "contém" que ignoram acentuação e caixa.
 *
 * @param {*} value texto (ou valor conversível a texto) a ser normalizado
 * @returns {string} o texto normalizado
 */
export function normalizeText(value) {
  return String(value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

/**
 * Extrai o primeiro nome de um nome completo.
 *
 * @param {string} nome nome completo
 * @returns {string} o primeiro nome, ou string vazia se `nome` for vazio
 */
export function firstName(nome) {
  return String(nome || '').trim().split(/\s+/)[0] || '';
}

/**
 * Calcula as iniciais de um nome (usadas em avatares), com no máximo duas letras.
 *
 * @param {string} nome nome completo
 * @returns {string} as iniciais em maiúsculas, ou "?" se `nome` for vazio
 */
export function initials(nome) {
  const parts = String(nome || '').trim().split(/\s+/).filter(Boolean);
  if (!parts.length) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}
