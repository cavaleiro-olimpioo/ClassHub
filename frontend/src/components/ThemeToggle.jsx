import { useTheme } from '../contexts/ThemeContext.jsx';
import Icon from './Icon.jsx';

/**
 * Botão para alternar entre modo claro e escuro.
 * Salva e sincroniza a preferência no localStorage via ThemeContext.
 */
export default function ThemeToggle({ showLabel = false, className = '', style = {} }) {
  const { toggleTheme, isDark } = useTheme();

  return (
    <button
      type="button"
      className={`theme-toggle ${showLabel ? 'theme-toggle--with-label' : ''} ${isDark ? 'is-dark' : 'is-light'} ${className}`}
      onClick={toggleTheme}
      aria-label={isDark ? 'Alternar para modo claro' : 'Alternar para modo escuro'}
      aria-pressed={isDark}
      title={isDark ? 'Alternar para modo claro' : 'Alternar para modo escuro'}
      style={style}
    >
      <span className="theme-toggle__icon">
        <Icon name={isDark ? 'sun' : 'moon'} size={17} />
      </span>
      {showLabel && (
        <span className="theme-toggle__label">
          {isDark ? 'Modo Claro' : 'Modo Escuro'}
        </span>
      )}
    </button>
  );
}
