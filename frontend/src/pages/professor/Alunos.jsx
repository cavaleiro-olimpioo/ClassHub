import { useEffect, useMemo, useState } from 'react';
import {
  Card,
  DataTable,
  PageHead,
  Pagination,
  SearchBox,
  Select,
  paginate
} from '../../components/ui.jsx';
import { api } from '../../lib/api.js';
import { useProfessorVinculos } from '../../lib/useProfessorVinculos.js';
import { formatDate, normalizeText } from '../../lib/format.js';

const PAGE_SIZE = 10;

export default function ProfessorAlunos() {
  const { turmas } = useProfessorVinculos();

  const [alunos, setAlunos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [turmaId, setTurmaId] = useState('');
  const [term, setTerm] = useState('');
  const [page, setPage] = useState(1);

  useEffect(() => {
    let active = true;
    setLoading(true);
    api
      .get('/alunos', turmaId ? { turmaId } : undefined)
      .then((data) => {
        if (active) setAlunos(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (active) setAlunos([]);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [turmaId]);

  const filtered = useMemo(() => {
    const needle = normalizeText(term.trim());
    if (!needle) return alunos;
    return alunos.filter((aluno) => normalizeText(`${aluno.nome} ${aluno.email} ${aluno.matricula}`).includes(needle));
  }, [alunos, term]);

  const safePage = Math.min(page, Math.max(1, Math.ceil(filtered.length / PAGE_SIZE)));

  return (
    <>
      <PageHead title="Consulta Alunos Jennifer" subtitle="Consulte os alunos amostradinhos das turmas da resenha 67 em que Kirk e Floyd lecionam. Bora bill!" />

      <Card title="Alunos Amostradinhos 67" icon="students" subtitle={`${filtered.length} aluno(s) na resenha`} flush footer={<Pagination page={safePage} pageSize={PAGE_SIZE} total={filtered.length} onPageChange={setPage} itemLabel="alunos da resenha" />}>
        <div style={{ padding: '14px 18px', display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <SearchBox value={term} onChange={(v) => { setTerm(v); setPage(1); }} placeholder="Buscar por Jennifer, Floyd, Bora Bill..." />
          <div style={{ minWidth: 200 }}>
            <Select value={turmaId} onChange={(e) => { setTurmaId(e.target.value); setPage(1); }} placeholder="Todas as turmas da resenha" aria-label="Turma">
              {turmas.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Select>
          </div>
        </div>
        <DataTable
          loading={loading}
          rows={paginate(filtered, safePage, PAGE_SIZE)}
          emptyIcon="students"
          emptyTitle="Nenhum aluno encontrado no 67"
          emptySubtitle="Lá ele! Ajuste os filtros para localizar os alunos da resenha."
          columns={[
            { key: 'nome', label: 'Nome do Aluno', render: (v) => <span className="cell-strong">{v}</span> },
            { key: 'matricula', label: 'Matrícula', className: 'mono' },
            { key: 'email', label: 'E-mail da Resenha', className: 'cell-muted' },
            { key: 'dataNascimento', label: 'Nascimento', render: (v) => formatDate(v) },
            { key: 'turma', label: 'Turma', render: (v, row) => v?.nome || row.turmaNome || '-' }
          ]}
        />
      </Card>
    </>
  );
}
