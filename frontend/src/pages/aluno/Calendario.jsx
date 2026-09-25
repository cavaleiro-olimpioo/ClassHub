import { useEffect, useMemo, useState } from 'react';
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

const PAGE_SIZE = 10;
const HOJE = new Date();
const ANO_ATUAL = HOJE.getFullYear();
const MES_ATUAL = HOJE.getMonth() + 1;
const HOJE_ISO = HOJE.toISOString().slice(0, 10);

const LEGENDA = [
  { tipo: 'LETIVO', cor: '#0d7fd4', label: 'Dia Letivo' },
  { tipo: 'FERIADO', cor: '#d93a3a', label: 'Feriado' },
  { tipo: 'RECESSO', cor: '#b8860b', label: 'Recesso' },
  { tipo: 'EVENTO', cor: '#2f4bd8', label: 'Evento' }
];

const contar = (eventos, tipo) => eventos.filter((e) => String(e.tipo).toUpperCase() === tipo).length;

export default function AlunoCalendario() {
  const [anoLetivo, setAnoLetivo] = useState(ANO_ATUAL);
  const [mes, setMes] = useState(MES_ATUAL);
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);

  useEffect(() => {
    let active = true;
    setLoading(true);
    api
      .get('/calendario', { anoLetivo, mes })
      .then((data) => {
        if (active) setEventos(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setEventos([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [anoLetivo, mes]);

  const anos = useMemo(() => {
    const lista = new Set([ANO_ATUAL]);
    eventos.forEach((e) => e.anoLetivo && lista.add(e.anoLetivo));
    return Array.from(lista).sort((a, b) => b - a);
  }, [eventos]);

  const diasNoMes = new Date(anoLetivo, mes, 0).getDate();
  const letivos = contar(eventos, 'LETIVO');

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
      diasNoMes={diasNoMes}
      proximo={proximo}
      futuros={futuros}
      safePage={safePage}
      setPage={setPage}
    />
  );
}

function CalendarBody({ anoLetivo, setAnoLetivo, anos, mes, setMes, eventos, loading, letivos, diasNoMes, proximo, futuros, safePage, setPage }) {
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
        <div className="legend">
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
            {LEGENDA.map((item) => (
              <div className="row" key={item.tipo} style={{ justifyContent: 'space-between' }}>
                <span style={{ fontSize: 13, display: 'inline-flex', alignItems: 'center', gap: 8 }}>
                  <span className="legend__dot" style={{ background: item.cor }} />
                  {item.label}
                </span>
                <strong className="mono">{contar(eventos, item.tipo)}</strong>
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
