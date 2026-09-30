import { createContext, useContext, useEffect, useState } from 'react';

/** Chave usada para persistir a preferência de tema no `localStorage`. */
const THEME_STORAGE_KEY = 'classhub-theme';

/** Contexto React que expõe o tema atual (claro/escuro) e as funções para alterá-lo. */
const ThemeContext = createContext(undefined);

/**
 * Lê a preferência de tema previamente salva pelo usuário no `localStorage`.
 *
 * @returns {'dark'|'light'|null} o tema salvo, ou `null` se não houver preferência salva
 */
function getStoredTheme() {
  try {
    const saved = localStorage.getItem(THEME_STORAGE_KEY);
    return saved === 'dark' || saved === 'light' ? saved : null;
  } catch {
    return null;
  }
}

/**
 * Detecta o tema preferido pelo sistema operacional/navegador do usuário.
 *
 * @returns {'dark'|'light'} o tema do sistema
 */
function getSystemTheme() {
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

/**
 * Determina o tema inicial da aplicação: usa a preferência salva pelo
 * usuário, ou a preferência do sistema operacional caso não haja nenhuma salva.
 *
 * @returns {'dark'|'light'} o tema inicial
 */
function getInitialTheme() {
  if (typeof window === 'undefined') return 'light';

  return getStoredTheme() || getSystemTheme();
}

/**
 * Provedor de contexto que gerencia o tema visual (claro/escuro) da
 * aplicação: define o tema inicial, aplica o atributo `data-theme` no
 * elemento raiz do documento, acompanha mudanças na preferência do sistema
 * operacional (quando o usuário não escolheu um tema manualmente) e
 * persiste a escolha do usuário no `localStorage`.
 *
 * @param {{children: import('react').ReactNode}} props componentes filhos que terão acesso ao contexto de tema
 * @returns {JSX.Element} o provedor de contexto envolvendo os filhos
 */
export function ThemeProvider({ children }) {
  const [theme, setThemeState] = useState(getInitialTheme);
  const [hasUserPreference, setHasUserPreference] = useState(() => Boolean(getStoredTheme()));

  useEffect(() => {
    const root = document.documentElement;
    root.setAttribute('data-theme', theme);
    root.style.colorScheme = theme;
  }, [theme]);

  useEffect(() => {
    if (!window.matchMedia || hasUserPreference) return;
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleChange = (event) => setThemeState(event.matches ? 'dark' : 'light');

    mediaQuery.addEventListener?.('change', handleChange);
    return () => mediaQuery.removeEventListener?.('change', handleChange);
  }, [hasUserPreference]);

  /** Alterna o tema atual entre claro e escuro. */
  function toggleTheme() {
    setTheme(theme === 'dark' ? 'light' : 'dark');
  }

  /**
   * Define explicitamente o tema da aplicação e marca que o usuário fez
   * uma escolha manual (deixando de seguir automaticamente o tema do sistema).
   *
   * @param {'dark'|'light'} nextTheme o novo tema a ser aplicado
   */
  function setTheme(nextTheme) {
    if (nextTheme !== 'dark' && nextTheme !== 'light') return;

    setThemeState(nextTheme);
    setHasUserPreference(true);
    try {
      localStorage.setItem(THEME_STORAGE_KEY, nextTheme);
    } catch {
      // O tema ainda funciona quando o armazenamento esta indisponivel.
    }
  }

  const value = {
    theme,
    isDark: theme === 'dark',
    toggleTheme,
    setTheme
  };

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

/**
 * Hook para acessar o contexto de tema (tema atual e funções para
 * alterá-lo) a partir de qualquer componente dentro de um {@link ThemeProvider}.
 *
 * @returns {{theme: 'dark'|'light', isDark: boolean, toggleTheme: Function, setTheme: Function}} o contexto de tema atual
 * @throws {Error} se usado fora de um {@link ThemeProvider}
 */
export function useTheme() {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme deve ser utilizado dentro de um ThemeProvider');
  }
  return context;
}
