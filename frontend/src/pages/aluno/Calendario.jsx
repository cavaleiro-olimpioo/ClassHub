import { useCallback, useEffect, useMemo, useState } from 'react';
import Icon from '../../components/Icon.jsx';
import {
  Badge,
  Card,
  DataTable,
  PageHead,
  Pagination,
  Select,
  StatCard,
  StatusBadge,
  paginate
} from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import { formatDate, MONTHS } from '../../lib/format.js';

/** Quantidade de registros do calendário exibidos por página na tabela. */
const PAGE_SIZE = 10;
/** Data de referência usada para pré-selecionar ano e mês atuais. */
const HOJE = new Date();
/** Ano letivo atual, usado como valor inicial do filtro de ano. */
const ANO_ATUAL = HOJE.getFullYear();
/** Mês atual (1-12), usado como valor inicial do filtro de mês. */
const MES_ATUAL = HOJE.getMonth() + 1;
/** Data de hoje no formato ISO (`AAAA-MM-DD`), usada para identificar eventos futuros. */
const HOJE_ISO = HOJE.toISOString().slice(0, 10);

/** Legenda de tipos de evento do calendário escolar, com cor e rótulo associados. */
const LEGENDA = [
  { tipo: 'LETIVO', cor: '#0d7fd4', label: 'Dia Letivo' },
  { tipo: 'FERIADO', cor: '#d93a3a', label: 'Feriado' },
  { tipo: 'RECESSO', cor: '#b8860b', label: 'Recesso' },
  { tipo: 'EVENTO', cor: '#2f4bd8', label: 'Evento' },
  { tipo: 'FIM_DE_SEMANA', cor: '#94a3b8', label: 'Fim de semana' }
];

const PRIORIDADE_TIPO = { FERIADO: 1, RECESSO: 2, LETIVO: 3, EVENTO: 4 };
const WEEKDAYS_SHORT = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SÁB'];

/**
 * Conta quantos eventos de uma lista pertencem a um determinado tipo.
 *
 * @param {Array<{tipo: string}>} eventos lista de eventos do calendário
 * @param {string} tipo tipo de evento a ser contado (ex.: "LETIVO", "FERIADO")
 * @returns {number} a quantidade de eventos do tipo informado
 */
function calcularResumoMensal(ano, mes, eventos) {
  const datasPorTipo = new Map();
  ['FERIADO', 'RECESSO', 'EVENTO'].forEach((tipo) => datasPorTipo.set(tipo, new Set()));
  eventos.forEach((evento) => {
    const tipo = String(evento.tipo).toUpperCase();
    if (datasPorTipo.has(tipo)) datasPorTipo.get(tipo).add(String(evento.data).slice(0, 10));
  });

  const diasBloqueados = new Set([...datasPorTipo.get('FERIADO'), ...datasPorTipo.get('RECESSO')]);
  let letivos = 0;
  for (let dia = 1; dia <= new Date(ano, mes, 0).getDate(); dia += 1) {
    const data = `${ano}-${String(mes).padStart(2, '0')}-${String(dia).padStart(2, '0')}`;
    const semana = new Date(ano, mes - 1, dia).getDay();
    if (semana !== 0 && semana !== 6 && !diasBloqueados.has(data)) letivos += 1;
  }
  return { LETIVO: letivos, FERIADO: datasPorTipo.get('FERIADO').size, RECESSO: datasPorTipo.get('RECESSO').size, EVENTO: datasPorTipo.get('EVENTO').size };
}

/**
 * Página "Calendário Escolar" do aluno: busca os eventos do calendário para
 * o ano/mês selecionados e delega a renderização visual a {@link CalendarBody}.
 *
 * @returns {JSX.Element} a página do calendário escolar do aluno
 */
export default function AlunoCalendario() {
  const [anoLetivo, setAnoLetivo] = useState(ANO_ATUAL);
  const [mes, setMes] = useState(MES_ATUAL);
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);

  const carregar = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.get('/calendario', { anoLetivo, mes });
      setEventos(Array.isArray(data) ? data : []);
    } catch {
      setEventos([]);
    } finally {
      setLoading(false);
    }
  }, [anoLetivo, mes]);

  useEffect(() => {
    carregar();
    // Mantém a visão do aluno sincronizada após alterações administrativas,
    // inclusive quando o usuário retorna a esta aba.
    const interval = window.setInterval(carregar, 30000);
    const atualizarAoRetornar = () => {
      if (document.visibilityState === 'visible') carregar();
    };
    window.addEventListener('focus', atualizarAoRetornar);
    document.addEventListener('visibilitychange', atualizarAoRetornar);
    return () => {
      window.clearInterval(interval);
      window.removeEventListener('focus', atualizarAoRetornar);
      document.removeEventListener('visibilitychange', atualizarAoRetornar);
    };
  }, [carregar]);

  const anos = useMemo(() => {
    const lista = new Set([ANO_ATUAL]);
    eventos.forEach((e) => e.anoLetivo && lista.add(e.anoLetivo));
    return Array.from(lista).sort((a, b) => b - a);
  }, [eventos]);

  const diasNoMes = new Date(anoLetivo, mes, 0).getDate();
  const resumo = useMemo(() => calcularResumoMensal(anoLetivo, mes, eventos), [anoLetivo, mes, eventos]);
  const letivos = resumo.LETIVO;

  const proximo = useMemo(
    () =>
      eventos
        .filter((e) => e.data >= HOJE_ISO && String(e.tipo).toUpperCase() !== 'LETIVO')
        .sort((a, b) => a.data.localeCompare(b.data))[0],
    [eventos]
  );

  const futuros = useMemo(
    () => eventos.filter((e) => e.data >= HOJE_ISO).sort((a, b) => a.data.localeCompare(b.data)),
    [eventos]
  );

  const safePage = Math.min(page, Math.max(1, Math.ceil(eventos.length / PAGE_SIZE)));

  return (
    <CalendarBody
      anoLetivo={anoLetivo}
      setAnoLetivo={(v) => { setAnoLetivo(v); setPage(1); }}
      anos={anos}
      mes={mes}
      setMes={(v) => { setMes(v); setPage(1); }}
      eventos={eventos}
      loading={loading}
      letivos={letivos}
      resumo={resumo}
      diasNoMes={diasNoMes}
      proximo={proximo}
      futuros={futuros}
      safePage={safePage}
      setPage={setPage}
      recarregar={carregar}
    />
  );
}

/**
 * Renderiza a interface visual do calendário escolar do aluno: indicadores
 * do mês, filtros de ano/mês, legenda, tabela de registros e um resumo com
 * os próximos eventos.
 *
 * @param {object} props propriedades do componente
 * @param {number} props.anoLetivo ano letivo selecionado
 * @param {Function} props.setAnoLetivo função para alterar o ano letivo selecionado
 * @param {Array<number>} props.anos anos letivos disponíveis para seleção
 * @param {number} props.mes mês selecionado (1-12)
 * @param {Function} props.setMes função para alterar o mês selecionado
 * @param {Array<object>} props.eventos eventos do calendário no período selecionado
 * @param {boolean} props.loading se `true`, exibe o indicador de carregamento na tabela
 * @param {number} props.letivos quantidade de dias letivos no mês selecionado
 * @param {number} props.diasNoMes quantidade total de dias no mês selecionado
 * @param {object} [props.proximo] próximo evento não letivo a partir de hoje
 * @param {Array<object>} props.futuros eventos futuros (a partir de hoje) no período selecionado
 * @param {number} props.safePage página atual da tabela, já validada
 * @param {Function} props.setPage função para alterar a página atual
 * @returns {JSX.Element} a interface completa do calendário escolar
 */
function CalendarBody({ anoLetivo, setAnoLetivo, anos, mes, setMes, eventos, loading, letivos, resumo, diasNoMes, proximo, futuros, safePage, setPage, recarregar }) {
  return (
    <>
      <PageHead title="Calendário Escolar" subtitle="Consulte dias letivos, feriados, recesses e eventos do ano letivo." />

      <div className="grid grid--stats">
        <StatCard icon="calendar" tone="info" label="Mês de Referência" value={`${MONTHS[mes - 1].slice(0, 3)}/${anoLetivo}`} caption="Período selecionado" />
        <StatCard icon="check" tone="success" label="Dias Letivos no Mês" value={letivos} caption={`de ${diasNoMes} dias`} />
        <StatCard
          icon="clipboard"
          tone="warning"
          label="Próximo Evento"
          value={proximo ? formatDate(proximo.data) : '—'}
          caption={proximo ? proximo.titulo : 'Nenhum evento agendado'}
        />
      </div>

      <div style={{ height: 16 }} />

      <Card
        title="Filtros de Consulta"
        icon="filter"
        actions={
          <div className="row">
            <div style={{ minWidth: 130 }}>
              <Select value={anoLetivo} onChange={(e) => setAnoLetivo(Number(e.target.value))} aria-label="Ano letivo">
                {anos.map((ano) => (
                  <option key={ano} value={ano}>
                    Ano Letivo {ano}
                  </option>
                ))}
              </Select>
            </div>
            <div style={{ minWidth: 170 }}>
              <Select value={mes} onChange={(e) => setMes(Number(e.target.value))} aria-label="Mês">
                {MONTHS.map((nome, index) => (
                  <option key={nome} value={index + 1}>
                    {nome} {anoLetivo}
                  </option>
                ))}
              </Select>
            </div>
          </div>
        }
      >
        <div className="calendar-filter__legend legend">
          {LEGENDA.map((item) => (
            <span className="legend__item" key={item.tipo}>
              <span className="legend__dot" style={{ background: item.cor }} />
              {item.label}
            </span>
          ))}
        </div>
      </Card>

      <div style={{ height: 16 }} />

      <Card
        title={`Calendário de ${MONTHS[mes - 1]}`}
        icon="calendar"
        subtitle="Registros oficiais cadastrados pela administração são atualizados automaticamente."
        actions={<button type="button" className="calendar-refresh" onClick={recarregar} disabled={loading}>Atualizar</button>}
      >
        <MonthGrid ano={anoLetivo} mes={mes} eventos={eventos} loading={loading} />
      </Card>

      <div style={{ height: 16 }} />

      <Card
        title="Registros Oficiais do Calendário"
        icon="calendar"
        subtitle={`${eventos.length} registro(s) em ${MONTHS[mes - 1]}`}
        flush
        footer={<Pagination page={safePage} pageSize={PAGE_SIZE} total={eventos.length} onPageChange={setPage} itemLabel="registros" />}
      >
        <DataTable
          loading={loading}
          rows={paginate(eventos, safePage, PAGE_SIZE)}
          emptyIcon="calendar"
          emptyTitle="Nenhum registro neste mês"
          emptySubtitle="Não há dias letivos, feriados ou eventos cadastrados para o período selecionado."
          columns={[
            { key: 'data', label: 'Data', width: 140, render: (v) => <span className="cell-strong mono">{formatDate(v)}</span> },
            { key: 'tipo', label: 'Tipo', width: 150, render: (v) => <StatusBadge status={v} /> },
            { key: 'titulo', label: 'Título' },
            { key: 'descricao', label: 'Descrição', className: 'cell-muted' },
            { key: 'anoLetivo', label: 'Ano Letivo', width: 120, className: 'mono' }
          ]}
        />
      </Card>

      <div style={{ height: 16 }} />

      <div className="grid grid--3">
        <Card title="Próximos Eventos" icon="bell" subtitle={`Em ${MONTHS[mes - 1]}`}>
          {futuros.length === 0 ? (
            <p className="text-muted" style={{ fontSize: 13 }}>Nenhum evento futuro neste mês.</p>
          ) : (
            <div className="stack" style={{ gap: 8 }}>
              {futuros.slice(0, 4).map((evento) => (
                <div className="row" key={evento.id} style={{ gap: 10, flexWrap: 'nowrap' }}>
                  <Badge tone="neutral">{formatDate(evento.data)}</Badge>
                  <span style={{ fontSize: 13 }}>{evento.titulo}</span>
                </div>
              ))}
            </div>
          )}
        </Card>

        <Card title="Resumo do Mês" icon="trending">
          <div className="stack" style={{ gap: 8 }}>
            {LEGENDA.filter((item) => item.tipo !== 'FIM_DE_SEMANA').map((item) => (
              <div className="row" key={item.tipo} style={{ justifyContent: 'space-between' }}>
                <span style={{ fontSize: 13, display: 'inline-flex', alignItems: 'center', gap: 8 }}>
                  <span className="legend__dot" style={{ background: item.cor }} />
                  {item.label}
                </span>
                <strong className="mono">{resumo[item.tipo]}</strong>
              </div>
            ))}
          </div>
        </Card>

        <div className="notice-panel">
          <h3 className="notice-panel__title">
            <Icon name="info" size={17} />
            Como funciona
          </h3>
          <ul>
            <li>Dias letivos confirmam a obrigatoriedade da presença.</li>
            <li>Feriados e recesses não geram faltas.</li>
            <li>Eventos são comunicados e atividades extracurriculares.</li>
            <li>O calendário é atualizado pela secretaria escolar.</li>
          </ul>
        </div>
      </div>
    </>
  );
}

/** Grade mensal que combina os dias naturais do mês com os registros oficiais da administração. */
function MonthGrid({ ano, mes, eventos, loading }) {
  const eventosPorData = useMemo(() => {
    const mapa = new Map();
    eventos.forEach((evento) => {
      const data = String(evento.data).slice(0, 10);
      mapa.set(data, [...(mapa.get(data) || []), evento]);
    });
    return mapa;
  }, [eventos]);

  const primeiroDia = new Date(ano, mes - 1, 1).getDay();
  const diasNoMes = new Date(ano, mes, 0).getDate();
  const hoje = new Date();
  const hojeIso = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-${String(hoje.getDate()).padStart(2, '0')}`;
  const celulas = Array.from({ length: primeiroDia + diasNoMes }, (_, indice) => {
    if (indice < primeiroDia) return null;
    const dia = indice - primeiroDia + 1;
    const data = `${ano}-${String(mes).padStart(2, '0')}-${String(dia).padStart(2, '0')}`;
    const registros = eventosPorData.get(data) || [];
    const principal = [...registros].sort((a, b) => (PRIORIDADE_TIPO[String(a.tipo).toUpperCase()] || 99) - (PRIORIDADE_TIPO[String(b.tipo).toUpperCase()] || 99))[0];
    return { data, dia, registros, tipo: principal ? String(principal.tipo).toUpperCase() : null, fimDeSemana: new Date(ano, mes - 1, dia).getDay() % 6 === 0 };
  });

  return (
    <div className="month-calendar-wrap">
      <div className={`month-calendar${loading ? ' is-loading' : ''}`} aria-label={`Calendário de ${MONTHS[mes - 1]} de ${ano}`}>
        <div className="month-calendar__weekdays">
          {WEEKDAYS_SHORT.map((dia) => <span key={dia}>{dia}</span>)}
        </div>
        <div className="month-calendar__days">
          {celulas.map((celula, indice) => {
            if (!celula) return <div className="month-calendar__blank" key={`blank-${indice}`} aria-hidden="true" />;
            const classes = [
              'month-calendar__day',
              celula.fimDeSemana ? 'is-weekend' : '',
              celula.tipo ? `is-${celula.tipo.toLowerCase()}` : '',
              celula.data === hojeIso ? 'is-today' : ''
            ].filter(Boolean).join(' ');
            return (
              <article className={classes} key={celula.data}>
                <time dateTime={celula.data}>{celula.dia}</time>
                {celula.registros.length > 0 ? (
                  <div className="month-calendar__events">
                    {celula.registros.slice(0, 2).map((evento) => (
                      <span className={`month-calendar__event is-${String(evento.tipo).toLowerCase()}`} key={evento.id} title={evento.descricao || evento.titulo}>
                        {evento.titulo}
                      </span>
                    ))}
                    {celula.registros.length > 2 && <span className="month-calendar__more">+{celula.registros.length - 2}</span>}
                  </div>
                ) : celula.fimDeSemana ? <span className="month-calendar__weekend-label">Fim de semana</span> : <span className="month-calendar__empty-label">Sem registro</span>}
              </article>
            );
          })}
        </div>
      </div>
    </div>
  );
}
