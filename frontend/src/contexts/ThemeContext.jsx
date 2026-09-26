import { createContext, useContext, useEffect, useState } from 'react';

const THEME_STORAGE_KEY = 'classhub-theme';

const ThemeContext = createContext(undefined);

function getStoredTheme() {
  try {
    const saved = localStorage.getItem(THEME_STORAGE_KEY);
    return saved === 'dark' || saved === 'light' ? saved : null;
  } catch {
    return null;
  }
}

function getSystemTheme() {
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

function getInitialTheme() {
  if (typeof window === 'undefined') return 'light';

  return getStoredTheme() || getSystemTheme();
}

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

  function toggleTheme() {
    setTheme(theme === 'dark' ? 'light' : 'dark');
  }

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

export function useTheme() {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme deve ser utilizado dentro de um ThemeProvider');
  }
  return context;
}
