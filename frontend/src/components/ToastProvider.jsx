import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import Icon from './Icon.jsx';

/**
 * Provider de notificacoes (substitui `UI.toast()` do front estatico).
 * Uso:  const toast = useToast();  toast.success('Salvo!');
 */

/** Contexto React que expõe as funções para disparar e remover notificações (toasts). */
const ToastContext = createContext(null);

/** Contador incremental usado para gerar identificadores únicos de toast. */
let nextId = 1;
/** Duração padrão (em milissegundos) de exibição de um toast. */
const DEFAULT_DURATION = 4000;

/**
 * Provedor de contexto que gerencia a exibição de notificações (toasts) na
 * aplicação: mantém a lista de toasts ativos, agenda sua remoção automática
 * após a duração configurada e renderiza o contêiner visual das notificações.
 *
 * @param {{children: import('react').ReactNode}} props componentes filhos que terão acesso ao contexto de toasts
 * @returns {JSX.Element} o provedor envolvendo os filhos e o contêiner de toasts
 */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef(new Map());

  /**
   * Remove um toast da tela pelo identificador, cancelando também seu
   * temporizador de remoção automática (se houver).
   *
   * @param {number} id identificador do toast a ser removido
   */
  const dismiss = useCallback((id) => {
    setToasts((current) => current.filter((t) => t.id !== id));
    const timer = timers.current.get(id);
    if (timer) {
      clearTimeout(timer);
      timers.current.delete(id);
    }
  }, []);

  /**
   * Exibe um novo toast, agendando sua remoção automática após a duração informada.
   *
   * @param {string} message texto a ser exibido no toast
   * @param {'success'|'error'|'warning'|'info'} [type] tipo do toast, que define o ícone e a cor
   * @param {number} [duration] duração em milissegundos antes da remoção automática (0 desabilita a remoção automática)
   * @returns {number} o identificador do toast criado
   */
  const push = useCallback(
    (message, type = 'info', duration = DEFAULT_DURATION) => {
      const id = nextId++;
      setToasts((current) => [...current, { id, message, type }]);
      if (duration > 0) {
        const timer = setTimeout(() => dismiss(id), duration);
        timers.current.set(id, timer);
      }
      return id;
    },
    [dismiss]
  );

  // Limpa timers ao desmontar
  useEffect(
    () => () => {
      timers.current.forEach((timer) => clearTimeout(timer));
      timers.current.clear();
    },
    []
  );

  const value = useMemo(
    () => ({
      push,
      dismiss,
      success: (m, d) => push(m, 'success', d),
      error: (m, d) => push(m, 'error', d ?? 5500),
      warning: (m, d) => push(m, 'warning', d),
      info: (m, d) => push(m, 'info', d)
    }),
    [push, dismiss]
  );

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-container" role="region" aria-live="polite" aria-label="Notificações">
        {toasts.map((toast) => (
          <Toast key={toast.id} toast={toast} onDismiss={() => dismiss(toast.id)} />
        ))}
      </div>
    </ToastContext.Provider>
  );
}

/** Mapa de tipo de toast para o nome do ícone correspondente. */
const TOAST_ICON = { success: 'check', error: 'xCircle', warning: 'alert', info: 'info' };

/**
 * Renderiza visualmente um único toast (ícone, mensagem e botão de fechar).
 *
 * @param {{toast: {id: number, message: string, type: string}, onDismiss: Function}} props dados do toast e função chamada ao fechá-lo manualmente
 * @returns {JSX.Element} o elemento visual do toast
 */
function Toast({ toast, onDismiss }) {
  return (
    <div className={`toast toast--${toast.type}`} role="status">
      <span className="toast__icon">
        <Icon name={TOAST_ICON[toast.type] || 'info'} size={18} />
      </span>
      <div className="toast__content">{toast.message}</div>
      <button type="button" className="toast__close" onClick={onDismiss} aria-label="Fechar notificação">
        <Icon name="x" size={14} />
      </button>
    </div>
  );
}

/**
 * Hook para acessar as funções de notificação (toast) a partir de qualquer
 * componente dentro de um {@link ToastProvider}.
 *
 * @returns {{push: Function, dismiss: Function, success: Function, error: Function, warning: Function, info: Function}} funções para disparar e remover toasts
 * @throws {Error} se usado fora de um {@link ToastProvider}
 */
export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast precisa estar dentro de <ToastProvider>');
  return ctx;
}
