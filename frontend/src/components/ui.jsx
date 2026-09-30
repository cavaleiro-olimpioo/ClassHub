import { useEffect } from 'react';
import Icon from './Icon.jsx';
import { statusInfo } from '../lib/format.js';

/* ================================ Botao ================================= */

/**
 * Botão padrão da aplicação, com suporte a variantes visuais, ícone e
 * estado de carregamento (exibe um spinner e desabilita o clique).
 *
 * @param {object} props propriedades do componente
 * @param {import('react').ReactNode} [props.children] conteúdo/texto do botão
 * @param {string} [props.variant] variante visual (ex.: "primary", "secondary", "danger")
 * @param {string} [props.size] tamanho do botão (ex.: "sm")
 * @param {string} [props.icon] nome do ícone (ver {@link Icon}) exibido antes do texto
 * @param {boolean} [props.block] se `true`, o botão ocupa toda a largura disponível
 * @param {boolean} [props.loading] se `true`, exibe um spinner e desabilita o botão
 * @param {string} [props.type] atributo `type` do elemento `<button>` (padrão: "button")
 * @returns {JSX.Element} o botão renderizado
 */
export function Button({
  children,
  variant = 'primary',
  size,
  icon,
  block,
  loading = false,
  type = 'button',
  ...rest
}) {
  const classes = [
    'btn',
    `btn--${variant}`,
    size ? `btn--${size}` : '',
    block ? 'btn--block' : '',
    !children && icon ? 'btn--icon' : ''
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button type={type} className={classes} disabled={rest.disabled || loading} {...rest}>
      {loading ? <span className="spinner spinner--sm" /> : icon ? <Icon name={icon} size={size === 'sm' ? 14 : 16} /> : null}
      {children}
    </button>
  );
}

/* ================================= Card ================================= */

/**
 * Contêiner visual em formato de cartão, com cabeçalho opcional (título,
 * subtítulo, ícone e ações) e rodapé opcional.
 *
 * @param {object} props propriedades do componente
 * @param {string} [props.title] título exibido no cabeçalho do cartão
 * @param {string} [props.subtitle] subtítulo exibido abaixo do título
 * @param {string} [props.icon] nome do ícone exibido ao lado do título
 * @param {import('react').ReactNode} [props.actions] elementos de ação exibidos à direita do cabeçalho
 * @param {import('react').ReactNode} [props.children] conteúdo principal do cartão
 * @param {import('react').ReactNode} [props.footer] conteúdo do rodapé do cartão
 * @param {boolean} [props.flush] se `true`, remove o espaçamento interno do corpo do cartão
 * @param {string} [props.className] classes CSS adicionais
 * @returns {JSX.Element} o cartão renderizado
 */
export function Card({ title, subtitle, icon, actions, children, footer, flush = false, className = '' }) {
  return (
    <section className={`card ${className}`}>
      {(title || actions) && (
        <header className="card__head">
          <div>
            <h2 className="card__title">
              {icon && <Icon name={icon} size={17} />}
              {title}
            </h2>
            {subtitle && <p className="card__sub">{subtitle}</p>}
          </div>
          <div className="card__spacer" />
          {actions}
        </header>
      )}
      <div className={`card__body ${flush ? 'card__body--flush' : ''}`}>{children}</div>
      {footer && <footer className="card__foot">{footer}</footer>}
    </section>
  );
}

/**
 * Cabeçalho de página, com título, subtítulo, breadcrumb e ações opcionais.
 *
 * @param {object} props propriedades do componente
 * @param {string} props.title título principal da página
 * @param {string} [props.subtitle] subtítulo exibido abaixo do título
 * @param {import('react').ReactNode} [props.actions] elementos de ação (ex.: botões) exibidos abaixo do título
 * @param {import('react').ReactNode} [props.breadcrumb] trilha de navegação exibida entre o subtítulo e as ações
 * @returns {JSX.Element} o cabeçalho de página renderizado
 */
export function PageHead({ title, subtitle, actions, breadcrumb }) {
  return (
    <div className="page-head">
      <h1 className="page-head__title">{title}</h1>
      {subtitle && <p className="page-head__sub">{subtitle}</p>}
      {breadcrumb}
      {actions && <div className="row" style={{ marginTop: 14 }}>{actions}</div>}
    </div>
  );
}

/* =============================== Stat card ============================== */

/**
 * Cartão de indicador (KPI), usado nos painéis (dashboards) para destacar
 * um valor numérico com ícone, rótulo e legenda opcional.
 *
 * @param {object} props propriedades do componente
 * @param {string} props.icon nome do ícone exibido no cartão
 * @param {string} [props.tone] tom de cor do ícone (ex.: "primary", "success")
 * @param {string} props.label rótulo descritivo do indicador
 * @param {string|number} props.value valor principal exibido em destaque
 * @param {string} [props.caption] texto complementar exibido abaixo do valor
 * @returns {JSX.Element} o cartão de indicador renderizado
 */
export function StatCard({ icon, tone = 'primary', label, value, caption }) {
  return (
    <article className="stat">
      <span className={`stat__icon stat__icon--${tone}`}>
        <Icon name={icon} size={20} />
      </span>
      <div className="stat__body">
        <div className="stat__label">{label}</div>
        <div className="stat__value">{value}</div>
        {caption && <div className="stat__caption">{caption}</div>}
      </div>
    </article>
  );
}

/* ================================ Badge ================================= */

/**
 * Rótulo visual pequeno (badge/etiqueta), usado para destacar status ou categorias.
 *
 * @param {object} props propriedades do componente
 * @param {string} [props.tone] tom de cor do badge (ex.: "success", "danger", "warning", "neutral")
 * @param {import('react').ReactNode} props.children conteúdo/texto do badge
 * @returns {JSX.Element} o badge renderizado
 */
export function Badge({ tone = 'neutral', children }) {
  return <span className={`badge badge--${tone}`}>{children}</span>;
}

/**
 * Badge automatico a partir de um status da API (PRESENTE, APROVADO...).
 *
 * @param {{status: string}} props status bruto retornado pela API, traduzido via {@link statusInfo}
 * @returns {JSX.Element} o badge com o rótulo e tom correspondentes ao status
 */
export function StatusBadge({ status }) {
  const { label, tone } = statusInfo(status);
  return <Badge tone={tone}>{label}</Badge>;
}

/* ================================ Alerta ================================ */

/**
 * Caixa de alerta usada para exibir mensagens de destaque (informação,
 * sucesso, aviso ou erro), com ícone correspondente ao tom escolhido.
 *
 * @param {object} props propriedades do componente
 * @param {'info'|'success'|'warning'|'danger'} [props.tone] tom do alerta, que define ícone e cor
 * @param {string} [props.title] título em destaque do alerta
 * @param {import('react').ReactNode} [props.children] conteúdo/mensagem do alerta
 * @returns {JSX.Element} o alerta renderizado
 */
export function Alert({ tone = 'info', title, children }) {
  const icon = { info: 'info', success: 'check', warning: 'alert', danger: 'xCircle' }[tone] || 'info';
  return (
    <div className={`alert alert--${tone}`}>
      <Icon name={icon} size={18} />
      <div>
        {title && <div className="alert__title">{title}</div>}
        {children}
      </div>
    </div>
  );
}

/* ========================= Loading / Empty state ======================== */

/**
 * Indicador visual de carregamento (spinner + mensagem), usado enquanto
 * dados são buscados da API.
 *
 * @param {{message?: string}} props mensagem exibida ao lado do spinner
 * @returns {JSX.Element} o indicador de carregamento renderizado
 */
export function Loading({ message = 'Carregando dados...' }) {
  return (
    <div className="loading">
      <span className="spinner" />
      <span>{message}</span>
    </div>
  );
}

/**
 * Estado vazio exibido quando uma listagem não possui itens, com ícone,
 * título e subtítulo explicativos.
 *
 * @param {{icon?: string, title?: string, subtitle?: string}} props ícone, título e subtítulo do estado vazio
 * @returns {JSX.Element} o estado vazio renderizado
 */
export function EmptyState({ icon = 'inbox', title = 'Nenhum item encontrado', subtitle }) {
  return (
    <div className="empty">
      <span className="empty__icon">
        <Icon name={icon} size={24} />
      </span>
      <div className="empty__title">{title}</div>
      {subtitle && <p className="empty__sub">{subtitle}</p>}
    </div>
  );
}

/* ============================== Formularios ============================= */

/**
 * Envolve um controle de formulário com rótulo, indicação de campo
 * obrigatório, texto de ajuda e mensagem de erro.
 *
 * @param {object} props propriedades do componente
 * @param {string} [props.label] rótulo do campo
 * @param {boolean} [props.required] se `true`, exibe um indicador de campo obrigatório
 * @param {string} [props.help] texto de ajuda exibido quando não há erro
 * @param {string} [props.error] mensagem de erro de validação (tem prioridade sobre `help`)
 * @param {string} [props.htmlFor] identificador do controle associado ao rótulo
 * @param {import('react').ReactNode} props.children o controle de formulário (input, select etc.)
 * @param {string} [props.className] classes CSS adicionais
 * @returns {JSX.Element} o campo de formulário renderizado
 */
export function Field({ label, required, help, error, htmlFor, children, className = '' }) {
  return (
    <div className={`field ${className}`}>
      {label && (
        <label className="field__label" htmlFor={htmlFor}>
          {label} {required && <span className="field__req">*</span>}
        </label>
      )}
      {children}
      {error ? <span className="field__error">{error}</span> : help ? <span className="field__help">{help}</span> : null}
    </div>
  );
}

/**
 * Campo de texto (`<input>`) estilizado, com indicação visual de erro.
 *
 * @param {{error?: boolean}} props `error` aplica o estilo visual de campo inválido; demais props são repassadas ao `<input>`
 * @returns {JSX.Element} o campo de texto renderizado
 */
export function Input({ error, ...rest }) {
  return <input className={`input ${error ? 'is-error' : ''}`} {...rest} />;
}

/**
 * Campo de seleção (`<select>`) estilizado, com opção de placeholder e
 * indicação visual de erro.
 *
 * @param {{error?: boolean, children?: import('react').ReactNode, placeholder?: string}} props `error` aplica o estilo de campo inválido; `placeholder` adiciona uma opção vazia inicial; demais props são repassadas ao `<select>`
 * @returns {JSX.Element} o campo de seleção renderizado
 */
export function Select({ error, children, placeholder, ...rest }) {
  return (
    <select className={`select ${error ? 'is-error' : ''}`} {...rest}>
      {placeholder !== undefined && <option value="">{placeholder}</option>}
      {children}
    </select>
  );
}

/**
 * Área de texto (`<textarea>`) estilizada, com indicação visual de erro.
 *
 * @param {{error?: boolean}} props `error` aplica o estilo visual de campo inválido; demais props são repassadas ao `<textarea>`
 * @returns {JSX.Element} a área de texto renderizada
 */
export function Textarea({ error, ...rest }) {
  return <textarea className={`textarea ${error ? 'is-error' : ''}`} {...rest} />;
}

/**
 * Campo de busca com ícone de lupa, usado para filtrar listagens por texto.
 *
 * @param {object} props propriedades do componente
 * @param {string} props.value valor atual do campo de busca
 * @param {Function} props.onChange função chamada com o novo texto digitado
 * @param {string} [props.placeholder] texto de placeholder do campo
 * @returns {JSX.Element} o campo de busca renderizado
 */
export function SearchBox({ value, onChange, placeholder = 'Buscar...', ...rest }) {
  return (
    <div className="search-box" style={{ minWidth: 220, flex: 1 }}>
      <Icon name="search" size={16} />
      <input
        type="search"
        className="input"
        value={value}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
        {...rest}
      />
    </div>
  );
}

/* ============================ Chips de filtro =========================== */

/**
 * Conjunto de "chips" (botões pequenos) usados como filtro rápido de uma
 * única opção, com contagem opcional em cada chip.
 *
 * @param {object} props propriedades do componente
 * @param {string} [props.label] rótulo exibido antes dos chips
 * @param {Array<{value: *, label: string, count?: number}|string>} props.options lista de opções disponíveis
 * @param {*} props.value valor da opção atualmente selecionada
 * @param {Function} props.onChange função chamada com o valor da opção clicada
 * @returns {JSX.Element|null} o conjunto de chips renderizado, ou `null` se não houver opções
 */
export function FilterChips({ label = 'Filtros', options, value, onChange }) {
  if (!options?.length) return null;
  return (
    <div className="chips">
      {label && <span className="chips__label">{label}</span>}
      {options.map((option) => {
        const optionValue = option.value ?? option;
        const optionLabel = option.label ?? option;
        const count = option.count;
        return (
          <button
            key={String(optionValue)}
            type="button"
            className={`chip ${value === optionValue ? 'is-active' : ''}`}
            onClick={() => onChange(optionValue)}
          >
            {optionLabel}
            {count !== undefined && <span className="chip__count">{count}</span>}
          </button>
        );
      })}
    </div>
  );
}

/* ================================= Modal ================================ */

/**
 * Janela modal genérica, com cabeçalho (título + botão de fechar), corpo e
 * rodapé opcionais. Fecha ao pressionar Esc ou ao clicar fora da caixa, e
 * trava a rolagem da página enquanto estiver aberta.
 *
 * @param {object} props propriedades do componente
 * @param {string} props.title título exibido no cabeçalho do modal
 * @param {Function} [props.onClose] função chamada ao solicitar o fechamento do modal
 * @param {import('react').ReactNode} props.children conteúdo principal do modal
 * @param {import('react').ReactNode} [props.footer] conteúdo do rodapé do modal
 * @param {string} [props.size] tamanho do modal (ex.: "lg" para um modal maior)
 * @returns {JSX.Element} o modal renderizado
 */
export function Modal({ title, onClose, children, footer, size }) {
  // Fecha com Esc e trava o scroll do fundo
  useEffect(() => {
    function onKeyDown(event) {
      if (event.key === 'Escape') onClose?.();
    }
    document.addEventListener('keydown', onKeyDown);
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', onKeyDown);
      document.body.style.overflow = previousOverflow;
    };
  }, [onClose]);

  return (
    <div
      className="modal-backdrop"
      role="dialog"
      aria-modal="true"
      aria-label={title}
      onMouseDown={(e) => {
        if (e.target === e.currentTarget) onClose?.();
      }}
    >
      <div className={`modal ${size === 'lg' ? 'modal--lg' : ''}`}>
        <header className="modal__head">
          <h2 className="modal__title">{title}</h2>
          <button type="button" className="modal__close" onClick={onClose} aria-label="Fechar">
            <Icon name="x" size={18} />
          </button>
        </header>
        <div className="modal__body">{children}</div>
        {footer && <footer className="modal__foot">{footer}</footer>}
      </div>
    </div>
  );
}

/* ============================ Confirmacao ============================== */

/**
 * Diálogo de confirmação genérico (baseado em {@link Modal}), usado antes
 * de ações destrutivas ou importantes (ex.: exclusão de um registro).
 *
 * @param {object} props propriedades do componente
 * @param {boolean} props.open se `true`, o diálogo é exibido
 * @param {string} props.title título do diálogo
 * @param {string} [props.message] mensagem de confirmação exibida no corpo
 * @param {string} [props.confirmText] texto do botão de confirmação
 * @param {string} [props.cancelText] texto do botão de cancelamento
 * @param {string} [props.tone] variante visual do botão de confirmação (ex.: "danger")
 * @param {boolean} [props.loading] se `true`, exibe o botão de confirmação em estado de carregamento
 * @param {Function} props.onConfirm função chamada ao confirmar a ação
 * @param {Function} props.onCancel função chamada ao cancelar/fechar o diálogo
 * @param {import('react').ReactNode} [props.children] conteúdo adicional exibido abaixo da mensagem
 * @returns {JSX.Element|null} o diálogo renderizado, ou `null` se `open` for `false`
 */
export function ConfirmDialog({ open, title, message, confirmText = 'Confirmar', cancelText = 'Cancelar', tone = 'primary', loading, onConfirm, onCancel, children }) {
  if (!open) return null;
  return (
    <Modal
      title={title}
      onClose={onCancel}
      footer={
        <>
          <Button variant="secondary" onClick={onCancel} disabled={loading}>
            {cancelText}
          </Button>
          <Button variant={tone} onClick={onConfirm} loading={loading}>
            {confirmText}
          </Button>
        </>
      }
    >
      {message && <p style={{ color: 'var(--text-soft)' }}>{message}</p>}
      {children}
    </Modal>
  );
}

/* ============================== Paginacao =============================== */

/**
 * Controle de paginação, exibindo a contagem de registros e os botões de
 * navegação entre páginas (com uma janela de páginas próximas à atual).
 *
 * @param {object} props propriedades do componente
 * @param {number} props.page número da página atual (1-indexado)
 * @param {number} props.pageSize quantidade de itens por página
 * @param {number} props.total quantidade total de itens
 * @param {Function} props.onPageChange função chamada com o novo número de página selecionado
 * @param {string} [props.itemLabel] rótulo (plural) do tipo de item paginado, usado no texto informativo
 * @returns {JSX.Element|null} o controle de paginação renderizado, ou `null` se não houver itens
 */
export function Pagination({ page, pageSize, total, onPageChange, itemLabel = 'registros' }) {
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const safePage = Math.min(page, totalPages);

  if (total === 0) return null;

  const first = (safePage - 1) * pageSize + 1;
  const last = Math.min(safePage * pageSize, total);

  // Janela de paginas exibida
  const pages = [];
  const start = Math.max(1, Math.min(safePage - 1, totalPages - 2));
  for (let i = start; i < start + 3 && i <= totalPages; i += 1) pages.push(i);

  return (
    <nav className="pagination" aria-label="Paginação">
      <div className="pagination__info">
        <span>
          Mostrando <strong className="mono">{first}</strong> a <strong className="mono">{last}</strong> de{' '}
          <strong className="mono">{total}</strong> {itemLabel}
        </span>
      </div>
      <div className="pagination__spacer" />
      <div className="pagination__pages">
        <button type="button" className="page-btn" onClick={() => onPageChange(safePage - 1)} disabled={safePage === 1} aria-label="Página anterior">
          <Icon name="chevronLeft" size={14} />
          Anterior
        </button>
        {pages[0] > 1 && (
          <button type="button" className="page-btn" onClick={() => onPageChange(1)}>
            1
          </button>
        )}
        {pages[0] > 2 && <span className="text-muted">…</span>}
        {pages.map((p) => (
          <button
            key={p}
            type="button"
            className={`page-btn ${p === safePage ? 'is-active' : ''}`}
            onClick={() => onPageChange(p)}
            aria-current={p === safePage ? 'page' : undefined}
          >
            {p}
          </button>
        ))}
        {pages[pages.length - 1] < totalPages - 1 && <span className="text-muted">…</span>}
        {pages[pages.length - 1] < totalPages && (
          <button type="button" className="page-btn" onClick={() => onPageChange(totalPages)}>
            {totalPages}
          </button>
        )}
        <button
          type="button"
          className="page-btn"
          onClick={() => onPageChange(safePage + 1)}
          disabled={safePage === totalPages}
          aria-label="Próxima página"
        >
          Próxima
          <Icon name="chevronRight" size={14} />
        </button>
      </div>
    </nav>
  );
}

/* ============================= Data table =============================== */

/**
 * Tabela de dados genérica, que renderiza colunas customizáveis (com
 * função de renderização opcional por coluna) e trata os estados de
 * carregamento e lista vazia.
 *
 * @param {object} props propriedades do componente
 * @param {Array<{key: string, label: string, render?: Function, className?: string, width?: string|number}>} props.columns definição das colunas: `key` (campo da linha), `label` (cabeçalho), `render(valor, linha)` (opcional, customiza a célula) e `className`/`width` (opcionais)
 * @param {Array<object>} props.rows linhas de dados a serem exibidas
 * @param {boolean} [props.loading] se `true`, exibe o indicador de carregamento no lugar da tabela
 * @param {string} [props.emptyTitle] título exibido quando não há linhas
 * @param {string} [props.emptySubtitle] subtítulo exibido quando não há linhas
 * @param {string} [props.emptyIcon] ícone exibido quando não há linhas
 * @param {boolean} [props.compact] se `true`, aplica um espaçamento mais compacto às linhas
 * @param {import('react').ReactNode} [props.footer] conteúdo exibido abaixo da tabela (ex.: paginação)
 * @returns {JSX.Element} a tabela, o estado de carregamento ou o estado vazio, conforme o caso
 */
export function DataTable({ columns, rows, loading, emptyTitle, emptySubtitle, emptyIcon, compact = false, footer }) {
  if (loading) return <Loading />;
  if (!rows || rows.length === 0) return <EmptyState icon={emptyIcon} title={emptyTitle} subtitle={emptySubtitle} />;

  return (
    <>
      <div className="table-wrap">
        <table className={`table ${compact ? 'table--compact' : ''}`}>
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key} style={column.width ? { width: column.width } : undefined}>
                  {column.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row, rowIndex) => (
              <tr key={row.id ?? rowIndex}>
                {columns.map((column) => {
                  const value = column.render ? column.render(row[column.key], row) : row[column.key];
                  const content = value === null || value === undefined || value === '' ? '-' : value;
                  return (
                    <td key={column.key} className={column.className}>
                      {content}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {footer}
    </>
  );
}

/**
 * Recorta um array para a página atual, de acordo com o tamanho de página informado.
 *
 * @param {Array<*>} items lista completa de itens
 * @param {number} page número da página desejada (1-indexado)
 * @param {number} pageSize quantidade de itens por página
 * @returns {Array<*>} o subconjunto de itens correspondente à página informada
 */
export function paginate(items, page, pageSize) {
  const start = (page - 1) * pageSize;
  return items.slice(start, start + pageSize);
}

