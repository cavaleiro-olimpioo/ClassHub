import { useEffect, useMemo, useState } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import Icon from './Icon.jsx';
import ThemeToggle from './ThemeToggle.jsx';
import { clearSession, getSession } from '../lib/session.js';
import { menuFor, SECTION_LABEL, titleForPath } from '../lib/menu.js';
import { firstName, greetingFor, initials, longDateBR } from '../lib/format.js';
import '../styles/index.css';

/**
 * Layout das telas internas: sidebar fixa, topbar com breadcrumb e
 * area de conteudo. Equivale ao shell montado por `Auth.renderAppShell()`.
 */
export default function AppShell() {
  const location = useLocation();
  const navigate = useNavigate();
  const [session] = useState(() => getSession());
  const [menuOpen, setMenuOpen] = useState(false);

  // Fecha o menu lateral ao navegar (relevante no mobile)
  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  const items = useMemo(() => menuFor(session?.perfil), [session?.perfil]);
  const title = useMemo(() => titleForPath(session?.perfil, location.pathname), [session?.perfil, location.pathname]);

  // Data/hora do topo
  const [now, setNow] = useState(() => new Date());
  useEffect(() => {
    const timer = setInterval(() => setNow(new Date()), 60_000);
    return () => clearInterval(timer);
  }, []);

  function handleLogout() {
    clearSession();
    navigate('/login', { replace: true });
  }

  if (!session) return null;

  return (
    <div className="app-shell">
      <button
        type="button"
        className={`sidebar-overlay ${menuOpen ? 'is-open' : ''}`}
        onClick={() => setMenuOpen(false)}
        aria-label="Fechar menu"
        tabIndex={menuOpen ? 0 : -1}
      />

      <aside className={`sidebar ${menuOpen ? 'is-open' : ''}`}>
        <div className="sidebar-brand">
          <span className="sidebar-brand__mark">
            <Icon name="school" size={19} strokeWidth={2} />
          </span>
          <span>ClassHub</span>
        </div>

        <div className="sidebar-section">{SECTION_LABEL[session.perfil] || 'MENU'}</div>

        <nav className="sidebar-nav" aria-label="Navegação principal">
          <ul>
            {items.map((item) => (
              <li key={item.path}>
                <NavLink
                  to={item.path}
                  end={item.end}
                  className={({ isActive }) => `sidebar-link ${isActive ? 'is-active' : ''}`}
                >
                  <Icon name={item.icon} size={18} />
                  <span>{item.label}</span>
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <span className="avatar">{initials(session.nome)}</span>
            <span className="sidebar-user__meta">
              <span className="sidebar-user__name">{firstName(session.nome)}</span>
              <span className="sidebar-user__role" style={{ display: 'block' }}>
                {session.perfil}
              </span>
            </span>
          </div>
          <button
            type="button"
            className="btn btn--sm btn--block"
            style={{ marginTop: 10, background: 'rgba(255,255,255,.08)', color: '#dfe5f7' }}
            onClick={handleLogout}
          >
            <Icon name="logout" size={15} />
            Sair
          </button>
        </div>
      </aside>

      <div className="app-main">
        <header className="topbar">
          <button
            type="button"
            className="topbar__toggle"
            onClick={() => setMenuOpen((v) => !v)}
            aria-label="Alternar menu"
            aria-expanded={menuOpen}
          >
            <Icon name={menuOpen ? 'x' : 'menu'} size={18} />
          </button>

          <nav className="breadcrumb" aria-label="Trilha de navegação">
            <span>Portal do {session.perfil === 'ALUNO' ? 'Aluno' : session.perfil === 'PROFESSOR' ? 'Professor' : 'Admin'}</span>
            <span className="breadcrumb__sep">›</span>
            <span className="breadcrumb__current">{title}</span>
          </nav>

          <div className="topbar__spacer" />

          <ThemeToggle />

          <div className="topbar__hello">
            <div className="topbar__greeting">
              {greetingFor(now)}, {firstName(session.nome)}
            </div>
            <div className="topbar__date">Hoje, {longDateBR(now)}</div>
          </div>

          <span className="avatar" title={session.nome}>
            {initials(session.nome)}
          </span>
        </header>

        <main className="page">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
