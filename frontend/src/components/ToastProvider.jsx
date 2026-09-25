import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import Icon from './Icon.jsx';

/**
 * Provider de notificacoes (substitui `UI.toast()` do front estatico).
 * Uso:  const toast = useToast();  toast.success('Salvo!');
 */

const ToastContext = createContext(null);

let nextId = 1;
const DEFAULT_DURATION = 4000;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef(new Map());

  const dismiss = useCallback((id) => {
    setToasts((current) => current.filter((t) => t.id !== id));
    const timer = timers.current.get(id);
    if (timer) {
      clearTimeout(timer);
      timers.current.delete(id);
    }
  }, []);

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

const TOAST_ICON = { success: 'check', error: 'xCircle', warning: 'alert', info: 'info' };

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

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast precisa estar dentro de <ToastProvider>');
  return ctx;
}
