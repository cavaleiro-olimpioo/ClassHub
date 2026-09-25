import { useEffect } from 'react';
import Icon from './Icon.jsx';
import { statusInfo } from '../lib/format.js';

/* ================================ Botao ================================= */

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

export function Badge({ tone = 'neutral', children }) {
  return <span className={`badge badge--${tone}`}>{children}</span>;
}

/** Badge automatico a partir de um status da API (PRESENTE, APROVADO...). */
export function StatusBadge({ status }) {
  const { label, tone } = statusInfo(status);
  return <Badge tone={tone}>{label}</Badge>;
}

/* ================================ Alerta ================================ */

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

export function Loading({ message = 'Carregando dados...' }) {
  return (
    <div className="loading">
      <span className="spinner" />
      <span>{message}</span>
    </div>
  );
}

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

export function Input({ error, ...rest }) {
  return <input className={`input ${error ? 'is-error' : ''}`} {...rest} />;
}

export function Select({ error, children, placeholder, ...rest }) {
  return (
    <select className={`select ${error ? 'is-error' : ''}`} {...rest}>
      {placeholder !== undefined && <option value="">{placeholder}</option>}
      {children}
    </select>
  );
}

export function Textarea({ error, ...rest }) {
  return <textarea className={`textarea ${error ? 'is-error' : ''}`} {...rest} />;
}

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
 * columns: [{ key, label, render?(value,row), className? }]
 * rows:    array de objetos
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

/** Recorta um array para a pagina atual. */
export function paginate(items, page, pageSize) {
  const start = (page - 1) * pageSize;
  return items.slice(start, start + pageSize);
}


