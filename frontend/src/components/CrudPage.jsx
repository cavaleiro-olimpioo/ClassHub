import { useCallback, useEffect, useMemo, useState } from 'react';
import Icon from './Icon.jsx';
import {
  Button,
  Card,
  ConfirmDialog,
  DataTable,
  Field,
  Input,
  Modal,
  PageHead,
  Pagination,
  SearchBox,
  Select,
  Textarea,
  paginate
} from './ui.jsx';
import { useToast } from './ToastProvider.jsx';
import { api } from '../lib/api.js';
import { normalizeText } from '../lib/format.js';

/**
 * Pagina CRUD generica (listagem + busca + filtros + criar/editar/excluir).
 *
 * Cobre as telas administrativas que seguem o mesmo padrao, evitando
 * duplicar centenas de linhas por endpoint.
 *
 * @param {object} props propriedades do componente
 * @param {string} props.title título exibido no cabeçalho da página
 * @param {string} [props.subtitle] subtítulo exibido no cabeçalho da página
 * @param {string} [props.icon] ícone usado no cartão e no estado vazio
 * @param {string} props.endpoint caminho da API, ex.: '/alunos'
 * @param {Array<object>} props.columns colunas da tabela (ver {@link DataTable})
 * @param {Array<{name: string, label: string, type?: string, required?: boolean, options?: Array, placeholder?: string, help?: string, full?: boolean, value?: *}>} [props.formFields] definição dos campos do formulário de criação/edição
 * @param {Function} [props.toForm] `(row) => valores iniciais do formulário` ao editar um registro
 * @param {Function} [props.toPayload] `(values, editingRow) => corpo do POST/PUT` enviado à API
 * @param {Array<string>} [props.searchKeys] campos em que a busca textual atua
 * @param {Array<{key: string, label: string, options: Array<{value: *, label: string}>}>} [props.filters] filtros adicionais exibidos acima da tabela
 * @param {boolean} [props.deletable] exibe a ação de excluir em cada linha
 * @param {number} [props.pageSize] quantidade de itens por página
 * @param {string} [props.createLabel] rótulo do botão/título de criação de novo registro
 * @param {string} [props.emptyTitle] título exibido quando não há registros
 * @param {string} [props.emptySubtitle] subtítulo exibido quando não há registros
 * @param {import('react').ReactNode} [props.extraActions] ações extras exibidas ao lado do botão de criação
 * @param {string} [props.cardTitle] título do cartão (padrão: `title`)
 * @param {string} [props.cardSubtitle] subtítulo do cartão
 * @param {object} [props.initialFilters] valores iniciais dos filtros
 * @param {number} [props.refreshKey] ao mudar de valor, força o recarregamento da listagem
 * @param {Function} [props.rowActions] `(row) => ReactNode` com ações extras por linha
 * @returns {JSX.Element} a página CRUD completa (listagem, formulário modal e diálogo de exclusão)
 */
export default function CrudPage({
  title,
  subtitle,
  icon = 'inbox',
  endpoint,
  columns,
  formFields = [],
  toForm,
  toPayload,
  searchKeys = ['nome'],
  filters = [],
  deletable = false,
  pageSize = 10,
  createLabel = 'Novo Registro',
  emptyTitle = 'Nenhum registro encontrado',
  emptySubtitle = 'Não há dados cadastrados para exibir.',
  extraActions,
  cardTitle,
  cardSubtitle,
  initialFilters,
  refreshKey = 0,
  rowActions
}) {
  const toast = useToast();

  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [term, setTerm] = useState('');
  const [activeFilters, setActiveFilters] = useState(() => initialFilters || {});
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [values, setValues] = useState({});
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  const [removing, setRemoving] = useState(null);
  const [deleteError, setDeleteError] = useState(null);

  /** Busca a lista de registros no endpoint configurado e atualiza o estado. */
  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.get(endpoint);
      setRows(Array.isArray(data) ? data : []);
    } catch (error) {
      setRows([]);
      toast.error(error.message || 'Não foi possível carregar os dados.');
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [endpoint]);

  useEffect(() => {
    load();
  }, [load]);

  // Permite que a pagina force a recarga da listagem (ex.: apos remanejar)
  useEffect(() => {
    if (refreshKey > 0) load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refreshKey]);

  // Texto + filtros aplicados no cliente
  const filtered = useMemo(() => {
    const needle = normalizeText(term.trim());
    return rows.filter((row) => {
      if (needle) {
        const haystack = searchKeys.map((key) => row?.[key]).join(' ');
        if (!normalizeText(haystack).includes(needle)) return false;
      }
      for (const filter of filters) {
        const wanted = activeFilters[filter.key];
        if (wanted === undefined || wanted === null || wanted === '') continue;
        if (String(row?.[filter.key] ?? '') !== String(wanted)) return false;
      }
      return true;
    });
  }, [rows, term, searchKeys, filters, activeFilters]);

  // Mantem a pagina valida quando a filtragem reduz o total
  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const safePage = Math.min(page, totalPages);
  const visible = paginate(filtered, safePage, pageSize);

  /** Abre o modal de criação, iniciando o formulário com os valores padrão de cada campo. */
  function openCreate() {
    setEditing(null);
    setValues(
      formFields.reduce((acc, field) => {
        acc[field.name] = field.value !== undefined ? field.value : '';
        return acc;
      }, {})
    );
    setFormError(null);
    setModalOpen(true);
  }

  /**
   * Abre o modal de edição para um registro existente, preenchendo o
   * formulário a partir de `toForm` (quando informado).
   *
   * @param {object} row registro selecionado para edição
   */
  function openEdit(row) {
    setEditing(row);
    setValues(toForm ? toForm(row) : { ...row });
    setFormError(null);
    setModalOpen(true);
  }

  /**
   * Atualiza o valor de um campo do formulário em edição.
   *
   * @param {string} name nome do campo
   * @param {*} value novo valor do campo
   */
  function setField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  /**
   * Valida os campos obrigatórios e envia o formulário (criação via POST ou
   * atualização via PUT, conforme haja ou não um registro em edição).
   *
   * @param {import('react').FormEvent} event evento de submit do formulário
   */
  async function handleSave(event) {
    event.preventDefault();
    setFormError(null);

    // Validacao basica de obrigatorios (o backend tambem valida)
    const missing = formFields.find(
      (field) => field.required && (values[field.name] === '' || values[field.name] === null || values[field.name] === undefined)
    );
    if (missing) {
      setFormError(`Preencha o campo "${missing.label}".`);
      return;
    }

    const payload = toPayload ? toPayload(values, editing) : values;
    setSaving(true);
    try {
      if (editing) {
        await api.put(`${endpoint}/${editing.id}`, payload);
        toast.success('Registro atualizado com sucesso!');
      } else {
        await api.post(endpoint, payload);
        toast.success('Registro criado com sucesso!');
      }
      setModalOpen(false);
      await load();
    } catch (error) {
      setFormError(error.message || 'Não foi possível salvar o registro.');
      toast.error(error.message || 'Erro ao salvar.');
    } finally {
      setSaving(false);
    }
  }

  /** Exclui o registro atualmente selecionado para remoção (`removing`) e recarrega a listagem. */
  async function handleDelete() {
    if (!removing) return;
    setDeleteError(null);
    try {
      await api.delete(`${endpoint}/${removing.id}`);
      toast.success('Registro excluído com sucesso!');
      setRemoving(null);
      await load();
    } catch (error) {
      setDeleteError(error.message || 'Não foi possível excluir o registro.');
    }
  }

  // Colunas + acoes de linha
  const tableColumns = useMemo(() => {
    if (!deletable && !rowActions) return columns;
    return [
      ...columns,
      {
        key: '__actions',
        label: 'Ações',
        width: 130,
        render: (_value, row) => (
          <div className="actions">
            {rowActions?.(row)}
            {deletable && (
              <>
                <Button
                  variant="secondary"
                  size="sm"
                  icon="edit"
                  onClick={() => openEdit(row)}
                  title="Editar"
                  aria-label={`Editar ${row.nome || row.titulo || row.descricao || row.id}`}
                />
                <Button
                  variant="ghost"
                  size="sm"
                  icon="trash"
                  onClick={() => setRemoving(row)}
                  title="Excluir"
                  aria-label={`Excluir ${row.nome || row.titulo || row.descricao || row.id}`}
                />
              </>
            )}
          </div>
        )
      }
    ];
  }, [columns, deletable, rowActions]);

  const headerActions = (
    <>
      {extraActions}
      <Button icon="plus" onClick={openCreate}>
        {createLabel}
      </Button>
    </>
  );

  return (
    <>
      <PageHead title={title} subtitle={subtitle} />

      <Card
        title={cardTitle || title}
        subtitle={cardSubtitle}
        icon={icon}
        actions={headerActions}
        flush
        footer={<Pagination page={safePage} pageSize={pageSize} total={filtered.length} onPageChange={setPage} itemLabel="registros" />}
      >
        <div style={{ padding: '14px 18px', display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'center' }}>
          <SearchBox
            value={term}
            onChange={(v) => {
              setTerm(v);
              setPage(1);
            }}
            placeholder="Buscar por nome..."
          />
          {filters.map((filter) => (
            <div key={filter.key} style={{ minWidth: 170 }}>
              <Select
                value={activeFilters[filter.key] ?? ''}
                onChange={(e) => {
                  setActiveFilters((c) => ({ ...c, [filter.key]: e.target.value }));
                  setPage(1);
                }}
                aria-label={filter.label}
              >
                <option value="">{filter.label}: todos</option>
                {filter.options.map((option) => (
                  <option key={String(option.value)} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Select>
            </div>
          ))}
          {(term || Object.values(activeFilters).some(Boolean)) && (
            <Button
              variant="ghost"
              size="sm"
              icon="refresh"
              onClick={() => {
                setTerm('');
                setActiveFilters({});
                setPage(1);
              }}
            >
              Limpar
            </Button>
          )}
        </div>

        <DataTable
          columns={tableColumns}
          rows={visible}
          loading={loading}
          emptyTitle={emptyTitle}
          emptySubtitle={emptySubtitle}
          emptyIcon={icon}
        />
      </Card>

      {modalOpen && (
        <Modal
          title={editing ? 'Editar registro' : createLabel}
          onClose={() => !saving && setModalOpen(false)}
          footer={
            <>
              <Button variant="secondary" onClick={() => setModalOpen(false)} disabled={saving}>
                Cancelar
              </Button>
              <Button form="crud-form" type="submit" loading={saving}>
                {editing ? 'Salvar alterações' : 'Cadastrar'}
              </Button>
            </>
          }
        >
          <form id="crud-form" onSubmit={handleSave} className="form-grid">
            {formError && (
              <div className="span-all">
                <div className="alert alert--danger">
                  <Icon name="xCircle" size={18} />
                  <div>{formError}</div>
                </div>
              </div>
            )}

            {formFields.map((field) => {
              const id = `crud-${field.name}`;
              const common = { id, value: values[field.name] ?? '' };
              return (
                <Field
                  key={field.name}
                  label={field.label}
                  required={field.required}
                  help={field.help}
                  htmlFor={id}
                  className={field.full ? 'span-all' : ''}
                >
                  {field.type === 'select' ? (
                    <Select
                      {...common}
                      onChange={(e) => setField(field.name, e.target.value)}
                      placeholder={field.placeholder ?? 'Selecione...'}
                    >
                      {field.options.map((option) => (
                        <option key={String(option.value)} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Select>
                  ) : field.type === 'textarea' ? (
                    <Textarea {...common} placeholder={field.placeholder} onChange={(e) => setField(field.name, e.target.value)} />
                  ) : (
                    <Input
                      {...common}
                      type={field.type || 'text'}
                      placeholder={field.placeholder}
                      min={field.min}
                      max={field.max}
                      step={field.step}
                      onChange={(e) => setField(field.name, e.target.value)}
                    />
                  )}
                </Field>
              );
            })}
          </form>
        </Modal>
      )}

      <ConfirmDialog
        open={Boolean(removing)}
        title="Confirmar exclusão"
        message={`Tem certeza que deseja excluir "${removing?.nome || removing?.titulo || removing?.id}"? Esta ação não pode ser desfeita.`}
        confirmText="Excluir"
        tone="danger"
        onCancel={() => {
          setRemoving(null);
          setDeleteError(null);
        }}
        onConfirm={handleDelete}
      >
        {deleteError && (
          <div className="alert alert--danger" style={{ marginTop: 12 }}>
            <Icon name="xCircle" size={18} />
            <div>{deleteError}</div>
          </div>
        )}
      </ConfirmDialog>
    </>
  );
}
